package com.tms.threed.previewPanel.shared.viewModel;

import com.tms.threed.previewPanel.client.ViewState;
import com.tms.threed.threedCore.threedModel.shared.ViewKey;

public class ViewStateByViewKey implements ViewState {

    private final ViewKey viewKey;

    private int currentAngle;

    public ViewStateByViewKey(ViewKey viewKey) {
        this.viewKey = viewKey;
        this.currentAngle = viewKey.getInitialAngle();
    }

    /**
     * copy
     */
    ViewStateByViewKey(ViewStateByViewKey source) {
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

}
