package com.tms.threed.threedFramework.threedAdmin.main.client;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.user.client.ui.DialogBox;
import com.tms.threed.threedFramework.util.gwtUtil.client.dialogs.DialogResources;

/**
 * This is buggy in Safari devMode: after using the X button, various mouse events on the page sop working correctly
 */
public class MyDialogBox extends DialogBox {

    private static final int WIDTH_OF_CLOSE_BUTTON_PX = 27;

    public MyDialogBox(String titleBar) {
        super(true);
        setText(titleBar);
        String url = DialogResources.INSTANCE.closeButton().getURL();
        setHTML("<table style='margin:0;padding:0' width='100%'><tr><td><b>" + titleBar + "</b></td><td align='right'><img src='" + url + "'/></td></tr></table>");
        setHTML("<table style='margin:0;padding:0' width='100%'><tr><td><b>" + titleBar + "</b></td><td align='right'><img src='" + url + "'/></td></tr></table>");
    }


    @Override
    protected void beginDragging(MouseDownEvent ev) {
        int dialogWidth = this.getOffsetWidth();

        int L = dialogWidth - WIDTH_OF_CLOSE_BUTTON_PX - 20;
        int R = dialogWidth;

        int x = ev.getX();
        int y = ev.getY();
        if (x > L && x < R && y > 5 && y < 33) {
            this.hide();
        } else {
            super.beginDragging(ev);
        }
    }
}
