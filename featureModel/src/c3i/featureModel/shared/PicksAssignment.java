package c3i.featureModel.shared;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PicksAssignment {

    public static final PicksAssignment UNASSIGNED = new PicksAssignment(Bit.OPEN, null);
    public static final PicksAssignment TRUE_USER = new PicksAssignment(Bit.TRUE, Source.User);
    public static final PicksAssignment TRUE_FIXUP = new PicksAssignment(Bit.TRUE, Source.Fixup);
    public static final PicksAssignment TRUE_INIT = new PicksAssignment(Bit.TRUE, Source.Initial);
    public static final PicksAssignment FALSE_USER = new PicksAssignment(Bit.FALSE, Source.User);
    public static final PicksAssignment FALSE_FIXUP = new PicksAssignment(Bit.FALSE, Source.Fixup);
    public static final PicksAssignment FALSE_INIT = new PicksAssignment(Bit.FALSE, Source.Initial);

    private final Bit value;
    private final Source source;

    private PicksAssignment(@Nonnull Bit value, @Nullable Source source) {
        assert value != null;
        assert (value.isOpen() && source == null) || value.isAssigned() || source != null;
        this.value = value;
        this.source = source;
    }

    public static PicksAssignment create(Bit value, Source source) {
        switch (value) {
            case OPEN:
                return UNASSIGNED;
            case TRUE:
                switch (source) {
                    case Fixup:
                        return TRUE_FIXUP;
                    case Initial:
                        return TRUE_INIT;
                    case User:
                        return TRUE_USER;
                    default:
                        throw new IllegalStateException();
                }
            case FALSE:
                switch (source) {
                    case Fixup:
                        return FALSE_FIXUP;
                    case Initial:
                        return FALSE_INIT;
                    case User:
                        return FALSE_USER;
                    default:
                        throw new IllegalStateException();
                }
            default:
                throw new IllegalStateException();
        }
    }

    public Bit getValue() {
        return value;
    }

    public Source getSource() {
        return source;
    }

    public boolean isAssigned() {
        return value.isAssigned();
    }

    public boolean isOpen() {
        return value.isOpen();
    }

    public boolean isTrue() {
        return value.isTrue();
    }

    public boolean isUserAssigned() {
        return isAssigned() && source.isUser();
    }

    @Override
    public String toString() {
        return value + "[" + source + "]";
    }
}
