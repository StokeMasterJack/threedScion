package com.tms.threed.jpgGen.server.singleJpg;


import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.tms.threed.jpgGen.shared.Stats;
import com.tms.threed.repo.server.JpgKey;
import com.tms.threed.repo.server.Repos;
import com.tms.threed.repo.server.SeriesRepo;
import com.tms.threed.repo.server.rt.RtRepo;
import com.tms.threed.threedCore.threedModel.shared.JpgWidth;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Takes multiple pngs and turns them into a single jpg
 */
public class JpgGeneratorPureJava {

    public static final float QUALITY = 75F * .01F;

    private final JpgKey jpgKey;

    private final SeriesRepo seriesRepo;
    private final RtRepo genRepo;
    private final String jpgFingerprint;
    private final File jpgOutputFile;

    public JpgGeneratorPureJava(Repos repos, JpgKey jpgKey) {
        this.jpgKey = jpgKey;
        this.seriesRepo = repos.getSeriesRepo(jpgKey.getSeriesKey());
        this.genRepo = seriesRepo.getRtRepo();
        this.jpgFingerprint = jpgKey.getFingerprint();
        this.jpgOutputFile = getJpgOutputFile();
    }

    public void generate() {
        this.generate(null);
    }

    public void generate(Stats stats) {
        if (jpgOutputFile.exists()) return;
        ArrayList<BufferedImage> pngs = readPngs(stats);
        BufferedImage combinedPng = createCombinedPng(pngs, stats);
        BufferedImage jpg = createJpg(combinedPng, stats);
        writeJpg(jpg, stats);
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

    private ArrayList<BufferedImage> readPngs(Stats stats) {

        long t1 = System.currentTimeMillis();

        ArrayList<BufferedImage> a = new ArrayList<BufferedImage>();


        String[] pngShortShas = jpgFingerprint.split("-");
        String pngShortSha = pngShortShas[0];

        BufferedImage bgImage = readPng(pngShortSha);
        a.add(bgImage);


        for (int i = 1; i < pngShortShas.length; i++) {
            pngShortSha = pngShortShas[i];
            BufferedImage image = readPng(pngShortSha);
            a.add(image);
        }

        long t2 = System.currentTimeMillis();

        if (stats != null) {
            stats.readPngsDeltaSum += (t2 - t1);
        }

        return a;


    }

    private BufferedImage createCombinedPng(List<BufferedImage> pngs, Stats stats) {

        long t1 = System.currentTimeMillis();


        BufferedImage bgImage = pngs.get(0);
        Graphics2D bgGraphics = bgImage.createGraphics();

        for (int i = 1; i < pngs.size(); i++) {

            BufferedImage image = pngs.get(i);
            bgGraphics.drawImage(image, 0, 0, null);
        }
        bgGraphics.dispose();

        long t2 = System.currentTimeMillis();
        if (stats != null) {
            stats.createCombinedPngDeltaSum += (t2 - t1);
        }

        return bgImage;
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

    private void writeJpg(BufferedImage input, Stats stats) {
        long t1 = System.currentTimeMillis();


        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("JPG");
        if (!iter.hasNext()) throw new IllegalStateException();

        ImageWriter writer = iter.next();
        ImageWriteParam iwp = writer.getDefaultWriteParam();

        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);


        iwp.setCompressionQuality(QUALITY);

        FileImageOutputStream output = null;
        try {
            output = newFileImageOs(jpgOutputFile);
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
                    log.warn("Problem closing file [" + jpgOutputFile + "]");
                }
            }
        }


        long t2 = System.currentTimeMillis();
        if (stats != null) {
            stats.writeJpgDeltaSum += (t2 - t1);
        }

    }

    private static Log log = LogFactory.getLog(JpgGeneratorPureJava.class);

    private File getJpgOutputFile() {
        return genRepo.getJpgFileName(jpgKey);
    }

    private void createParentDirs(File jpgFile) {
        try {
            Files.createParentDirs(jpgFile);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private BufferedImage createJpg(BufferedImage png, Stats stats) {
        long t1 = System.currentTimeMillis();


        BufferedImage bufferedImage;
        int pngWidth = png.getWidth(null);
        int pngHeight = png.getHeight(null);


        //Color.WHITE estes the background to white. You can use any other color

        JpgWidth width = jpgKey.getWidth();

        if (width.isScaled()) {
            int iJpgWidth = width.intValue();
            double dJpgWidth = iJpgWidth;

            double dPngWidth = pngWidth;


            double sx = dJpgWidth / dPngWidth;
            double sy = sx;

            double dJpgHeight = pngHeight * sy;

            int iJpgHeight = (int) dJpgHeight;

            bufferedImage = new BufferedImage(iJpgWidth, iJpgHeight, BufferedImage.TYPE_INT_RGB);

            Graphics2D g = bufferedImage.createGraphics();

            g.drawImage(png, 0, 0, iJpgWidth, iJpgHeight, Color.WHITE, null);
            g.scale(sx, sy);

        } else {
            bufferedImage = new BufferedImage(pngWidth, pngHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufferedImage.createGraphics();
            g.drawImage(png, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), Color.WHITE, null);
        }


        long t2 = System.currentTimeMillis();
        if (stats != null) {
            stats.createJpgDeltaSum += (t2 - t1);
        }

        return bufferedImage;

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

    private BufferedImage readPng(String pngShortSha) {
        ObjectLoader objectLoader = seriesRepo.getPngByShortSha(pngShortSha);
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
