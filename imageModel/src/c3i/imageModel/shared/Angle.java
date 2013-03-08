package c3i.imageModel.shared;

import smartsoft.util.shared.Strings;

import javax.annotation.Nonnull;

public class Angle implements Comparable<Angle> {

    public static final int HERO_ANGLE = 2;

    public static final int SIDE_ANGLE = 1;
    public static final int DASH_ANGLE = 3;
    public static final int TOP_ANGLE = 2;


    public final int displayIndex;
    public final int angleValue;
    public final String label;

    //cached fields
    public transient int hash;


    public Angle(int displayIndex, int angleValue, String label) {
        assert displayIndex >= 0;
        assert angleValue >= 1;

        this.displayIndex = displayIndex;
        this.angleValue = angleValue;
        if (label == null) {
            this.label = angleValue + "";
        } else {
            this.label = label;
        }

        hash = initHash();
    }

    public Angle(int displayIndex, int angleValue) {
        this(displayIndex, angleValue, null);
    }

    public int getAngleValue() {
        return angleValue;
    }

    public String getLabel() {
        return label;
    }

    public int getDisplayIndex() {
        return displayIndex;
    }

    public void checkViewTypeNamedAngleButtons() {
        if (Strings.isEmpty(label)) throw new IllegalStateException();
    }

    public boolean eq(@Nonnull Angle that) {
        return this.displayIndex == that.displayIndex;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Angle that = (Angle) o;

        return angleValue == that.angleValue;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    private int initHash() {
        return angleValue;
    }

    public boolean isSide() {
        return angleValue == SIDE_ANGLE;
    }

    public boolean isDash() {
        return angleValue == DASH_ANGLE;
    }

    public boolean isTop() {
        return angleValue == TOP_ANGLE;
    }

    public static enum ImageFileExtension {
        JPG, PNG
    }

    @Override
    public String toString() {
        return angleValue + "";
    }

    public static String getAnglePadded(int angleValue) {
        String anglePadding = angleValue < 10 ? "0" : "";
        return anglePadding + angleValue;
    }

    public String getAnglePadded() {
        return getAnglePadded(this.angleValue);
    }

    public String getLocalImageName(ImageFileExtension imageType, boolean blinkBlink) {
        StringBuffer sb = new StringBuffer();
        getLocalImageName(sb, imageType, blinkBlink);
        return sb.toString();
    }

    public void getLocalImageName(StringBuffer sb, ImageFileExtension imageType, boolean blinkBlink) {
        String anglePadding = angleValue < 10 ? "0" : "";
        String sWhite = blinkBlink ? "_w" : "";
        String s = anglePadding + angleValue;
        String name = "vr_1_" + s + sWhite;

        String fileExtension = imageType.name().toLowerCase();
        sb.append(name);
        sb.append('.');
        sb.append(fileExtension);
    }

    public void getLocalJpgImageName(StringBuffer sb) {
        getLocalImageName(sb, ImageFileExtension.JPG, false);
    }

    public void getLocalPngImageName(StringBuffer sb, boolean blinkBlink) {
        getLocalImageName(sb, ImageFileExtension.PNG, blinkBlink);
    }

    public void getLocalPngImageName(StringBuffer sb) {
        getLocalImageName(sb, ImageFileExtension.PNG, false);
    }

    public String getLocalJpgFileName() {
        return getLocalImageName(ImageFileExtension.JPG, false);
    }

    public String getLocalPngFileName(boolean blinkBlink) {
        return getLocalImageName(ImageFileExtension.PNG, blinkBlink);
    }

    public String getLocalPngFileName() {
        return getLocalImageName(ImageFileExtension.PNG, false);
    }

    @Override
    public int compareTo(Angle that) {
        if (this.displayIndex == that.displayIndex) return 0;
        else if (this.displayIndex < that.displayIndex) return -1;
        else return 1;
    }
}

