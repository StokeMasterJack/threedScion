package com.tms.threed.threedFramework.threedModel.shared;

import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.featureModel.shared.FixResult;
import com.tms.threed.threedFramework.featureModel.shared.Fixer;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.imageModel.shared.IImageStack;
import com.tms.threed.threedFramework.imageModel.shared.ImLayer;
import com.tms.threed.threedFramework.imageModel.shared.ImPng;
import com.tms.threed.threedFramework.imageModel.shared.ImSeries;
import com.tms.threed.threedFramework.imageModel.shared.ImView;
import com.tms.threed.threedFramework.imageModel.shared.slice.ImageSlice;
import com.tms.threed.threedFramework.imageModel.shared.slice.Png;
import com.tms.threed.threedFramework.imageModel.shared.slice.SimplePicks;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.threedCore.shared.SeriesInfo;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;
import com.tms.threed.threedFramework.threedCore.shared.Slice;
import com.tms.threed.threedFramework.threedCore.shared.ViewKey;
import com.tms.threed.threedFramework.util.gwtUtil.client.Console;
import com.tms.threed.threedFramework.util.lang.shared.Path;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ThreedModel {

    //    private final SeriesId seriesId;
    private final FeatureModel featureModel;
    private final ImSeries imageModel;

    private final SeriesKey seriesKey;

    private final Map<Slice, ImageSlice> imageSliceMap = new HashMap<Slice, ImageSlice>();

    public final ViewKey exteriorViewKey;
    public final ViewKey interiorViewKey;

    public final Slice heroSlice;

    public ThreedModel(FeatureModel featureModel, ImSeries imageModel) {
        assert featureModel != null;
        assert imageModel != null;

        this.featureModel = featureModel;
        this.imageModel = imageModel;

        this.seriesKey = featureModel.getSeriesKey();

        exteriorViewKey = getViewKey(ViewKey.EXTERIOR);
        interiorViewKey = getViewKey(ViewKey.INTERIOR);

        heroSlice = new Slice(exteriorViewKey.getName(), 2);

        //create imageSlice map
//        for (ImView imView : imageModel.getViews()) {
//            String viewName = imView.getName();
//            int angleCount = imView.getAngleCount();
//            for (int angle = 1; angle <= angleCount; angle++) {
//
//                Slice slice = new Slice(imView.getViewKey(), angle);
//                ImageSlice imageSlice = imageModel.createImageSlice(slice);
////                System.out.println(imView.getName() + "\t angle: " + angle + ":\t" + imageSlice.getJpgVars());
//
//
////                if (viewName.equalsIgnoreCase("exterior") && angle == 6) {
////                    imageSlice.print();
////                }
//
//                imageSliceMap.put(slice, imageSlice);
//            }
//        }

    }

    public SeriesInfo getSeriesInfo() {
        return imageModel.getSeriesInfo();
    }

    public FeatureModel getFeatureModel() {
        return featureModel;
    }

    public ImSeries getImageModel() {
        return imageModel;
    }

    public FixResult fixupPicks1(Set<Var> picksRaw) {
        return Fixer.fix(featureModel, picksRaw);
    }

    public FixResult fixupPicks2(Set<String> picksRaw) {
        HashSet<Var> varSet = new HashSet<Var>();
        for (String varCode : picksRaw) {
            Var var = featureModel.getVarOrNull(varCode);
            if (var == null) {
                continue;
            }
            varSet.add(var);
        }
        return fixupPicks1(varSet);
    }

//    public Path getJpg(ViewKey viewKey, Angle angle, Set<String> rawPicks) {
//        Picks picks = fixupPicks(rawPicks);
//        return imageModel.getJpg(viewKey, angle, picks);
//    }
//
//    public Path getJpg(ViewKey viewKey, Angle angle, VarPicksSnapshot picksRaw) {
//        Picks picks = fixupPicks(picksRaw.getFeatureSet());
//        return imageModel.getJpg(viewKey, angle, picks);
//    }

    public IImageStack getImageStack(String viewName, int angle, SimplePicks picks) {
        assert viewName != null;
        assert picks != null;
        return getImageStack(viewName, angle, picks, JpgWidth.W_STD);
    }

    public IImageStack getImageStack(String viewName, int angle, SimplePicks picks, JpgWidth jpgWidth) {
        assert viewName != null;
        assert picks != null;
        return imageModel.getView(viewName).getImageStack(picks, angle, jpgWidth);
    }

    public IImageStack getImageStack(Slice slice, SimplePicks picks, JpgWidth jpgWidth) {
        assert slice != null;
        assert picks != null;

        ImView view = imageModel.getView(slice.getViewName());
        return view.getImageStack(picks, slice.getAngle(), jpgWidth);
    }

//    public IImageStack getImageStack(String viewName, int angle, SimplePicks picks) {
//        assert viewName != null;
//        assert picks != null;
//
//        Slice slice = this.getSlice(viewName, angle);
//
//        return getImageStack(slice, picks);
//    }
//
//    public IImageStack getImageStack(Slice slice, SimplePicks picks) {
//        assert slice != null;
//        assert picks != null;
//
//        ImageSlice imageSlice = imageSliceMap.get(slice);
//        Path threedBaseUrl = imageModel.getThreedBaseUrl();
//        return imageSlice.computeImgStack(picks, threedBaseUrl);
//    }


    @Nullable
    public Path getBlinkPngUrl(Slice slice, SimplePicks picks, Var blinkFeature) {
        assert picks != null : "Picks is required";
        assert slice != null;
        assert blinkFeature != null;

        ImView view = getView(slice.getView());
        ImPng accessoryPng = view.getAccessoryPng(slice.getAngle(), picks, blinkFeature);

        Console.log("accessoryPng = " + accessoryPng);

        if (accessoryPng == null) {
            return null;
        }

        String pngShortSha = accessoryPng.getShortSha();

        Path threedBaseUrl = view.getSeries().getThreedBaseUrl();
        Path blinkBase = threedBaseUrl.append("blink");
        return blinkBase.append(pngShortSha).appendName(".png");
    }


    public ImView getView(ViewKey viewKey) {
        return imageModel.getView(viewKey);
    }

    public ImView getView(String viewName) {
        return imageModel.getView(viewName);
    }

    @Override
    public boolean equals(Object obj) {
//        throw new UnsupportedOperationException();
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj.getClass() != ThreedModel.class) return false;
        ThreedModel that = (ThreedModel) obj;

        boolean fmsEqual = this.featureModel.equals(that.featureModel);
        boolean imsEqual = this.imageModel.equals(that.imageModel);

        return fmsEqual && imsEqual;
    }

    public Slice getInitialSlice() {
        return getImageModel().getInitialViewState();
    }

    public SeriesKey getSeriesKey() {
        return seriesKey;
    }


