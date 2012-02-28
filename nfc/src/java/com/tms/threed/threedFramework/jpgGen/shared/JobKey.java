package com.tms.threed.threedFramework.jpgGen.shared;

import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.threedModel.shared.SeriesId;

public class JobKey {

    private final SeriesId seriesId;
    private final JpgWidth jpgWidth;

    public JobKey(SeriesId seriesId, JpgWidth jpgWidth) {
        this.seriesId = seriesId;
        this.jpgWidth = jpgWidth;
    }

    public SeriesId getSeriesId() {
        return seriesId;
    }

    public JpgWidth getJpgWidth() {
        return jpgWidth;
    }
}
