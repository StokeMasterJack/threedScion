package com.tms.threed.jpgGen.server.singleJpg;

import com.tms.threed.threedCore.threedModel.shared.JpgWidth;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenDetails {

    private final Integer quality;
    private final JpgWidth jpgWidth;

    /**
     * @param quality
     * @param jpgWidth
     */
    public GenDetails(Integer quality, JpgWidth jpgWidth) {
        assert jpgWidth != null;
        if (quality == null) {
            quality = 75;
        } else if (quality < 1 || quality > 100) {
            throw new IllegalArgumentException("quality must be between 1 and 100 exclusive");
        }


        this.quality = quality;
        this.jpgWidth = jpgWidth;
    }

    public GenDetails(Integer jpgWidth) {
        this(null, new JpgWidth(jpgWidth));
    }

    public GenDetails(JpgWidth jpgWidth) {
        this(null, jpgWidth);
    }

    public GenDetails() {
        this(75, null);
    }

    /**
     * JPG compression quality.
     * <p/>
     * 100 is best
     * 0 is worst
     *
     * @return int between o and 100 inclusive
     */
    public int getQuality() {
        return quality;
    }

    public JpgWidth getJpgWidth() {
        return jpgWidth;
    }

    private static final Log log = LogFactory.getLog(GenDetails.class);
}
