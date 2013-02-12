package c3i.imageModel.shared;

public class AngleKey {

    private final ViewKey viewKey;
    private final int angle;

    public AngleKey(ViewKey viewKey, int angle) {
        this.viewKey = viewKey;
        this.angle = angle;
    }

    public ViewKey getViewKey() {
        return viewKey;
    }

    public int getAngle() {
        return angle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AngleKey angleKey = (AngleKey) o;

        if (angle != angleKey.angle) return false;
        if (!viewKey.equals(angleKey.viewKey)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = viewKey.hashCode();
        result = 31 * result + angle;
        return result;
    }

    @Override
    public String toString() {
        return viewKey.toString() + "  Angle[" + angle + "]";
    }
}
