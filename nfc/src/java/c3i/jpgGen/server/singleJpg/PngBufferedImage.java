package c3i.jpgGen.server.singleJpg;

import c3i.imageModel.shared.PngSegment;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class PngBufferedImage {

    private final BufferedImage sourceBufferedImage;
    private final PngSegment pngSpec;

    public PngBufferedImage(BufferedImage sourceBufferedImage, PngSegment pngSpec) {
        this.sourceBufferedImage = sourceBufferedImage;
        this.pngSpec = pngSpec;
    }

    public BufferedImage getSourceBufferedImage() {
        return sourceBufferedImage;
    }

    public PngSegment getPngSpec() {
        return pngSpec;
    }

    public Graphics2D createGraphics() {
        return sourceBufferedImage.createGraphics();
    }
}