//    public Jpg getJpg(Slice state, SimplePicks picks) {
//        IImageStack imageStack = getImageStack(state.getViewName(), state.getAngle(), picks);
//        return imageStack.getJpg();
//    }

    public void print() {

        System.out.println("===============");
        System.out.println("Feature Model");
        System.out.println("===============");
        featureModel.getRootVar().printVarTree();

        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println("===============");
        System.out.println("Image Model");
        System.out.println("===============");
        imageModel.printTree();
    }

    public ImageSlice getImageSlice(String viewName, int angle) {
        return getImageModel().simplify(viewName, angle);
    }

    public ImageSlice getImageSlice(Slice slice) {
        ImageSlice imageSlice = imageSliceMap.get(slice);
        if (imageSlice == null) {
            imageSlice = imageModel.createImageSlice(slice);
            imageSliceMap.put(slice, imageSlice);
        }
        return imageSlice;
    }


    private void showPngsWithNephews() {
        ViewKey[] viewKeys = imageModel.getSeriesInfo().getViewKeys();
        for (ViewKey viewKey : viewKeys) {
            int[] angles = viewKey.getAngleValues();
            for (int angle : angles) {
                ImageSlice im = getImageSlice(viewKey.getName(), angle);

                for (Png uncle : im.getNonZPngsWithNephews()) {
                    System.out.println("Uncle: " + uncle + "  Rule: " + uncle.getImplicant());
                    Collection<Png> nephews = uncle.getNephews();
                    for (Png nephew : nephews) {
                        System.out.println("Nephu: " + nephew + "  Rule: " + nephew.getImplicant());
                    }
                    System.out.println();
                    System.out.println();
                }

            }
        }
    }


    public Slice getFirstSlice() {
        return getImageModel().getFirstSlice();
    }

    public Slice getSlice(String viewName, int angle) {
        return getImageModel().getSlice(viewName, angle);
    }

    public ViewKey[] getViewKeys() {
        return getImageModel().getViewKeys();
    }

    public int getSliceCount() {
        return imageModel.getSliceCount();
    }

    public ViewKey getViewKey(String viewName) {
        ViewKey[] viewKeys = imageModel.getViewKeys();
        for (ViewKey viewKey : viewKeys) {
            if (viewKey.getName().equals(viewName)) {
                return viewKey;
            }
        }
        throw new IllegalArgumentException("No view named [" + viewName + " for this threedMode[" + getSeriesKey() + "]");
    }

    public ViewKey getExteriorViewKey() {
        return exteriorViewKey;
    }

    public ViewKey getInteriorViewKey() {
        return interiorViewKey;
    }

    public Collection<Var> getPngVarsForSlice1(Slice slice) {
        String viewName = slice.getView();
        ImView view = imageModel.getView(viewName);
        ImView copy = view.copy(slice.getAngle());
        return copy.getJpgVars();
    }

    public Collection<Var> getPngVarsForSlice2(Slice slice) {
        ImageSlice imageSlice = getImageSlice(slice);
        return imageSlice.getJpgVars();
    }


    public List<Slice> getSlices() {
        ArrayList<Slice> slices = new ArrayList<Slice>();
        List<ImView> views = getImageModel().getViews();
        for (ImView view : views) {
            for (int angle = 1; angle <= view.getAngleCount(); angle++) {
                slices.add(new Slice(view.getName(), angle));
            }
        }
        return slices;
    }


    public void setRepoBaseUrl(Path repoBaseUrl) {
        imageModel.setRepoBaseUrl(repoBaseUrl);
    }

    public void printBrief() {
        int fmVarCount = featureModel.size();
        System.out.println("fmVarCount = " + fmVarCount);
        int sliceCount = imageModel.getSliceCount();
        System.out.println("sliceCount = " + sliceCount);

    }

    public void printFirstFivePngs() {
        Slice slice = imageModel.getInitialViewState();
        ImView view = imageModel.getView(slice.getViewName());
        ImLayer imLayer = view.getLayers().get(0);
        ImPng imPng = imLayer.getChildNodes().get(0).asPng();
        System.out.println(imPng.toString());


    }
}
