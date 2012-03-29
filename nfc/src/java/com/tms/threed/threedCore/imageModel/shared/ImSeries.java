package com.tms.threed.threedCore.imageModel.shared;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedCore.imageModel.shared.slice.ImageSlice;
import com.tms.threed.threedCore.threedModel.shared.*;
import smartsoft.util.lang.shared.Path;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImSeries extends ImNodeBase implements IsParent<ImView>, IsRoot {

    private final SeriesInfo seriesInfo;
    private final List<ImView> imViews;

    private Path repoBaseUrl;

    public ImSeries(int depth, List<ImView> imViews, SeriesInfo seriesInfo) {
        super(depth);
        this.seriesInfo = seriesInfo;
        this.imViews = imViews;

        for (ImView view : imViews) {
            view.initParent(this);
        }
    }

    /**
     * @param repoBaseUrl to be set client-side
     */
    public void setRepoBaseUrl(Path repoBaseUrl) {
        this.repoBaseUrl = repoBaseUrl;
    }

    public Slice getSlice(String viewName, int angle) {
        return getView(viewName).getSlice(angle);
    }

    @Override
    public String getName() {
        return seriesInfo.getSeriesName();
    }

    public SeriesInfo getSeriesInfo() {
        return seriesInfo;
    }

    public ImView getView(int i) {
        return imViews.get(i);
    }

    public ImView getView(ViewKey viewKey) {
        for (ImView view : imViews) {
            if (view.is(viewKey)) return view;
        }
        throw new IllegalArgumentException("No such view[" + viewKey + "]");
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
        ViewKey viewKey = seriesInfo.getExterior();
        return getView(viewKey);
    }

    public ImView getInteriorView() {
        ViewKey viewKey = seriesInfo.getInterior();
        return getView(viewKey);
    }

    @Override
    public Path getLocalPath() {
        String y = seriesInfo.getYear() + "";
        String n = seriesInfo.getSeriesName();
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
            imView.getJpgVars(varSet);
        }
    }

    public Set<ImPng> getPngs() {
        HashSet<ImPng> pngs = new HashSet<ImPng>();
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

    public Slice getInitialViewState() {
        return getSeriesInfo().getInitialViewState();
    }

    public ImView getInitialView() {
        ViewKey initialViewKey = getSeriesInfo().getInitialView();
        return getView(initialViewKey);
    }

    public Path getThreedBaseUrl() {
        if (repoBaseUrl == null) {
            throw new IllegalStateException("repoBaseUrl should not be null");
        }
        assert repoBaseUrl != null;
        SeriesKey seriesKey = getSeriesKey();
        String name = seriesKey.getName();
        int year = seriesKey.getYear();


        return repoBaseUrl.append(name).append(year + "").append("3d");
    }

    public Path getThreedBaseJpgUrl(JpgWidth jpgWidth) {
        return getThreedBaseUrl().append("jpgs").append(jpgWidth.getUrlPathFolderName());
    }

    public Path getThreedBasePngUrl() {
        return getThreedBaseUrl().append("pngs");
    }

    public SeriesKey getSeriesKey() {
        return getSeriesInfo().getSeriesKey();
    }

    public Path getRepoBaseUrl() {
        return repoBaseUrl;
    }

    public ImageSlice simplify(String viewName, int angle) {
        return getView(viewName).createSlice(angle);
    }

    public ImageSlice createImageSlice(Slice slice) {
        return getView(slice.getViewName()).createSlice(slice.getAngle());
    }

    public int getVarCount() {
        return getVars().size();
    }

    public Slice getFirstSlice() {
        return new Slice(getViews().get(0).getViewKey().getName(), 1);
    }

    public ViewKey[] getViewKeys() {
        return seriesInfo.getViewKeys();
    }

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

        boolean repoBaseEq = repoBaseEq(that.repoBaseUrl);
        boolean viewsEq = this.imViews.equals(that.imViews);

        return repoBaseEq && viewsEq;
    }


    private boolean repoBaseEq(Path thatRepoBase) {
        if (repoBaseUrl == null && thatRepoBase == null) return true;
        if (repoBaseUrl != null && thatRepoBase == null) return false;
        if (repoBaseUrl == null && thatRepoBase != null) return false;
        return repoBaseUrl.equals(thatRepoBase);
    }

    public int getViewCount() {
        return imViews.size();
    }
}
