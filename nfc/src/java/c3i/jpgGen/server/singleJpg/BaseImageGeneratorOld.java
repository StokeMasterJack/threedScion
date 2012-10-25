package c3i.jpgGen.server.singleJpg;


import c3i.core.imageModel.shared.BaseImageType;
import c3i.core.imageModel.shared.IBaseImageKey;
import c3i.core.imageModel.shared.PngSegment;
import c3i.core.imageModel.shared.Profile;
import c3i.jpgGen.shared.Stats;
import c3i.repo.server.Repos;
import c3i.repo.server.SeriesRepo;
import c3i.repo.server.rt.RtRepo;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectStream;
import smartsoft.util.lang.shared.RectSize;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Takes multiple pngs and turns them into a single jpg
 */
public class BaseImageGeneratorOld {

    public static final float QUALITY = 75F * .01F;

    private final IBaseImageKey baseImageKey;
    private final Profile profile;

    private final SeriesRepo seriesRepo;
    private final RtRepo genRepo;
    private final String jpgFingerprint;
    private final File outputFile;

    private final BaseImageType baseImageType;

    public BaseImageGeneratorOld(Repos repos, IBaseImageKey baseImage) {
        this.baseImageKey = baseImage;
        this.seriesRepo = repos.getSeriesRepo(baseImageKey.getSeriesKey());

        this.profile = baseImageKey.getProfile();
        this.profile.getBaseImageType();
        this.baseImageType = profile.getBaseImageType();

        this.genRepo = seriesRepo.getRtRepo();
        this.jpgFingerprint = baseImageKey.getFingerprint();
        this.outputFile = getOutputFile();
    }

    public void generate() {
        this.generate(null);
    }

    public void generate(Stats stats) {
        if (outputFile.exists()) return;

        ensureSourcePngsCopiedToGenFolder();

        List<PngBufferedImage> sourcePngs = readPngs(stats);
        BufferedImage combinedPng = createCombinedPng(sourcePngs, stats);

        if (baseImageType == BaseImageType.JPG) {
            BufferedImage baseJpg = createBaseJpg(combinedPng, stats);
            writeBaseJpg(baseJpg, stats);
        } else {
            BufferedImage basePng = createBasePng(combinedPng, stats);
            writeBasePng(basePng, stats);
        }
    }

    private void ensureSourcePngsCopiedToGenFolder() {


    }

