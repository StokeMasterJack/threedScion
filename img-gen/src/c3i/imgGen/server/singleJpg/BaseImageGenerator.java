package c3i.imgGen.server.singleJpg;


import c3i.imageModel.shared.IBaseImageKey;
import c3i.imageModel.shared.ImageModelKey;
import c3i.imageModel.shared.PngSegment;
import c3i.imageModel.shared.RawBaseImage;
import c3i.imgGen.api.SrcPngLoader;
import c3i.imgGen.shared.Stats;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import org.imgscalr.Scalr;
import smartsoft.util.shared.IORuntimeException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Takes multiple pngs and turns them into a single jpg
 */
public class BaseImageGenerator {

    public static final float QUALITY = 75F * .01F;

    private final IBaseImageKey baseImage;
    private final File outFile;
    private final SrcPngLoader pngLoader;

    public BaseImageGenerator(final File outFile, final IBaseImageKey baseImage, final SrcPngLoader pngLoader) {
        this.baseImage = baseImage;
        this.outFile = outFile;
        this.pngLoader = pngLoader;
    }

    public void generate() {
        this.generate(null);
    }

    public void generate(Stats stats) {
        if (outFile.exists()) return;
        BufferedImage combined = combinePngs(stats);
        BufferedImage scaled = maybeScale(combined, stats);
        writeBaseImage(stats, scaled);
    }

    private void writeBaseImage(Stats stats, BufferedImage scaled) {
        if (baseImage.isJpg()) {
            writeBaseJpg(scaled, stats);
        } else {
            writeBasePng(scaled, stats);
        }
    }

    private boolean includeBackground() {
        return baseImage.getProfile().includeBackgroundLayer();
    }

    private BufferedImage combinePngs(Stats stats) {

        long t1 = System.currentTimeMillis();

        BufferedImage newImage = null;
        Graphics2D newGraphics = null;

        RawBaseImage pngKeys = baseImage.getPngKeys();

        boolean zeroPngIsBackground = false;

        int i = 0;
        for (PngSegment pngKey : pngKeys.getPngs()) {
            BufferedImage layerPng = readSrcPng(pngKey.getShortSha());

            boolean background = layerPng.getTransparency() == Transparency.OPAQUE;

            if (newImage == null) {
                zeroPngIsBackground = background;

                int w = layerPng.getWidth();
                int h = layerPng.getHeight();

                int combinedImageType;
                if (includeBackground()) { //is final thing supposed to be a jpg
                    assert zeroPngIsBackground;
                    combinedImageType = BufferedImage.TYPE_INT_RGB;
                } else {
                    combinedImageType = BufferedImage.TYPE_INT_ARGB;
                }

                newImage = new BufferedImage(w, h, combinedImageType);
                newGraphics = newImage.createGraphics();

                //If layer zero is supposed to be a background
                //but it somehow has some transparency (like Tundra undercarriage)
                //then make sure those alpha portions are not accidentally turned black.
                if (includeBackground()) {
                    newGraphics.setPaint(Color.WHITE);
                    newGraphics.fillRect(0, 0, w, h);
                }


            }

            if (i != 0 && background) {
                throw new IllegalStateException();
            }

            boolean skip = background && !includeBackground();

            if (!skip) {
                int deltaY = pngKey.getDeltaY();
                if (deltaY != 0) {
                    newGraphics.drawImage(layerPng, 0, deltaY, null);
                } else {
                    newGraphics.drawImage(layerPng, 0, 0, null);
                }
            }

            i++;
        }


        if (newGraphics != null) {
            newGraphics.dispose();
        }

        long t2 = System.currentTimeMillis();

        if (stats != null) {
            stats.combinePngsDeltaSum += (t2 - t1);
        }

        return newImage;
    }

    private static BufferedImage readSrcImage(InputStream is) {
        try {
            BufferedImage bi = ImageIO.read(is);
            return bi;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeBaseJpg(BufferedImage input, Stats stats) {
        long t1 = System.currentTimeMillis();

        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("JPG");
        if (!iter.hasNext()) throw new IllegalStateException();

        ImageWriter writer = iter.next();
        ImageWriteParam iwp = writer.getDefaultWriteParam();

        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

        iwp.setCompressionQuality(QUALITY);

        FileImageOutputStream output = null;
        try {
            output = newFileImageOs(outFile);
            writer.setOutput(output);
            IIOImage image = new IIOImage(input, null, null);
            writeImage(writer, iwp, image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    log.warning("Problem closing file [" + outFile + "]");
                }
            }
        }


        long t2 = System.currentTimeMillis();
        if (stats != null) {
            stats.writeBaseImageDeltaSum += (t2 - t1);
        }

    }

    private void writeBasePng(BufferedImage input, Stats stats) {
        long t1 = System.currentTimeMillis();
        try {
            Files.createParentDirs(outFile);
            ImageIO.write(input, "PNG", outFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            long t2 = System.currentTimeMillis();
            if (stats != null) {
                stats.writeBaseImageDeltaSum += (t2 - t1);
            }
        }


    }

    private static Logger log = Logger.getLogger("c3i");

    private void createParentDirs(File jpgFile) {
        try {
            Files.createParentDirs(jpgFile);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private BufferedImage maybeScale(BufferedImage imageIn, Stats stats) {

        if (baseImage.getProfile().isStandard()) {
            return imageIn;
        }

        int wIn = imageIn.getWidth();
        int wOut = baseImage.getProfile().getWidth();

        if (wOut == wIn) {
            return imageIn;
        }

        long t1 = System.currentTimeMillis();
        BufferedImage retVal = Scalr.resize(imageIn, wOut);
        long t2 = System.currentTimeMillis();

        if (stats != null) {
            stats.maybeScalePngDeltaSum += (t2 - t1);
        }
        return retVal;


    }


    private static FileImageOutputStream newFileImageOs(File outFile) {
        try {
            Files.createParentDirs(outFile);
            return new FileImageOutputStream(outFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeImage(ImageWriter writer, ImageWriteParam iwp, IIOImage image) {
        try {
            writer.write(null, image, iwp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputSupplier<? extends InputStream> getPng(String pngShortSha) {
        return null;
    }

    public void test1() throws Exception {
        InputStream is = null;
        try {
            is = getPng("www").getInput();
            BufferedImage bufferedImage = readSrcImage(is);
        } finally {
            Closeables.closeQuietly(is);
        }
    }

    private BufferedImage readSrcPng(String pngShortSha) throws IORuntimeException {
        InputStream is = null;
        try {
            ImageModelKey imageModelKey = baseImage.getSeriesKey();
            is = pngLoader.getPng(imageModelKey, pngShortSha).getInput();
            return readSrcImage(is);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            Closeables.closeQuietly(is);
        }
    }


//    private BufferedImage readSrcPng(String pngShortSha, Stats stats) {
//        long t1 = System.currentTimeMillis();
//        ObjectLoader objectLoader = seriesRepo.getSrcPngByShortSha(pngShortSha);
//        ObjectStream is = null;
//        try {
//            is = objectLoader.openStream();
//            return readSrcImage(is);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } finally {
//            Closeables.closeQuietly(is);
//            long t2 = System.currentTimeMillis();
//            if (stats != null) {
//                stats.readSrcPngDeltaSum += (t2 - t1);
//            }
//        }
//    }


}
