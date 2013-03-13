package c3i.core.threedModel.shared;

import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.shared.Assignments;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.ImageModel;
import c3i.imageModel.shared.Slice;
import c3i.imageModel.shared.ViewKeyOld;
import c3i.imageModel.shared.ViewSlice;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ThreedModel {

    private final FeatureModel featureModel;
    private final ImageModel imageModel;

    private final SeriesKey seriesKey;

    public final Slice heroSlice;


    public ThreedModel(FeatureModel featureModel, ImageModel imageModel) {
        assert featureModel != null;
        assert imageModel != null;

        this.featureModel = featureModel;
        this.imageModel = imageModel;

        this.seriesKey = featureModel.getSeriesKey();


        heroSlice = new Slice("exterior", 2);

    }

    public FeatureModel getFeatureModel() {
        return featureModel;
    }

    public ImageModel getImageModel() {
        return imageModel;
    }

    public FixedPicks fixupRaw(Iterable<String> picksRaw) {
        return featureModel.fixupRaw(picksRaw);
    }

    public FixedPicks fixup(Set<Var> picks) {
        return featureModel.fixup(picks);
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


    public Collection<Object> getPngVarsForSlice1(Slice slice) {
        String viewName = slice.getView();
        ImView view = imageModel.getView(viewName);
        return view.getPngVars(slice.getAngle());
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

    public void setSubSeries(SubSeries subSeries) {
        featureModel.setSubSeries(subSeries);
    }

    public String getDisplayName(FixedPicks picks) {
        if (picks == null) {
            return getFeatureModel().getDisplayName();
        }
        Assignments assignments = picks.getAssignments();
        Var displayName = getFeatureModel().resolveVar("displayName");
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

    public static Set<Var> objectSetToVarSet(Set<Object> pngVars) {
        HashSet<Var> vars = new HashSet<Var>();
        for (Object pngVar : pngVars) {
            Var var = (Var) pngVar;
            vars.add(var);
        }
        return vars;
    }


}
