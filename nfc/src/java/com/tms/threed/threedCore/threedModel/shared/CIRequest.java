package com.tms.threed.threedCore.threedModel.shared;

import com.tms.threed.threedCore.imageModel.shared.slice.SimplePicks;

public class CIRequest {

    private final String viewName;
    private final int angle;
    private final SimplePicks picks;
    private final JpgWidth jpgWidth;
    private final CIMode mode;

    public CIRequest(String viewName, int angle, SimplePicks picks, JpgWidth jpgWidth, CIMode mode) {
        this.viewName = viewName;
        this.angle = angle;
        this.picks = picks;
        this.jpgWidth = jpgWidth;
        this.mode = mode;
    }

    public String getViewName() {
        return viewName;
    }

    public int getAngle() {
        return angle;
    }

    public SimplePicks getPicks() {
        return picks;
    }

    public JpgWidth getJpgWidth() {
        return jpgWidth;
    }

    public CIMode getMode() {
        return mode;
    }
}
