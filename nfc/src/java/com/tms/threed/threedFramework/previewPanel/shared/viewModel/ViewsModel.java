package com.tms.threed.threedFramework.previewPanel.shared.viewModel;

import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.threedFramework.threedCore.shared.ViewKey;

public interface ViewsModel {

//    SeriesInfo getCurrentSeries();

    void setCurrentView(ViewKey newViewKey);

    int getCurrentAngle(ViewKey viewKey);

    void previousAngle(ViewKey viewKey);

    void nextAngle(ViewKey viewKey);

    ViewKey getCurrentView();

    void nextAngle();

    void previousAngle();

    int getCurrentAngle();

    void setCurrentAngle(int newAngle);

    HandlerRegistration addViewChangeHandler(ViewChangeHandler handler);

    HandlerRegistration addAngleChangeHandler(AngleChangeHandler handler);

    void nextView();
}