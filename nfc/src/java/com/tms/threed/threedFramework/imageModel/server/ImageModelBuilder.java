package com.tms.threed.threedFramework.imageModel.server;

import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.featureModel.shared.UnknownVarCodeException;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.imageModel.shared.ImFeature;
import com.tms.threed.threedFramework.imageModel.shared.ImFeatureOrPng;
import com.tms.threed.threedFramework.imageModel.shared.ImLayer;
import com.tms.threed.threedFramework.imageModel.shared.ImNodeType;
import com.tms.threed.threedFramework.imageModel.shared.ImPng;
import com.tms.threed.threedFramework.imageModel.shared.ImSeries;
import com.tms.threed.threedFramework.imageModel.shared.ImView;
import com.tms.threed.threedFramework.imageModel.shared.PngShortSha;
import com.tms.threed.threedFramework.threedCore.shared.SeriesInfo;
import com.tms.threed.threedFramework.threedCore.shared.SeriesInfoBuilder;
import com.tms.threed.threedFramework.util.vnode.server.VNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.lib.ObjectId;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ImageModelBuilder {

    private final FeatureModel featureModel;
    private final VNode seriesVDir;

    private final BlinkChecker blinkChecker;

    public ImageModelBuilder(FeatureModel featureModel, VNode seriesVDir,BlinkChecker blinkChecker) {
        this.featureModel = featureModel;
        this.seriesVDir = seriesVDir;
        this.blinkChecker = blinkChecker;
    }

    public ImSeries buildImageModel() {
        List<VNode> viewDirs = seriesVDir.getChildNodes();
        assert viewDirs != null;
        assert viewDirs.size() > 0;
        List<ImView> imViews = createImViewsFromSeriesDir(seriesVDir);

        SeriesInfo seriesInfo = SeriesInfoBuilder.createSeriesInfo(featureModel.getSeriesKey());

        return new ImSeries(seriesVDir.getDepth(), imViews, seriesInfo);
    }



    private List<ImView> createImViewsFromSeriesDir(VNode seriesDir) {
        List<VNode> viewDirs = seriesDir.getChildNodes();

        assert viewDirs != null;
        List<ImView> imViews = new ArrayList<ImView>();
        for (VNode viewDir : viewDirs) {
            ImView view = createImViewFromViewDir(viewDir);
            imViews.add(view);
        }


        return imViews;
    }

    private ImView createImViewFromViewDir(VNode viewDir) {
        String name = viewDir.getName();
        List<ImLayer> imLayers = createImLayersFromViewDir(viewDir);
        return new ImView(viewDir.getDepth(), name, imLayers);
    }

    private List<ImLayer> createImLayersFromViewDir(VNode viewDir) {
        List<VNode> layerDirs = viewDir.getChildNodes();

        if (layerDirs == null) {
            throw new IllegalStateException(viewDir.getName() + " has no child dirs");
        }

        List<ImLayer> imLayers = new ArrayList<ImLayer>();
        for (VNode layerDir : layerDirs) {
            ImLayer layer = createImLayerFromLayerDir(layerDir);
            imLayers.add(layer);
        }
        return imLayers;
    }

    private ImLayer createImLayerFromLayerDir(@Nonnull VNode layerDir) {
        String layerName = layerDir.getName();
        List<ImFeatureOrPng> featuresAndOrPngs = createFeaturesAndOrPngsFromDir(ImNodeType.LAYER_DIR, layerDir);
        return new ImLayer(layerDir.getDepth(), layerName, featuresAndOrPngs);
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
                ImPng imPng = createImPngFromPngFile(vNode);
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
    private ImPng createImPngFromPngFile(VNode pngVFile) {
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

        PngShortSha shortSha = pngSha==null?null:new PngShortSha(pngSha.getName());
        boolean blink = blinkChecker.isBlinkPng(shortSha);

        return new ImPng(depth, angle, shortSha,blink);
    }



    private ImFeature createImFeatureFromFeatureDir(VNode featureDir) {
        String featureCode = featureDir.getName();
        Var var = null;
        try {
            var = featureModel.get(featureCode);
        } catch (UnknownVarCodeException e) {
            System.out.println("Feature [" + featureCode + "] was found in imageModel tree here [" + featureDir.getFullPath() + "]");
            System.out.println(e.getMessage());
            System.out.println("Bad Feature Code in png folders");
        }
        List<ImFeatureOrPng> childNodes = createFeaturesAndOrPngsFromDir(ImNodeType.FEATURE_DIR, featureDir);
        return new ImFeature(featureDir.getDepth(), var, childNodes);
    }

    private static final Log log = LogFactory.getLog(ImageModelBuilder.class);

}
