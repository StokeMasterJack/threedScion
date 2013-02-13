package c3i.smartClient.client.model;

import com.google.common.base.Preconditions;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.GestureChangeEvent;
import com.google.gwt.event.dom.client.GestureChangeHandler;
import com.google.gwt.event.dom.client.GestureEndEvent;
import com.google.gwt.event.dom.client.GestureEndHandler;
import com.google.gwt.event.dom.client.GestureStartEvent;
import com.google.gwt.event.dom.client.GestureStartHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import smartsoft.util.shared.Path;
import smartsoft.util.shared.RectSize;

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
