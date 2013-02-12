package c3i.core.imageModel.shared;

import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.shared.boolExpr.Var;
import smartsoft.util.shared.Path;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import threed.core.imageModel.shared.slice.ImageSlice;

public class ImSeries extends ImNodeBase implements IsParent<ImView>, IsRoot {

    private final SeriesKey seriesKey;
    private final List<ImView> imViews;

    public ImSeries(int depth, List<ImView> imViews, SeriesKey seriesKey) {
        super(depth);
        this.seriesKey = seriesKey;
        this.imViews = imViews;

        for (ImView view : imViews) {
            view.initParent(this);
        }
    }


    public Slice getSlice(String viewName, int angle) {
        return getView(viewName).getSlice(angle);
    }

    @Override
    public String getName() {
        return seriesKey.getName();
    }

    public ImView getView(int i) {
        return imViews.get(i);
    }

    public ImView getView(ViewKeyOld viewKey) {
        return getView(viewKey.getName());
    }

    public boolean isValidViewName(String viewName) {
        try {
            getView(viewName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public ImView getView(String viewName) {
        for (ImView view : imViews) {
            if (view.is(viewName)) {
                return view;
            }
        }
        throw new IllegalArgumentException("No such view[" + viewName + "]");
    }

    public List<ImView> getViews() {
        return imViews;
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public boolean isSeries() {
        return true;
    }

    public ImView getExteriorView() {
        return getView("exterior");
    }

    public ImView getInteriorView() {
        return getView("interior");
    }

    @Override
    public Path getLocalPath() {
        String y = seriesKey.getYear() + "";
        String n = seriesKey.getName();
        Path localPath = new Path(n, y);
        return localPath;
    }

    @Override
    public List<ImView> getChildNodes() {
        return imViews;
    }

    @Override
    public boolean containsAngle(int angle) {
        List<ImView> views = getViews();
        if (views == null) return false;
        for (int i = 0; i < views.size(); i++) {
            if (views.get(i).containsAngle(angle)) return true;
        }
        return false;
    }

    public Set<Var> getVars() {
        HashSet<Var> vars = new HashSet<Var>();
        getVars(vars);
        return vars;
    }

    public void getVars(Set<Var> varSet) {
        for (int i = 0; i < imViews.size(); i++) {
            ImView imView = imViews.get(i);
            imView.getPngVars(varSet);
        }
    }

    public Set<SrcPng> getPngs() {
        HashSet<SrcPng> pngs = new HashSet<SrcPng>();
        for (ImView imView : imViews) {
            imView.populatePngSet(pngs);
        }
        return pngs;
    }

    public String getVarCode() {
        return "imageModel";
    }

    public void printSummary() {
        System.out.println("varCount: " + getVars().size());
        for (ImView view : imViews) {
            view.printSummary();
        }
        System.out.println();

    }


    public String summary() {
        return "vars: " + getVars().size() + "\t pngs[" + getPngs().size() + "]";
    }

//    public Slice getInitialViewState() {
//        return getSeriesInfo().getInitialViewState();
//
//        return new Slice(initialView.getName(), initialView.getInitialAngle());
//    }
//
//    public int getInitialViewIndex() {
//        return getSeriesInfo().getInitialViewKey().index;
//    }
//
//    public ImView getInitialView() {
//        int viewIndex = getInitialViewIndex();
//        return getView(viewIndex);
//    }

    public Path getThreedBaseUrl(Path repoBase) {
        if (repoBase == null) {
            //            throw new IllegalStateException("repoBase should not be null");
            repoBase = new Path();
        }
        SeriesKey seriesKey = getSeriesKey();
        String brand = seriesKey.getBrandKey().getKey();
        String seriesName = seriesKey.getName();
        int year = seriesKey.getYear();
        return repoBase.append(brand).append(seriesName).append(year).append("3d");
    }

    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    public ViewSlice getViewSlice(String viewName, int angle) {
        return getView(viewName).getViewSlice(angle);
    }

    public ViewSlice getImageSlice(Slice slice) {
        return getViewSlice(slice.getView(), slice.getAngle());
    }

    public int getVarCount() {
        return getVars().size();
    }

    public Slice getFirstSlice() {
        return new Slice(getViews().get(0).getName(), 1);
    }

//    public ViewKeyOld[] getViewKeys() {
//        return seriesInfo.getViewKeys();
//    }

    public int getSliceCount() {
        int t = 0;
        for (ImView view : imViews) {
            t += view.getAngleCount();
        }
        return t;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != ImSeries.class) return false;

        ImSeries that = (ImSeries) obj;

        boolean viewsEq = this.imViews.equals(that.imViews);

        return viewsEq;
    }

    public int getViewCount() {
        return imViews.size();
    }


    public ImView getInitialView() {
        return imViews.get(0);
    }
}
