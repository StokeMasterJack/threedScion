package com.tms.threed.threedFramework.util.lang.shared;

public class ImageSize {

    public static final ImageSize STD_PNG = new ImageSize(599, 366);

    private final int width;
    private final int height;

    public ImageSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
