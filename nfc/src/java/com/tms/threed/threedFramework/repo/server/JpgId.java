package com.tms.threed.threedFramework.repo.server;

import com.tms.threed.threedFramework.imageModel.server.ImageUtil;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;

import java.io.File;

public class JpgId {

    private final SeriesKey seriesKey;
    private final JpgWidth width;
    private final String fingerprint;

    public JpgId(SeriesKey seriesKey, JpgWidth width, String fingerprint) {
        this.seriesKey = seriesKey;
        this.width = width;
        this.fingerprint = fingerprint;
    }


    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    public JpgWidth getWidth() {
        return width;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JpgId jpgId = (JpgId) o;

        if (!fingerprint.equals(jpgId.fingerprint)) return false;
        if (!seriesKey.equals(jpgId.seriesKey)) return false;
        if (!width.equals(jpgId.width)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = seriesKey.hashCode();
        result = 31 * result + width.hashCode();
        result = 31 * result + fingerprint.hashCode();
        return result;
    }

    public String getShortFingerprint() {
        return ImageUtil.getFingerprint(fingerprint);
    }

    public TwoThirty8 getTwoThirty8() {
        return getTwoThirty8(fingerprint);
    }

    public static class TwoThirty8 {

        private final String two;
        private final String thirty8;

        public TwoThirty8(String two, String thirty8) {
            this.two = two;
            this.thirty8 = thirty8;
        }

        public String getTwo() {
            return two;
        }

        public String getThirty8() {
            return thirty8;
        }

        public File getFileName(File prefix) {
            File twoFile = new File(prefix, two);
            return new File(twoFile, thirty8 + ".jpg");
        }

        @Override public String toString() {
            return "TwoThirty8{" +
                    "two='" + two + '\'' +
                    ", thirty8='" + thirty8 + '\'' +
                    '}';
        }
    }

    public static TwoThirty8 getTwoThirty8(String jpgFingerprint) {
        String shortFingerprint = ImageUtil.getFingerprint(jpgFingerprint);
        String two = shortFingerprint.substring(0,2);
        String thirty8 = shortFingerprint.substring(2);
        TwoThirty8 twoThirty8 = new TwoThirty8(two, thirty8);
        return twoThirty8;
    }

    @Override public String toString() {
        return fingerprint;
    }
}
