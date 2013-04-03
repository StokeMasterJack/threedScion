package c3i.repo.server;

import c3i.featureModel.shared.FeatureModel;
import c3i.featureModel.shared.UnknownVarCodeException;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.common.SeriesKey;
import c3i.imageModel.shared.ImFeature;
import c3i.imageModel.shared.ImFeatureOrPng;
import c3i.imageModel.shared.ImLayer;
import c3i.imageModel.shared.ImNodeType;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.ImageModel;
import c3i.imageModel.shared.PngShortSha;
import c3i.imageModel.shared.SrcPng;
import c3i.imageModel.shared.ViewLiftSpec;
import c3i.repo.server.vnode.VNode;
import com.google.common.base.Preconditions;
import org.eclipse.jgit.lib.ObjectId;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;


public class ImageModelBuilder {

    private final FeatureModel featureModel;
    private final VNode seriesVDir;

    public ImageModelBuilder(FeatureModel featureModel, VNode seriesVDir) {
        Preconditions.checkNotNull(seriesVDir);
        this.featureModel = featureModel;
        this.seriesVDir = seriesVDir;
    }

    public ImageModel buildImageModel() {
        List<VNode> viewDirs = seriesVDir.getChildNodes();
        assert viewDirs != null;
        assert viewDirs.size() > 0;
        List<ImView> imViews = createImViewsFromSeriesDir(seriesVDir);


        SeriesKey seriesKey = featureModel.getKey();
        return new ImageModel(seriesVDir.getDepth(), imViews, seriesKey);
    }


    private static String viewSortWeight(String viewName) {
        if (viewName.equals("exterior")) return "0";
        if (viewName.equals("interior")) return "1";
        return "2" + viewName;
    }

    private List<ImView> createImViewsFromSeriesDir(VNode seriesDir) {
        List<VNode> viewDirs = seriesDir.getChildNodes();
        assert viewDirs != null;

        Collections.sort(viewDirs, new Comparator<VNode>() {
            @Override
            public int compare(VNode o1, VNode o2) {
                String n1 = o1.getName();
                String n2 = o2.getName();
                String s1 = viewSortWeight(n1);
                String s2 = viewSortWeight(n2);
                return s1.compareTo(s2);
            }
        });

        List<ImView> imViews = new ArrayList<ImView>();
        for (int i = 0; i < viewDirs.size(); i++) {
            VNode viewDir = viewDirs.get(i);
            ImView view = createImViewFromViewDir(viewDir, i);
            imViews.add(view);
        }


        return imViews;
    }

    private ImView createImViewFromViewDir(VNode viewDir, int viewIndex) {
        ViewHelper viewHelper = new ViewHelper(viewDir);
        List<ImLayer> imLayers = createImLayersFromViewDir(viewHelper);
        return new ImView(viewDir.getDepth(), viewHelper.viewName, viewIndex, imLayers, viewHelper.liftSpec);
    }

    public class ViewHelper {
        String viewName;
        VNode viewDir;
        VNode liftFile;

        ViewLiftSpec liftSpec;
        String liftLayerList;

        public ViewHelper(VNode viewDir) {
            this.viewDir = viewDir;
            viewName = viewDir.getName();
            liftFile = viewDir.getChildNode("lift.txt");

            if (liftFile == null) {
                liftSpec = null;
            } else {
                Properties p = liftFile.readFileAsProperties();
                liftSpec = parse(p, featureModel);
                liftLayerList = p.getProperty("layers");
            }


        }

        ViewLiftSpec parse(Properties properties, FeatureModel vars) {
            String varCode = properties.getProperty("trigger-feature");
            String var = vars.getVar(varCode).toString();
            int deltaY = Integer.parseInt(properties.getProperty("delta-y"));
            return new ViewLiftSpec(var, deltaY);
        }

        public boolean isLayerLifter(String layerName) {
            if (liftLayerList == null) return false;
            return liftLayerList.contains(layerName);
        }

    }

