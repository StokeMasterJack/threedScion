package com.tms.threed.threedCore.threedModel.shared;

import smartsoft.util.lang.shared.ImageSize;

import java.io.Serializable;

import static smartsoft.util.date.shared.StringUtil.isEmpty;

/**
 * Used for scaling. If this returns null then no scaling (i.e.JPG image dims same as input png image dims)
 */
public class JpgWidth implements Serializable{

    private static final long serialVersionUID = -30797081563647027L;

    private static final String W_STD_STRING = "wStd";
    private static final String STD_STRING = "Std";

    private static final String STANDARD_STRING = "standard";
    public static final JpgWidth W_STD = new JpgWidth((Integer) null);

    private Integer width;

    /**
     * @param width jpg width in pixes or null to use same width as png
     */
    public JpgWidth(Integer width) {
        this.width = width;

        if (width != null) {
            if (width < 10 || width > 5000) throw new IllegalArgumentException("Bad JpgWidth");
        }

    }

    private JpgWidth(){}

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
            width = null;
        } else if (widthString.length() < 3) {
            throw new IllegalArgumentException("Bad JpgWidth: " + widthString);
        } else {
            if (widthString.startsWith("w")) {
                widthString = widthString.substring(1);
            }
            try {
                width = new Integer(widthString);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Bad JpgWidth: " + widthString);
            }
        }
    }

    public static boolean isStandard(String jpgWidthString) {
        if (isEmpty(jpgWidthString)) return true;
        if (jpgWidthString.equalsIgnoreCase(STANDARD_STRING)) return true;
        if (jpgWidthString.equalsIgnoreCase(W_STD_STRING)) return true;
        if (jpgWidthString.equalsIgnoreCase(STD_STRING)) return true;
        if (jpgWidthString.toLowerCase().contains("standard")) return true;
        if (jpgWidthString.toLowerCase().contains("std")) return true;
        return false;
    }

    public String stringValue() {
        if (width == null) {
            return W_STD_STRING;
        } else {
            return "w" + width;
        }
    }

    public String getUrlPathFolderName() {
        if (width == null) {
            return W_STD_STRING;
        } else {
            return "w" + width;
        }
    }

    @Override public String toString() {
        return stringValue();
    }

    public boolean isStandard() {
        return width == null;
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

    public ImageSize getJpgSize(ImageSize pngSize) {
        if (width == null) return pngSize;

        int pngWidth = pngSize.getWidth();
        int pngHeight = pngSize.getHeight();

        int iJpgWidth = width.intValue();
        double dJpgWidth = iJpgWidth;

        double dPngWidth = pngWidth;


        double sx = dJpgWidth / dPngWidth;
        double sy = sx;

        double dJpgHeight = pngHeight * sy;

        int iJpgHeight = (int) dJpgHeight;

        ImageSize jpgImageSize = new ImageSize(iJpgWidth, iJpgHeight);
        return jpgImageSize;

    }
}
