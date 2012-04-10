package threed.skin.previewPanel.shared.viewModel;

import com.google.gwt.event.shared.GwtEvent;
import threed.core.threedModel.shared.Slice;

import static smartsoft.util.lang.shared.Strings.getSimpleName;

/**
 * Angle and View changed
 */
public class AngleAndViewChangeEvent extends GwtEvent<AngleAndViewChangeHandler> {

    private Slice newViewSnap;

    public AngleAndViewChangeEvent(Slice newViewSnap) {
        this.newViewSnap = newViewSnap;
    }

    public static final Type<AngleAndViewChangeHandler> TYPE = new Type<AngleAndViewChangeHandler>();

    @Override public Type<AngleAndViewChangeHandler> getAssociatedType() {
        return TYPE;
    }

    @Override protected void dispatch(AngleAndViewChangeHandler handler) {
        handler.onChange(this);
    }

    public Slice getNewViewSnapAngle() {
        return newViewSnap;
    }

    @Override public String toString() {
        return getSimpleName(this) + " [" + newViewSnap + "]";
    }

}
