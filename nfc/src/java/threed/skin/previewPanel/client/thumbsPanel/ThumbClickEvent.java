package threed.skin.previewPanel.client.thumbsPanel;

import com.google.gwt.event.shared.GwtEvent;

public class ThumbClickEvent extends GwtEvent<ThumbClickHandler> {

    private int thumbIndex;

    public ThumbClickEvent(int thumbIndex) {
        this.thumbIndex = thumbIndex;
    }

    public static final Type<ThumbClickHandler> TYPE = new Type<ThumbClickHandler>();

    @Override public Type<ThumbClickHandler> getAssociatedType() {
        return TYPE;
    }

    @Override protected void dispatch(ThumbClickHandler handler) {
        handler.onThumbClick(this);
    }

    public int getThumbIndex() {
        return thumbIndex;
    }
}
