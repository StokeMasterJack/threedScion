package c3i.jpgGen.server.singleJpg;

import c3i.core.imageModel.shared.PngKey;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PngBufferedImage {

    private final BufferedImage sourceBufferedImage;
    private final PngKey pngSpec;

    public PngBufferedImage(BufferedImage sourceBufferedImage, PngKey pngSpec) {
        this.sourceBufferedImage = sourceBufferedImage;
        this.pngSpec = pngSpec;
    }

    public BufferedImage getSourceBufferedImage() {
        return sourceBufferedImage;
    }

    public PngKey getPngSpec() {
        return pngSpec;
    }

    public Graphics2D createGraphics() {
        return sourceBufferedImage.createGraphics();
    }
}
