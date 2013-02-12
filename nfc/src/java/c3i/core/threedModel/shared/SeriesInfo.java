package c3i.core.threedModel.shared;

import c3i.core.common.shared.SeriesKey;
import c3i.core.imageModel.shared.Slice;
import c3i.core.imageModel.shared.ViewKeyOld;

import javax.annotation.Nonnull;

public class SeriesInfo {

    private final SeriesKey seriesKey;
    private final ViewKeyOld[] viewsKeys;

    public SeriesInfo(@Nonnull final SeriesKey seriesKey, @Nonnull final ViewKeyOld[] viewsKeys) {
        this.seriesKey = seriesKey;
        this.viewsKeys = viewsKeys;
    }

    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    public String getName() {
        return getSeriesKey().getName();
    }

    public ViewKeyOld[] getViewKeys() {
        return viewsKeys;
    }

    public ViewKeyOld getViewKeyByName(String viewName) {
        for (ViewKeyOld viewsKey : viewsKeys) {
            if (viewsKey.getName().equals(viewName)) return viewsKey;
        }
        throw new IllegalArgumentException("Bad viewName[" + viewName + "]");
    }

    public ViewKeyOld getExterior() {
        return viewsKeys[0];
    }

    public ViewKeyOld getInterior() {
        return viewsKeys[1];
    }

    public ViewKeyOld nextView(ViewKeyOld viewKey) {
        int i = indexOf(viewKey);
        if (i == lastViewIndex()) return getFirstView();
        else return viewsKeys[i + 1];
    }

    public ViewKeyOld previousView(ViewKeyOld viewKey) {
        int i = indexOf(viewKey);
        if (i == 0) return getLastView();
        else return viewsKeys[i - 1];
    }

    public ViewKeyOld getFirstView() {
        return viewsKeys[0];
    }

    public ViewKeyOld getInitialViewKey() {
        return getFirstView();
    }

    public ViewKeyOld getLastView() {
        return viewsKeys[lastViewIndex()];
    }

    public boolean isLast(ViewKeyOld viewKey) {
        return indexOf(viewKey) == viewsKeys.length - 1;
    }

    public boolean isFirstView(ViewKeyOld viewKey) {
        return indexOf(viewKey) == 0;
    }

    public int indexOf(ViewKeyOld viewKey) {
        for (int i = 0; i < viewsKeys.length; i++) {
            if (viewsKeys[i].equals(viewKey)) return i;

        }
        return -1;
    }

    public int lastViewIndex() {
        return viewsKeys.length - 1;
    }

    public int getViewCount() {
        return getViewKeys().length;
    }

    public ViewKeyOld getViewKey(int viewIndex) {
        return viewsKeys[viewIndex];
    }

    public int compareTo(ViewKeyOld v1, ViewKeyOld v2) {
        if (v1 == v2) return 0;
        if (v2 == null) return 1;
        if (v1 == null) return -1;
        Integer i1 = indexOf(v1);
        Integer i2 = indexOf(v2);
        return i1.compareTo(i2);
    }

    public boolean same(SeriesInfo that) {
        return this == that;
    }

    public boolean same(SeriesKey that) {
        return this.seriesKey.equals(that);
    }


    public int getYear() {
        return seriesKey.getYear();
    }

    public String getSeriesName() {
        return seriesKey.getName();
    }

    public boolean containsView(ViewKeyOld key) {
        for (ViewKeyOld viewsKey : viewsKeys) {
            if (viewsKey.equals(key)) return true;
        }
        return false;
    }

    public boolean isValidViewName(String name) {
        for (ViewKeyOld viewKey : viewsKeys) {
            String vkName = viewKey.getName();
            if (vkName.equals(name)) return true;
        }
        return false;
    }

//    public static SeriesInfo DUMMY_SERIES_INFO = SeriesInfoBuilder.createDummySeriesInfo();


    public Slice getInitialViewState() {
        ViewKeyOld initialView = getInitialViewKey();
        return new Slice(initialView.getName(), initialView.getInitialAngle());
    }

    public Slice getViewSnapFromOrientation(int orientation) {
        ViewKeyOld viewKey;
        int angle;
        if (orientation >= 1 && orientation <= 12) {
            viewKey = viewsKeys[0];
            angle = orientation;
        } else if (orientation >= 13 && orientation <= 15) {
            viewKey = viewsKeys[1];
            angle = orientation - 12;
        } else if (orientation == 16) {
            viewKey = viewsKeys[2];
            angle = 1;
        } else {
            throw new IllegalStateException();
        }
        return new Slice(viewKey.getName(), angle);
    }
}
