package com.tms.threed.previewPanel.client;

import com.tms.threed.threedCore.threedModel.shared.ViewKey;

public interface ViewState {

    ViewKey getCurrentView();

    void previousAngle();

    void nextAngle();

    void setCurrentAngle(int newAngle);

    int getCurrentAngle();

}