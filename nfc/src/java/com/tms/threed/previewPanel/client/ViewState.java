package com.tms.threed.previewPanel.client;

import com.tms.threed.threedCore.threedModel.shared.ViewKey;

public interface ViewState {

    void setCurrentView();

    ViewKey getCurrentView();

    void previousAngle();

    void nextAngle();

    void setCurrentAngle(int newAngle);

    int getCurrentAngle();

    boolean isActive();

    int getPanelIndex();

}