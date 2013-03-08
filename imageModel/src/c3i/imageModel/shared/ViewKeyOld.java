package c3i.imageModel.shared;

import smartsoft.util.shared.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ViewKeyOld {

    public static final String INTERIOR = "interior";
    public static final String EXTERIOR = "exterior";
    public static final String CARGO = "cargo";
    public static final String UNDERCARRIAGE = "undercarriage";

    public static final String[] ALL_VIEW_NAMES = new String[]{INTERIOR, EXTERIOR, CARGO, UNDERCARRIAGE};

    public static final int HERO_ANGLE = 2;

    public static final int SIDE_ANGLE = 1;
    public static final int DASH_ANGLE = 3;
    public static final int TOP_ANGLE = 2;

    private final String name;
    private final ViewType viewType;
    private final Angle[] angles;
    private final int[] angleValues;

    public final String label;
    public final int index;

    public final Angle firstAngle;
    public final Angle lastAngle;
    public final Angle initialAngle;


    public ViewKeyOld(int index, @Nonnull String name, @Nonnull ViewType viewType, @Nullable Angle[] angles) {
        if (name == null) throw new IllegalArgumentException("\"name\" is required");
        if (viewType == null) throw new IllegalArgumentException("\"viewType\" is required");
        if (angles == null) throw new IllegalArgumentException("\"angles\" is required");

        angleValues = new int[angles.length];
        for (int i = 0; i < angles.length; i++) {
            angleValues[i] = angles[i].angleValue;
        }

        this.index = index;
        this.name = name;
        this.viewType = viewType;
        this.angles = angles;

        firstAngle = initFirstAngle();
        lastAngle = initLastAngle();
        initialAngle = initInitialAngle();

        checkViewTypeNamedAngleButtons();
        checkViewTypeSingleAngle();

        label = Strings.capFirstLetter(name) + " View";


    }

    public int getInitialAngle() {
        return initialAngle.angleValue;
    }

    private boolean checkViewTypeNamedAngleButtons() {
        if (viewType.equals(ViewType.NAMED_ANGLE_BUTTONS)) {
            if (angles == null) throw new IllegalArgumentException();
            for (Angle angle : this.angles) {
                angle.checkViewTypeNamedAngleButtons();
            }
        }
        return true;
    }

    private boolean checkViewTypeSingleAngle() {
        if (viewType.equals(ViewType.SINGLE_ANGLE)) {
            assert firstAngle.getDisplayIndex() == 0;
            assert lastAngle.getDisplayIndex() == 0;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    private Angle initInitialAngle() {
        return initFirstAngle();
    }

    private Angle initFirstAngle() {
        return angles[0];
    }


    private Angle initLastAngle() {
        return angles[angles.length - 1];
    }

    public boolean isLast(Angle angle) {
        return lastAngle == angle;
    }

    public int getNext(int angleValue) {
        Angle angle = getByAngleValue(angleValue);
        return getNext(angle).angleValue;
    }

    public Angle getNext(Angle angle) {
        if (isLast(angle)) return firstAngle;
        else return getByDisplayIndex(angle.displayIndex + 1);
    }

    public Angle getPrevious(Angle angle) {
        if (isFirst(angle)) return lastAngle;
        else return getByDisplayIndex(angle.displayIndex - 1);
    }

    public int getPrevious(int angleValue) {
        Angle angle = getByAngleValue(angleValue);
        return getPrevious(angle).angleValue;
    }

    public Angle getByDisplayIndex(int displayIndex) {
        return angles[displayIndex];
    }

    public Angle getByAngleValue(int angleValue) {
        for (Angle angle : angles) {
            if (angle.angleValue == angleValue) return angle;
        }
        throw new IllegalArgumentException("bad angleValue[" + angleValue + "] for view[" + getName() + "]");
    }

    public boolean isFirst(Angle angle) {
        return firstAngle == angle;
    }

    public ViewType getType() {
        return viewType;
    }

    public static ViewKeyOld createSingleAngleView(int viewIndex, @Nonnull String name) {
        return new ViewKeyOld(viewIndex, name, ViewType.SINGLE_ANGLE, new Angle[]{new Angle(0, 1, name)});
    }

    public static ViewKeyOld createNamedAngleView(int viewIndex, @Nonnull String name, @Nonnull Angle[] angles) {
        return new ViewKeyOld(viewIndex, name, ViewType.NAMED_ANGLE_BUTTONS, angles);
    }

    public static ViewKeyOld createThreeSixtySpinView(int viewIndex, @Nonnull String name, @Nonnull Angle[] angles) {
        return new ViewKeyOld(viewIndex, name, ViewType.THREE_SIXTY_SPIN, angles);
    }

    public static ViewKeyOld createInteriorView(int viewIndex) {
        Angle[] angles = {
                new Angle(0, 1, "Side"),
                new Angle(1, 3, "Dash"),
                new Angle(2, 2, "Top")
        };
        return createNamedAngleView(viewIndex, INTERIOR, angles);
    }

    public static ViewKeyOld createExteriorView(int viewIndex) {
        Angle[] angles = {
                new Angle(0, 2, "Hero"),
                new Angle(1, 1),
                new Angle(2, 12),
                new Angle(3, 11),
                new Angle(4, 10),
                new Angle(5, 9),
                new Angle(6, 8),
                new Angle(7, 7),
                new Angle(8, 6),
                new Angle(9, 5),
                new Angle(10, 4),
                new Angle(11, 3)
        };
        return createThreeSixtySpinView(viewIndex, EXTERIOR, angles);
    }

    public static ViewKeyOld createCargoView(int viewIndex) {
        return createSingleAngleView(viewIndex, CARGO);
    }

    public static ViewKeyOld createUndercarriageView(int viewIndex) {
        return createSingleAngleView(viewIndex, UNDERCARRIAGE);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        return index == ((ViewKeyOld) o).index;
    }

    @Override
    public int hashCode() {
        return index;
    }

    public boolean isExterior() {
        return name.equals(EXTERIOR);
    }

    public boolean isInterior() {
        return name.equals(INTERIOR);
    }

    public int getAngleCount() {
        if (angles == null) return 0;
        return angles.length;
    }

    @Override
    public String toString() {
        return name;
    }

    public static enum ViewType {
        THREE_SIXTY_SPIN, NAMED_ANGLE_BUTTONS, SINGLE_ANGLE
    }

}
