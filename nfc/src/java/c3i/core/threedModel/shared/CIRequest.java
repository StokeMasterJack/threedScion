package c3i.core.threedModel.shared;

import c3i.imageModel.shared.JpgWidth;
import c3i.imageModel.shared.SimplePicks;

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
