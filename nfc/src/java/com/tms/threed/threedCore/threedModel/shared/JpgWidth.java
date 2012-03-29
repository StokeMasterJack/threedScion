package com.tms.threed.threedCore.threedModel.shared;

import com.google.common.base.Preconditions;
import smartsoft.util.lang.shared.ImageSize;

import java.io.Serializable;

/**
 * Used for scaling. If this returns null then no scaling (i.e.JPG image dims same as input png image dims)
 */
public class JpgWidth implements Serializable {

    private static final long serialVersionUID = -30797081563647027L;

    private static final String STD_KEY = "wStd";
    public static final Integer STD_WIDTH = ImageSize.STD_PNG.getWidth();

    public static final JpgWidth W_STD = new JpgWidth(STD_KEY, STD_WIDTH);

    private final String key;
    private final Integer width;

    /**
     * @param width jpg width in pixes or null to use same width as png
     */
    public JpgWidth(String key, Integer width) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(width);

        if (width != null) {
            if (width < 10 || width > 5000) throw new IllegalArgumentException("Bad JpgWidth");
        }

        this.key = key;
        this.width = width;
    }

    public JpgWidth(Integer width) {
        Preconditions.checkNotNull(width);

        if (width != null) {
            if (width < 10 || width > 5000) throw new IllegalArgumentException("Bad JpgWidth");
        }

        if (STD_WIDTH.equals(width)) {
            this.key = "wStd";
        } else {
            this.key = "w" + width;
        }
        this.width = width;
    }

    /**
     * @param widthString jpg width string. examples of valid values:
     *
     *      null
     *      wStd
     *      standard
     *
     *      w300
     *      w400
     *      w500
     */
    public JpgWidth(String widthString) {
        if (isStandard(widthString)) {
            key = "wStd";
            width = ImageSize.STD_PNG.getWidth();
        } else if (widthString.length() < 3) {
            throw new IllegalArgumentException("Bad JpgWidth: " + widthString);
        } else {
            if (widthString.startsWith("w")) {
                key = widthString;
                try {
                    width = Integer.parseInt(widthString.substring(1));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Bad JpgWidth: " + widthString);
                }
            } else {
                throw new IllegalArgumentException("Bad JpgWidth: " + widthString);
            }
        }
    }

    public static boolean isStandard(String jpgWidthString) {
        return STD_KEY.equals(jpgWidthString);
    }

    //    public boolean isSta
    public String stringValue() {
        if (isStandard()) {
            return STD_KEY;
        } else {
            return "w" + width;
        }
    }


    public String getUrlPathFolderName() {
        return stringValue();
    }

    @Override
    public String toString() {
        return stringValue();
    }

    public boolean isStandard() {
        return key.equals(STD_KEY);
    }

    public boolean isScaled() {
        return !isStandard();
    }

    public int intValue() {
        if (isStandard()) throw new IllegalStateException("Can only get the int value of scaled width");
        return width;
    }

    public int intValueNoFail() {
        if (isStandard()) return ImageSize.STD_PNG.getWidth();
        return width;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JpgWidth that = (JpgWidth) o;

        if (this.width == null) {
            return that.width == null;
        } else {
            return this.width.equals(that.width);
        }

    }

    @Override
    public int hashCode() {
        return width != null ? width.hashCode() : 0;
    }

    public ImageSize getJpgSize() {
        return getJpgSize(ImageSize.STD_PNG);
    }

    public ImageSize getJpgSize(ImageSize ratio) {
        double widthHeightRatio = ratio.getWidthHeightRatio();
        return getJpgSize(widthHeightRatio);
    }

    public ImageSize getJpgSize(double widthHeightRatio) {
        double thisHeight = ((double) width) / widthHeightRatio;
        return new ImageSize(width, (int) thisHeight);
    }

    public String getKey() {
        return key;
    }
}