    private List<ImLayer> createImLayersFromViewDir(ViewHelper viewHelper) {
        List<VNode> layerDirs = viewHelper.viewDir.getChildNodes();

        if (layerDirs == null) {
            throw new IllegalStateException(viewHelper.viewName + " has no child dirs");
        }

        List<ImLayer> imLayers = new ArrayList<ImLayer>();
        for (VNode layerDir : layerDirs) {
            if (layerDir.isDirectory()) {
                ImLayer layer = createImLayerFromLayerDir(layerDir, viewHelper);
                imLayers.add(layer);
            }

        }
        return imLayers;
    }

    private ImLayer createImLayerFromLayerDir(@Nonnull VNode layerDir, ViewHelper viewHelper) {
        String layerName = layerDir.getName();

        boolean isLiftLayer;
        if (viewHelper.liftSpec != null && viewHelper.isLayerLifter(layerName)) {
            isLiftLayer = true;
        } else {
            isLiftLayer = false;
        }

        List<ImFeatureOrPng> featuresAndOrPngs = createFeaturesAndOrPngsFromDir(ImNodeType.LAYER_DIR, layerDir);
        return new ImLayer(layerDir.getDepth(), layerName, featuresAndOrPngs, isLiftLayer);
    }

    /**
     *
     * dirNode could be any valid png or feature parent: i.e. LayerDir or FeatureDir
     * dirNode's children will either be FeatureDir or Png
     *
     * if dirNode has no children, an empty list is returned
     *
     * @param
     * @return a list including Pngs and or FeatureDirs
     */
    @Nonnull
    private List<ImFeatureOrPng> createFeaturesAndOrPngsFromDir(ImNodeType nodeType, VNode layerDirOrFeatureDir) {
        List<ImFeatureOrPng> featuresAndOrPngs = new ArrayList<ImFeatureOrPng>();
        if (!layerDirOrFeatureDir.hasChildNodes()) {
            return featuresAndOrPngs;
        }
        List<VNode> childNodes = layerDirOrFeatureDir.getChildNodes();
        for (VNode vNode : childNodes) {
            if (vNode.isDirectory()) {
                //must be a FeatureDir
                ImFeature imFeature = createImFeatureFromFeatureDir(vNode);
                featuresAndOrPngs.add(imFeature);
            } else {
                //must be a png
                SrcPng imPng = createImPngFromPngFile(vNode);
                featuresAndOrPngs.add(imPng);
            }
        }
        return featuresAndOrPngs;
    }

    public static final String VERSION_PREFIX = "vr_1_";
    public static final String PNG_SUFFIX = ".png";

    /**
     * vr_1_04.png
     * 04.png
     * 04
     */
    private SrcPng createImPngFromPngFile(VNode pngVFile) {
        String name = pngVFile.getName();
        name = name.replace(VERSION_PREFIX, "");
        String sAngle = name.replace(PNG_SUFFIX, "");

//        boolean empty = pngVFile.isEmptyPng();

        int angle;
        try {
            angle = Integer.parseInt(sAngle);
        } catch (NumberFormatException e) {
            System.out.println(pngVFile.getName());
            System.out.println(pngVFile.getFullPath());
            throw new RuntimeException(e);
        }

        int depth = pngVFile.getDepth();
        ObjectId pngSha = pngVFile.getFullSha();

        PngShortSha shortSha = pngSha == null ? null : new PngShortSha(pngSha.getName());

        return new SrcPng(depth, angle, shortSha);
    }


    private ImFeature createImFeatureFromFeatureDir(VNode featureDir) {
        String featureCode = featureDir.getName();
        String  var = null;
        try {
            var = featureModel.getVar(featureCode).toString();
        } catch (UnknownVarCodeException e) {
            System.out.println("Feature [" + featureCode + "] was found in imageModel tree here [" + featureDir.getFullPath() + "]");
            System.out.println(e.getMessage());
            System.out.println("Bad Feature Code in png folders");
        }
        List<ImFeatureOrPng> childNodes = createFeaturesAndOrPngsFromDir(ImNodeType.FEATURE_DIR, featureDir);
        return new ImFeature(featureDir.getDepth(), var, childNodes);
    }

    private final static Logger log = Logger.getLogger("c3i");

}
