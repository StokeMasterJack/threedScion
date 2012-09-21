package c3i.smartClient.client.widgets.dragToSpin;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.HandlesAllMouseEvents;
import com.google.gwt.event.dom.client.HandlesAllTouchEvents;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.UIObject;

public class DragToSpinHelper<T extends UIObject & HasAllMouseHandlers & HasAllTouchHandlers> {

    private static final int DELTA_X_FOR_ANGLE_CHANGE = 50;

    private DragToSpinModel angleScrollListener;

    private TheTarget target;

    private boolean enabled;

    public void setAngleScrollListener(DragToSpinModel spinListener) {
        this.angleScrollListener = spinListener;
    }

    public void attachToTarget(T aTarget) {
        target = new TheTarget(aTarget);
        target.addAllHandlers(eventHandler);
    }

    public boolean isEnabled() {
        return enabled && angleScrollListener != null;
    }

    public void setEnabled(boolean enabled) {
        if (target == null) throw new IllegalStateException("must call attachToTarget before calling setEnabled");
        this.enabled = enabled;
        target.setVisibility(enabled);
    }

    private static native void fixIE(Element img) /*-{
        // ...implemented with JavaScript
        img.ondragstart = function (e) {
            return false;
        }
    }-*/;

    private EventHandlers eventHandler = new EventHandlers();

    class EventHandlers extends HandlesAllMouseEvents implements TouchStartHandler,
            TouchMoveHandler, TouchEndHandler, TouchCancelHandler {
        private boolean isArmed;
        private Integer startX = null;
        private boolean mouseDown;

        public void onMouseMove(MouseMoveEvent ev) {
            if (!isArmed) return;

            final int mouseX = ev.getX();
            int deltaX = mouseX - startX;
            if (deltaX == 0) return;

            if (Math.abs(deltaX) >= DELTA_X_FOR_ANGLE_CHANGE) {
                if (deltaX > 0) {
                    angleScrollListener.angleNext();
                } else if (deltaX < 0) {
                    angleScrollListener.anglePrevious();
                } else {
                    throw new IllegalStateException();
                }
                startX = mouseX;
            }

        }

        public void onMouseDown(MouseDownEvent ev) {
            this.mouseDown = true;
            arm(ev);
        }

        public void onMouseOver(MouseOverEvent event) {
            if (mouseDown) {
                arm(event);
            } else {
                disarm();
            }
        }

        public void onMouseOut(MouseOutEvent event) {
            disarm();
        }

        public void onMouseUp(MouseUpEvent event) {
            this.mouseDown = false;
            disarm();
        }


        private void arm(TouchStartEvent ev) {
            if (!isEnabled()) {
                return;
            }

            ev.preventDefault();
            isArmed = true;

            JsArray changedTouches = ev.getChangedTouches();
            Touch touch = changedTouches.get(0).cast();
            startX = touch.getClientX();
            target.setCursorMove();
        }


        private void arm(MouseEvent ev) {
            if (!isEnabled()) {
                return;
            }

            ev.preventDefault();
            isArmed = true;

            startX = ev.getX();
            target.setCursorMove();
        }

        private void disarm() {
            isArmed = false;
            startX = null;
            target.setCursorDefault();
        }

        @Override
        public void onMouseWheel(MouseWheelEvent event) {
            //ignore
        }

        @Override
        public void onTouchMove(TouchMoveEvent event) {
            if (!isArmed) return;
            if (angleScrollListener == null) return;

            JsArray<Touch> touches = event.getChangedTouches();
            Touch touch = touches.get(0).cast();
            int mouseX = touch.getClientX();

            int deltaX = mouseX - startX;
            if (deltaX == 0) return;

            if (Math.abs(deltaX) >= DELTA_X_FOR_ANGLE_CHANGE) {
                if (deltaX > 0) {
                    angleScrollListener.angleNext();
                } else if (deltaX < 0) {
                    angleScrollListener.anglePrevious();
                } else {
                    throw new IllegalStateException("Invalid deltaX: [" + deltaX + "]");
                }
                startX = mouseX;
            }
        }

        @Override
        public void onTouchStart(TouchStartEvent event) {
            event.preventDefault();
            mouseDown = true;


            arm(event);
        }

        @Override
        public void onTouchCancel(TouchCancelEvent event) {

        }

        @Override
        public void onTouchEnd(TouchEndEvent event) {

        }
    }

    class TheTarget {

        T target;

        TheTarget(T target) {
            this.target = target;
            fixIE(target.getElement());
        }

        private void setCursorMove() {
            target.getElement().getStyle().setCursor(Style.Cursor.MOVE);
            //image.getElement().getStyle().setProperty("cursor","hand");
        }

        private void setCursorDefault() {
            target.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        }

        void addAllHandlers(HandlesAllMouseEvents handlers) {
            handlers.handle(target);

            HandlesAllMouseEvents.handle(target, eventHandler);
            HandlesAllTouchEvents.handle(target, eventHandler);
        }

        public void setVisibility(boolean b) {
            target.setVisible(b);
        }


    }


}