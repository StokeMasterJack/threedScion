package com.tms.threed.threedFramework.featureModel.shared.picks;

import com.google.gwt.event.shared.EventHandler;

public interface PicksChangeHandler extends EventHandler {
    void onPicksChange(PicksChangeEvent e);
}