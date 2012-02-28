package com.tms.threed.threedFramework.previewPanel.shared.viewModel;

import com.tms.threed.threedFramework.previewPanel.client.ViewState;
import com.tms.threed.threedFramework.threedModel.shared.ViewKey;

public class ViewStateByPanel implements ViewState {

    private final int panelIndex;

    private final ViewStates viewStates;

    public ViewStateByPanel(int panelIndex, ViewStates viewStates) {
        this.panelIndex = panelIndex;
        this.viewStates = viewStates;
    }

    @Override public void setCurrentView() {
        ViewKey currentView = getCurrentView();
        viewStates.setCurrentView(currentView);
    }

    @Override public ViewKey getCurrentView() {
        return viewStates.getCurrentViewForPanel(panelIndex);
    }

    @Override public void previousAngle() {
        viewStates.previousAngle(getCurrentView());
    }

    @Override public void nextAngle() {
        viewStates.nextAngle(getCurrentView());
    }

    @Override public void setCurrentAngle(int newAngle) {
        viewStates.setCurrentAngle(getCurrentView(), newAngle);
    }

    @Override public int getCurrentAngle() {
        return viewStates.getCurrentAngle(getCurrentView());
    }

    @Override public boolean isActive() {
        return panelIndex == 0;
    }

    public int getPanelIndex() {
        return panelIndex;
    }
}

