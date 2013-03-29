package c3i.ip;

import c3i.featureModel.shared.common.SeriesKey;
import c3i.imageModel.shared.PngSegment;
import c3i.repo.server.BrandRepo;
import c3i.repo.server.SeriesRepo;
import c3i.repo.server.rt.RtRepo;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Takes multiple pngs and turns them into a single jpg
 */
public class ZPngGenerator {

    public static final float QUALITY = 75F * .01F;

    private final PngSegment pngKey;
    private final int width;

    private final SeriesRepo seriesRepo;
    private final RtRepo genRepo;
    private final File outputFile;

    public ZPngGenerator(BrandRepo repos, int width, SeriesKey seriesKey, PngSegment pngKey) {
        this.pngKey = pngKey;
        this.width = width;
        this.seriesRepo = repos.getSeriesRepo(seriesKey);
        this.genRepo = seriesRepo.getRtRepo();
        this.outputFile = getOutputFile();
    }

    public void generate() {
        if (outputFile.exists()) return;

        FileOutputStream fos = null;
        BufferedOutputStream os = null;

        try {
            Files.createParentDirs(outputFile);
            fos = new FileOutputStream(outputFile);
            os = new BufferedOutputStream(fos);

            System.out.println("ZPngGenerator.generate");
            if (pngKey.getDeltaY() == 0 && width == -1) {
                System.out.println("\t no deltaY and no resize");
                ObjectLoader srcPng = seriesRepo.getSrcPngByShortSha(pngKey.getShortSha());
                srcPng.copyTo(os);
            } else if (width == -1 && pngKey.getDeltaY() > 0) {
                //lift only
                System.out.println("\t lift only");
                BufferedImage sourcePng = readSrcPng(pngKey.getShortSha());
                BufferedImage imageOut = liftImage(sourcePng, pngKey.getDeltaY());
                writeZPngPng(imageOut);

            } else {
                throw new IllegalStateException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            Closeables.closeQuietly(os);
            Closeables.closeQuietly(fos);
        }

    }


    private static BufferedImage readImage(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static BufferedImage readImage(InputStream is) {
        try {
            BufferedImage bi = ImageIO.read(is);
            return bi;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void writeZPngPng(BufferedImage input) {

        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("PNG");
        if (!iter.hasNext()) throw new IllegalStateException();

        ImageWriter writer = iter.next();
        ImageWriteParam iwp = writer.getDefaultWriteParam();


        FileImageOutputStream output = null;
        try {
            output = newFileImageOs(outputFile);
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
                    log.warning("Problem closing file [" + outputFile + "]");
                }
            }
        }


    }

    private static Logger log = Logger.getLogger("c3i");

    private File getOutputFile() {
        return genRepo.getZPngFileName(pngKey);
    }

    private void createParentDirs(File jpgFile) {
        try {
            Files.createParentDirs(jpgFile);
        } catch (IOException e) {
            throw new RuntimeException();
        }
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

    public BufferedImage liftImage(BufferedImage img, int deltaY) {
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();

        BufferedImage newImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(img, 0, deltaY, imgWidth, imgHeight, null);
        } finally {
            g.dispose();
        }
        return newImage;
    }


    private BufferedImage readSrcPng(String pngShortSha) {
        ObjectLoader objectLoader = seriesRepo.getSrcPngByShortSha(pngShortSha);
        ObjectStream is = null;
        try {
            is = objectLoader.openStream();
            return readImage(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            Closeables.closeQuietly(is);
        }
    }


}
