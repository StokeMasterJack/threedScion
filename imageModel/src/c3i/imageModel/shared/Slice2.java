package c3i.imageModel.shared;

import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.common.SimplePicks;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Slice2 {

    private final ImView view;
    private final int angle;

    public Slice2(ImView view, int angle) {
        checkNotNull(view);
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

    public Set<Var> getPngVars() {
        return view.getPngVars(angle);
    }

    public Slice getSlice() {
        return new Slice(view.getName(), angle);
    }

    public RawBaseImage getPngSegments(SimplePicks picks) {
        return view.getPngSegments(picks, angle);
    }


}
