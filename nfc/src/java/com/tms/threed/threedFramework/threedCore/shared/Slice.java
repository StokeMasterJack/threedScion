package com.tms.threed.threedFramework.threedCore.shared;

import javax.annotation.Nonnull;

public final class Slice {

    private final String view;
    private final int angle;

    public Slice(@Nonnull String view, int angle) {
        assert view != null;
        this.angle = angle;
        this.view = view;
    }

    public Slice(@Nonnull String slice) {
        assert slice != null;
        String[] a = slice.split("-");
        this.angle = new Integer(a[1]);
        this.view = a[0];
    }

    public int getAngle() {
        return angle;
    }

    public String getAnglePadded() {
        if (angle >= 1 && angle <= 9) return "0" + angle;
        else return angle + "";
    }

    public String getView() {
        return view;
    }

    public String getViewName() {
        return view;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Slice viewSnap = (Slice) o;

        if (angle != viewSnap.angle) return false;
        if (!view.equals(viewSnap.view)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = view.hashCode();
        result = 31 * result + angle;
        return result;
    }

    @Override public String toString() {
        return "view: " + view + ", angle: " + angle;
    }

}
