package com.tms.threed.threedAdmin.client.featurePicker;

import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.threedCore.featureModel.shared.picks.PicksChangeHandler;

public interface PicksInfo {

    boolean isValid();

    HandlerRegistration addPicksChangeHandler(PicksChangeHandler handler);
}
