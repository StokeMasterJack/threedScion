package c3i.smartClient.client.service;

import c3i.featureModel.shared.common.SeriesId;
import c3i.imageModel.shared.ImageMode;

import java.util.Set;

public class ImageStackRequest {

    private final SeriesId seriesId;
    private final Set<String> picks;
    private final String view;
    private final int angle;
    private final int width;
    private final ImageMode mode;

    public ImageStackRequest(SeriesId seriesId, Set<String> picks, String view, int angle, int width, ImageMode mode) {
        this.seriesId = seriesId;
        this.picks = picks;
        this.view = view;
        this.angle = angle;
        this.width = width;
        this.mode = mode;
    }

    public SeriesId getSeriesId() {
        return seriesId;
    }

    public Set<String> getPicks() {
        return picks;
    }

    public String getView() {
        return view;
    }

    public int getAngle() {
        return angle;
    }

    public int getWidth() {
        return width;
    }

    public ImageMode getMode() {
        return mode;
    }
}
