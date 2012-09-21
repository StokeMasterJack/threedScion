package c3i.smartClient.client.model;

import com.google.common.base.Preconditions;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import smartsoft.util.lang.shared.Path;
import smartsoft.util.lang.shared.RectSize;

public class ImageWidget extends Widget {

    private final ImageElement element;

    public ImageWidget() {
        this.element = Document.get().createImageElement();
        setElement(element);
        setStyleName("3d-image");
    }

    public HandlerRegistration addErrorHandler(ErrorHandler handler) {
        return addDomHandler(handler, ErrorEvent.getType());
    }

    public HandlerRegistration addLoadHandler(LoadHandler handler) {
        return addDomHandler(handler, LoadEvent.getType());
    }

    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
        return addDomHandler(handler, DoubleClickEvent.getType());
    }

    public HandlerRegistration addDragEndHandler(DragEndHandler handler) {
        return addBitlessDomHandler(handler, DragEndEvent.getType());
    }

    public HandlerRegistration addDragEnterHandler(DragEnterHandler handler) {
        return addBitlessDomHandler(handler, DragEnterEvent.getType());
    }

    public HandlerRegistration addDragHandler(DragHandler handler) {
        return addBitlessDomHandler(handler, DragEvent.getType());
    }

    public HandlerRegistration addDragLeaveHandler(DragLeaveHandler handler) {
        return addBitlessDomHandler(handler, DragLeaveEvent.getType());
    }

    public HandlerRegistration addDragOverHandler(DragOverHandler handler) {
        return addBitlessDomHandler(handler, DragOverEvent.getType());
    }

    public HandlerRegistration addDragStartHandler(DragStartHandler handler) {
        return addBitlessDomHandler(handler, DragStartEvent.getType());
    }

    public HandlerRegistration addDropHandler(DropHandler handler) {
        return addBitlessDomHandler(handler, DropEvent.getType());
    }

    public HandlerRegistration addGestureChangeHandler(GestureChangeHandler handler) {
        return addDomHandler(handler, GestureChangeEvent.getType());
    }

    public HandlerRegistration addGestureEndHandler(GestureEndHandler handler) {
        return addDomHandler(handler, GestureEndEvent.getType());
    }

    public HandlerRegistration addGestureStartHandler(GestureStartHandler handler) {
        return addDomHandler(handler, GestureStartEvent.getType());
    }

    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
        return addDomHandler(handler, MouseDownEvent.getType());
    }

    public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        return addDomHandler(handler, MouseMoveEvent.getType());
    }

    public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
        return addDomHandler(handler, MouseOutEvent.getType());
    }

    public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
        return addDomHandler(handler, MouseOverEvent.getType());
    }

    public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
        return addDomHandler(handler, MouseUpEvent.getType());
    }

    public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
        return addDomHandler(handler, MouseWheelEvent.getType());
    }

    public HandlerRegistration addTouchCancelHandler(TouchCancelHandler handler) {
        return addDomHandler(handler, TouchCancelEvent.getType());
    }

    public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
        return addDomHandler(handler, TouchEndEvent.getType());
    }

    public HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler) {
        return addDomHandler(handler, TouchMoveEvent.getType());
    }

    public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
        return addDomHandler(handler, TouchStartEvent.getType());
    }

    public void setPixelSize(RectSize imageSize) {
        Preconditions.checkNotNull(imageSize);
        this.setPixelSize(imageSize.getWidth(), imageSize.getHeight());
    }

    public void setZIndex(int newValue) {
        element.getStyle().setZIndex(newValue);
    }

    public void setSrc(String src) {
        element.setSrc(src);
    }

    public void setUrl(Path url) {
        if (url == null) {
            setSrc(null);
        } else {
            setSrc(url.toString());
        }
    }
}
