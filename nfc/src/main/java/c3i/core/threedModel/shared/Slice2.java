package c3i.core.threedModel.shared;

import c3i.core.imageModel.shared.ImView;

import javax.annotation.Nonnull;

public final class Slice2 {

    private final ImView view;
    private final int angle;

    public Slice2(@Nonnull ImView view, int angle) {
        assert view != null;
        this.angle = angle;
        this.view = view;
    }

    public int getAngle() {
        return angle;
    }

    public String getAnglePadded() {
        return getAnglePadded(this.angle);
    }

    public static String getAnglePadded(int angle) {
        if (angle >= 1 && angle <= 9) return "0" + angle;
        else return angle + "";
    }

    public ImView getView() {
        return view;
    }

    public String getViewName() {
        return view.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Slice2 viewSnap = (Slice2) o;

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

    @Override
    public String toString() {
        return "view: " + view + ", angle: " + angle;
    }

}
