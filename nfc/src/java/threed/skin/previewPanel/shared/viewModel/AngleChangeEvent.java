package threed.skin.previewPanel.shared.viewModel;

import com.google.gwt.event.shared.GwtEvent;

import static smartsoft.util.lang.shared.Strings.getSimpleName;

/**
 * Angle changed but view did not
 */
public class AngleChangeEvent extends GwtEvent<AngleChangeHandler> {

    private int newAngle;

    public AngleChangeEvent(int newAngle) {
        this.newAngle = newAngle;
    }

    public static final Type<AngleChangeHandler> TYPE = new Type<AngleChangeHandler>();

    @Override public Type<AngleChangeHandler> getAssociatedType() {
        return TYPE;
    }

    @Override protected void dispatch(AngleChangeHandler handler) {
        handler.onChange(this);
    }

    public int getNewAngle() {
        return newAngle;
    }

    @Override public String toString() {
        return getSimpleName(this) + " [" + newAngle + "]";
    }

}
