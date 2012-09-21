package c3i.core.threedModel.shared;

import c3i.core.imageModel.shared.PngSpec;
import com.google.common.collect.ImmutableSet;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.lang.shared.Path;
import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.shared.Assignments;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.imageModel.shared.ImLayer;
import c3i.core.imageModel.shared.ImSeries;
import c3i.core.imageModel.shared.ImView;
import c3i.core.imageModel.shared.SrcPng;
import c3i.core.imageModel.shared.ViewKey;
import c3i.core.imageModel.shared.ViewKeyOld;
import c3i.core.imageModel.shared.ViewSlice;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

//import threed.core.imageModel.shared.slice.ImageSlice;


public class ThreedModel {

    private final FeatureModel featureModel;
    private final ImSeries imageModel;

    private final SeriesKey seriesKey;

//    private final Map<Slice, ImageSlice> imageSliceMap = new HashMap<Slice, ImageSlice>();

//    private final



    public final Slice heroSlice;



    public ThreedModel(FeatureModel featureModel, ImSeries imageModel) {
        assert featureModel != null;
        assert imageModel != null;

        this.featureModel = featureModel;
        this.imageModel = imageModel;

        this.seriesKey = featureModel.getSeriesKey();


        heroSlice = new Slice("exterior", 2);

    }

//    public SeriesInfo getSeriesInfo() {
//        return imageModel.getSeriesInfo();
//    }

    public FeatureModel getFeatureModel() {
        return featureModel;
    }

    public ImSeries getImageModel() {
        return imageModel;
    }

    public FixedPicks fixupRaw(Iterable<String> picksRaw) {
        return featureModel.fixupRaw(picksRaw);
    }

    public FixedPicks fixup(Set<Var> picks) {
        return featureModel.fixup(picks);
    }


    @Nullable
    public Path getBlinkPngUrl(Slice slice, FixedPicks picks, Var blinkFeature, Path repoBaseUrl) {
        assert picks != null : "Picks is required";
        assert slice != null;
        assert blinkFeature != null;

        ImView view = getView(slice.getView());
        PngSpec accessoryPng = view.getAccessorySrcPng(slice.getAngle(), picks, blinkFeature);

        Console.log("accessoryPng = " + accessoryPng);

        if (accessoryPng == null) {
            return null;
        }

        String urlSegment = accessoryPng.serializeToUrlSegment();

        Path threedBaseUrl = view.getSeries().getThreedBaseUrl(repoBaseUrl);
        Path blinkBase = threedBaseUrl.append("blink");
        return blinkBase.append(urlSegment).appendName(".png");
    }


    public ImView getView(ViewKeyOld viewKey) {
        return imageModel.getView(viewKey);
    }

    public ImView getView(String viewName) {
        return imageModel.getView(viewName);
    }

    public ImView getView(int viewIndex) {
        return imageModel.getView(viewIndex);
    }

    public boolean equalsHeavy(Object obj) {

        if (this == obj) return true;
        if (obj == null) return false;
        if (obj.getClass() != ThreedModel.class) return false;
        ThreedModel that = (ThreedModel) obj;

        boolean fmsEqual = this.featureModel.equals(that.featureModel);
        boolean imsEqual = this.imageModel.equals(that.imageModel);

        return fmsEqual && imsEqual;
    }

//    public Slice getInitialSlice() {
//        return getImageModel().getInitialViewState();
//    }
//
//    public ImView getInitialView() {
//        return getImageModel().getInitialView();
//    }
//
//    public ViewKey getInitialViewKey() {
//        return getImageModel().getInitialView().getViewKey();
//    }

    public int getInitialViewIndex() {
        return imageModel.getInitialView().getIndex();
    }

    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

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

    public ViewSlice getViewSlice(String viewName, int angle) {
        return getImageModel().getViewSlice(viewName, angle);
    }


    public ViewSlice getViewSlice(Slice slice) {
        ImView view = getImageModel().getView(slice.getView());
        return view.getViewSlice(slice.getAngle());
    }

    public Slice getFirstSlice() {
        return getImageModel().getFirstSlice();
    }

    public Slice getSlice(String viewName, int angle) {
        return getImageModel().getSlice(viewName, angle);
    }


    public int getSliceCount() {
        return imageModel.getSliceCount();
    }

//    public ViewKeyOld getViewKey(String viewName) {
//        ViewKeyOld[] viewKeys = imageModel.getViewKeys();
//        for (ViewKeyOld viewKey : viewKeys) {
//            if (viewKey.getName().equals(viewName)) {
//                return viewKey;
//            }
//        }
//        throw new IllegalArgumentException("No view named [" + viewName + " for this threedMode[" + getSeriesKey() + "]");
//    }

//    public ViewKeyOld getExteriorViewKey() {
//        return exteriorViewKey;
//    }
//
//    public ViewKeyOld getInteriorViewKey() {
//        return interiorViewKey;
//    }

    public Collection<Var> getPngVarsForSlice1(Slice slice) {
        String viewName = slice.getView();
        ImView view = imageModel.getView(viewName);
        return view.getPngVars(slice.getAngle());
    }

//    public Collection<Var> getPngVarsForSlice2(Slice slice) {
//        ImageSlice imageSlice = getImageSlice(slice);
//        return imageSlice.getJpgVars();
//    }


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

    public void printBrief() {
        int fmVarCount = featureModel.size();
        System.out.println("fmVarCount = " + fmVarCount);
        int sliceCount = imageModel.getSliceCount();
        System.out.println("sliceCount = " + sliceCount);

    }

    public void printFirstFivePngs() {
        Slice slice = imageModel.getFirstSlice();
        ImView view = imageModel.getView(slice.getViewName());
        ImLayer imLayer = view.getLayers().get(0);
        SrcPng imPng = imLayer.getChildNodes().get(0).asPng();
        System.out.println(imPng.toString());


    }

    public void setSubSeries(SubSeries subSeries) {
        featureModel.setSubSeries(subSeries);
    }

    public String getDisplayName(FixedPicks picks) {
        if (picks == null) {
            return getFeatureModel().getDisplayName();
        }
        Assignments assignments = picks.getAssignments();
        Var displayName = getFeatureModel().getVarOrNull("displayName");
        if (displayName == null) {
            return getFeatureModel().getDisplayName();
        }
        List<Var> childVars = displayName.getChildVars();
        if (childVars == null) {
            return getFeatureModel().getDisplayName();
        }
        for (Var childVar : childVars) {
            if (assignments.isTrue(childVar)) {
                return childVar.getName();
            }
        }
        return getFeatureModel().getDisplayName();
    }

    public ImmutableSet<Var> varCodesToVars(Iterable<String> picksRaw) {
        return featureModel.varCodesToVars(picksRaw);
    }

    public List<ImView> getViews() {
        return imageModel.getViews();
    }


    public int getViewCount() {
        return imageModel.getViewCount();
    }


}
