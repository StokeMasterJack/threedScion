package com.tms.threed.threedFramework.previewPanel.client;

import com.tms.threed.threedFramework.threedCore.shared.ViewKey;

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