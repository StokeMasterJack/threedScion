package com.tms.threed.threedAdmin.main.client;

import com.google.gwt.user.client.ui.Widget;

public interface UiContext {

    void hideMessage();
    void showMessage(String msg);
    int addTab(final Widget widget, String tabName);

}
