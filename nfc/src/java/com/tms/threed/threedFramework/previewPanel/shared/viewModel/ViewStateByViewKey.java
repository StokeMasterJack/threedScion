package com.tms.threed.threedFramework.previewPanel.shared.viewModel;

import com.tms.threed.threedFramework.previewPanel.client.ViewState;
import com.tms.threed.threedFramework.threedModel.shared.ViewKey;

public class ViewStateByViewKey implements ViewState {

    //    private final ViewStates viewStates;
    private final ViewKey viewKey;
    private final ViewStates viewStates;

    private int currentAngle;

    public ViewStateByViewKey(ViewStates viewStates, ViewKey viewKey) {
        this.viewStates = viewStates;
        this.viewKey = viewKey;
        this.currentAngle = viewKey.getInitialAngle();
    }

    /**
     * copy
     */
    ViewStateByViewKey(ViewStateByViewKey source) {
        this.viewStates = source.viewStates;
        this.viewKey = source.viewKey;
        this.currentAngle = source.currentAngle;
    }

    public void setCurrentAngle(int currentAngle) {
        this.currentAngle = currentAngle;
    }

    public int getCurrentAngle() {
        return currentAngle;
    }

    public ViewKey getCurrentView() {
        return viewKey;
    }

    public void previousAngle() {
        this.currentAngle = viewKey.getPrevious(currentAngle);
    }

    public void nextAngle() {
        this.currentAngle = viewKey.getNext(currentAngle);
    }

    @Override public void setCurrentView() {
        System.out.println("ViewStateByViewKey.setCurrentView(..)");
    }

    @Override public boolean isActive() {
        return viewStates.getCurrentView() == this.getCurrentView();
    }

    @Override public int getPanelIndex() {
        return viewStates.getCurrentPanelForView(viewKey);
    }
}