    private void ser(ArrayList<BufferedImage> pngs) {

        try {
            String name = "/Users/dford/temp/bufferedImageSers/" + jpgFingerprint + ".ser";
            File f = new File(name);
            System.out.println(f);
            Files.createParentDirs(f);
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(name));
            out.writeObject(pngs);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<PngBufferedImage> readPngs(Stats stats) {

        long t1 = System.currentTimeMillis();

        ArrayList<PngBufferedImage> a = new ArrayList<PngBufferedImage>();


        String[] pngSegments = jpgFingerprint.split("-");
        String pngSegment = pngSegments[0];


        PngSegment bgPngSpec = new PngSegment(pngSegment);
        BufferedImage bgImage = readSrcPng(bgPngSpec.getShortSha());

        a.add(new PngBufferedImage(bgImage, bgPngSpec));


        for (int i = 1; i < pngSegments.length; i++) {
            pngSegment = pngSegments[i];
            PngSegment pngSpec = new PngSegment(pngSegment);
            BufferedImage sourceBufferedImage = readSrcPng(pngSpec.getShortSha());
            a.add(new PngBufferedImage(sourceBufferedImage, pngSpec));
        }

        long t2 = System.currentTimeMillis();

        if (stats != null) {
            stats.combinePngsDeltaSum += (t2 - t1);
        }

        return a;


    }

    private BufferedImage createCombinedPng(List<PngBufferedImage> pngs, Stats stats) {

        long t1 = System.currentTimeMillis();

        int w = pngs.get(0).getSourceBufferedImage().getWidth();
        int h = pngs.get(0).getSourceBufferedImage().getHeight();


//        PngBufferedImage bgImage = pngs.get(0);
        BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = newImage.createGraphics();

        for (int i = 0; i < pngs.size(); i++) {
            PngBufferedImage image = pngs.get(i);
            int deltaY = image.getPngSpec().getDeltaY();
            if (deltaY != 0) {
                graphics.drawImage(image.getSourceBufferedImage(), 0, deltaY, null);
            } else {
                graphics.drawImage(image.getSourceBufferedImage(), 0, 0, null);
            }
        }
        graphics.dispose();

        long t2 = System.currentTimeMillis();
        if (stats != null) {
            stats.readSrcPngDeltaSum += (t2 - t1);
        }

        return newImage;
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
                    log.warn("Problem closing file [" + outputFile + "]");
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
                    log.warn("Problem closing file [" + outputFile + "]");
                }
            }
        }


        long t2 = System.currentTimeMillis();
        if (stats != null) {
            stats.writeBaseImageDeltaSum += (t2 - t1);
        }

    }

    private static Log log = LogFactory.getLog(BaseImageGeneratorOld.class);

    private File getOutputFile() {
        return genRepo.getBaseImageFileName(baseImageKey);
    }

    private void createParentDirs(File jpgFile) {
        try {
            Files.createParentDirs(jpgFile);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private BufferedImage createBaseJpg(BufferedImage combinedPng, Stats stats) {
        long t1 = System.currentTimeMillis();


        BufferedImage retVal;
        int pngWidth = combinedPng.getWidth(null);
        int pngHeight = combinedPng.getHeight(null);


        //Color.WHITE estes the background to white. You can use any other color


        Profile profileKey = baseImageKey.getProfile();

        RectSize outputSize = profileKey.getImageSize();
        RectSize inputSize = new RectSize(combinedPng.getWidth(), combinedPng.getHeight());

        boolean scaled = !inputSize.equals(outputSize);

        if (scaled) {
            int iJpgWidth = outputSize.getWidth();
            double dJpgWidth = iJpgWidth;

            double dPngWidth = pngWidth;


            double sx = dJpgWidth / dPngWidth;
            double sy = sx;

            double dJpgHeight = pngHeight * sy;

            int iJpgHeight = (int) dJpgHeight;

            retVal = new BufferedImage(iJpgWidth, iJpgHeight, BufferedImage.TYPE_INT_RGB);

            Graphics2D g = retVal.createGraphics();

            g.drawImage(combinedPng, 0, 0, iJpgWidth, iJpgHeight, Color.WHITE, null);
            g.scale(sx, sy);

        } else {
            retVal = new BufferedImage(pngWidth, pngHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = retVal.createGraphics();
            g.drawImage(combinedPng, 0, 0, retVal.getWidth(), retVal.getHeight(), Color.WHITE, null);
        }


        long t2 = System.currentTimeMillis();
        if (stats != null) {
            stats.maybeScalePngDeltaSum += (t2 - t1);
        }

        return retVal;

    }


    /**
     * maybe scale
     */
    private BufferedImage createBasePng(BufferedImage combinedPng, Stats stats) {
        long t1 = System.currentTimeMillis();


        BufferedImage retVal;
        int pngWidth = combinedPng.getWidth(null);
        int pngHeight = combinedPng.getHeight(null);


        //Color.WHITE estes the background to white. You can use any other color


        Profile profileKey = baseImageKey.getProfile();

        RectSize outputSize = profileKey.getImageSize();
        RectSize inputSize = new RectSize(combinedPng.getWidth(), combinedPng.getHeight());

        boolean scaled = !inputSize.equals(outputSize);

        if (scaled) {


            int iJpgWidth = outputSize.getWidth();

//            System.out.println("png needs to be scaled to width: " + iJpgWidth);
            double dJpgWidth = iJpgWidth;

            double dPngWidth = pngWidth;


            double sx = dJpgWidth / dPngWidth;
            double sy = sx;

            double dJpgHeight = pngHeight * sy;

            int iJpgHeight = (int) dJpgHeight;

            retVal = new BufferedImage(iJpgWidth, iJpgHeight, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g = retVal.createGraphics();

            g.drawImage(combinedPng, 0, 0, iJpgWidth, iJpgHeight, null);
            g.scale(sx, sy);

        } else {
//            System.out.println("png does NOT need to be scaled");
//            retVal = new BufferedImage(pngWidth, pngHeight, BufferedImage.TYPE_INT_RGB);
//            Graphics2D g = retVal.createGraphics();
//            g.drawImage(combinedPng, 0, 0, retVal.getWidth(), retVal.getHeight(), Color.WHITE, null);

            //why not just return the combinedPng that was passed, with no processing?
            return combinedPng;
        }


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

//    private BufferedImage readSrcPngAndTransformIfNeeded(String sPngSegment) {
//        PngSpec pngSegment = new PngSpec(sPngSegment);
//
//        String pngShortSha = pngSegment.getShortSha();
//        Integer transform = pngSegment.getTransform();
//
//
//        if (transform == null) {
//            return readSrcPng(pngShortSha);
//        } else {
//            BufferedImage bufferedImage = readSrcPng(pngShortSha);
//            return transformPng(bufferedImage, transform);
//        }
//
//    }

//    private BufferedImage transformPng(BufferedImage in, Integer transform) {
//        int pngWidth = in.getWidth(null);
//        int pngHeight = in.getHeight(null);
//
//        BufferedImage retVal = new BufferedImage(pngWidth, pngHeight, BufferedImage.TYPE_INT_RGB);
//
//        Graphics2D g = retVal.createGraphics();
//        g.drawImage(in, 0, transform, null);
//
//        return retVal;
//    }

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
