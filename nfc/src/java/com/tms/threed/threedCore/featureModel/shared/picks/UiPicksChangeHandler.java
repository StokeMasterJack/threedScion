package com.tms.threed.threedCore.featureModel.shared.picks;

import com.google.gwt.event.shared.EventHandler;

public interface UiPicksChangeHandler extends EventHandler {
    void onPicksChange(UiPicksChangeEvent e);
}