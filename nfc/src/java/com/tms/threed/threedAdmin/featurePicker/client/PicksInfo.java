package com.tms.threed.threedAdmin.featurePicker.client;

import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.threedCore.featureModel.shared.picks.PicksChangeHandler;

public interface PicksInfo {

    boolean isValid();

    HandlerRegistration addPicksChangeHandler(PicksChangeHandler handler);
}
