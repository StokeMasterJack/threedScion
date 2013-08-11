package c3i.smartClient.client.widgets.dragToSpin;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.HandlesAllMouseEvents;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.UIObject;

public class DragToSpinHelperOld<T extends UIObject & HasAllMouseHandlers> {

    private static final int DELTA_X_FOR_ANGLE_CHANGE = 50;

    private DragToSpinModel angleScrollListener;

    private TheTarget target;

    private boolean enabled;

    public void setAngleScrollListener(DragToSpinModel spinListener) {
        this.angleScrollListener = spinListener;
    }

    public void attachToTarget(T aTarget) {
        target = new TheTarget(aTarget);
        target.addAllHandlers(mouseHandlers);
    }

    public boolean isEnabled() {
        return enabled && angleScrollListener != null;
    }

    public void setEnabled(boolean enabled) {
        if(target==null) throw new IllegalStateException("must call attachToTarget before calling setEnabled");
        this.enabled = enabled;
        target.setVisibility(enabled);
    }

    private static native void fixIE(Element img) /*-{
        // ...implemented with JavaScript
        img.ondragstart=function(e) {return false;}
    }-*/;

    private HandlesAllMouseEvents mouseHandlers = new HandlesAllMouseEvents() {

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
                }
                else if (deltaX < 0) {
                    angleScrollListener.anglePrevious();
                }
                else {
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
            if (mouseDown) arm(event);
            else disarm();
        }

        public void onMouseOut(MouseOutEvent event) {
            disarm();
        }

        public void onMouseUp(MouseUpEvent event) {
            this.mouseDown = false;
            disarm();
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

        @Override public void onMouseWheel(MouseWheelEvent event) {
            //ignore
        }
    };

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
        }

        public void setVisibility(boolean b){
            target.setVisible(b);
        }


    }


}