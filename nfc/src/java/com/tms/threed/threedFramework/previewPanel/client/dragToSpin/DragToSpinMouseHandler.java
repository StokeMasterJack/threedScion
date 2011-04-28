package com.tms.threed.threedFramework.previewPanel.client.dragToSpin;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;
import com.tms.threed.threedFramework.previewPanel.client.ViewState;

public class DragToSpinMouseHandler implements MouseMoveHandler, MouseDownHandler, MouseOverHandler, MouseOutHandler, MouseUpHandler {

    private Image image;

    private static final int DELTA_X_FOR_ANGLE_CHANGE = 50;

    private boolean isArmed;

    private Integer startX = null;

    private ViewState model;

    private boolean mouseDown;

    public DragToSpinMouseHandler(Image image, ViewState model) {
        this.image = image;
        this.model = model;

        image.addMouseMoveHandler(this);
        image.addMouseDownHandler(this);
        image.addMouseOverHandler(this);
        image.addMouseOutHandler(this);
        image.addMouseUpHandler(this);

        final Element element = image.getElement();
        fixIE(element);
    }

    public void attachToTarget(Image image) {
        this.image = image;
        this.model = model;

        image.addMouseMoveHandler(this);
        image.addMouseDownHandler(this);
        image.addMouseOverHandler(this);
        image.addMouseOutHandler(this);
        image.addMouseUpHandler(this);

        final Element element = image.getElement();
        fixIE(element);
    }

    public boolean isEnabled() {
        return model.getCurrentView().isExterior();
    }

//    public void setEnabled(boolean enabled) {
//        this.enabled = enabled;
//    }

    native void fixIE(Element img) /*-{
        // ...implemented with JavaScript
        img.ondragstart=function(e) {return false;}
    }-*/;

    public void onMouseMove(MouseMoveEvent ev) {
        if (!isArmed) return;

        final int mouseX = ev.getX();
        int deltaX = mouseX - startX;
        if (deltaX == 0) return;

        if (Math.abs(deltaX) >= DELTA_X_FOR_ANGLE_CHANGE) {
            if (deltaX > 0) model.nextAngle();
            else if (deltaX < 0) model.previousAngle();
            else throw new IllegalStateException();
            startX = mouseX;
        }
    }

    public void onMouseDown(MouseDownEvent ev) {
        this.mouseDown = true;
        arm(ev);
    }

    private void arm(MouseEvent ev) {
        if (!isEnabled()) {
            return;
        }

        ev.preventDefault();
        isArmed = true;

        startX = ev.getX();
        setCursorMove();
    }

    private void setCursorMove() {
        image.getElement().getStyle().setCursor(Style.Cursor.MOVE);
//       image.getElement().getStyle().setProperty("cursor","hand");
    }

    private void setCursorDefault() {
        image.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
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

    private void disarm() {
        isArmed = false;
        startX = null;
        setCursorDefault();
    }
}
