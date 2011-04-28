package com.tms.threed.threedFramework.jpgGen.server.singleJpg;


import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.tms.threed.threedFramework.jpgGen.shared.Stats;
import com.tms.threed.threedFramework.repo.server.JpgId;
import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.repo.server.SeriesRepo;
import com.tms.threed.threedFramework.repo.server.rt.RtRepo;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
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
public class JpgGeneratorPureJava {


    private final JpgId jpgId;

    private final SeriesRepo seriesRepo;
    private final RtRepo genRepo;
    private final GenDetails genDetails;
    private final String jpgFingerprint;
    private final File jpgOutputFile;

    private boolean canceled;

//    private final ConcurrentMap<String, SeriesRepo> pngCache;

    /*

    Avalon:
        readPngsDeltaSum = 			136,389ms   18,585
        createCombinedPngDeltaSum =   6,952ms
        createJpgDeltaSum = 		  1,185ms
        writeJpgDeltaSum = 			 32,017ms

        total jpg count: 822



        readPngsDeltaSum =           174,615
        createCombinedPngDeltaSum = 8,670
        createJpgDeltaSum =         1,496
        writeJpgDeltaSum =          36,682


        readPngsDeltaSum = 126240
        createCombinedPngDeltaSum = 5684
        createJpgDeltaSum = 1174
        writeJpgDeltaSum = 31478

     */

    public JpgGeneratorPureJava(Repos repos, JpgId jpgId) {

        this.jpgId = jpgId;
        this.seriesRepo = repos.getSeriesRepo(jpgId.getSeriesKey());
        this.genRepo = seriesRepo.getRtRepo();
        this.genDetails = new GenDetails(jpgId.getWidth());
        this.jpgFingerprint = jpgId.getFingerprint();


        this.jpgOutputFile = getJpgOutputFile();
    }

    public void generate(Stats stats) {
        if (jpgOutputFile.exists()) return;
        ArrayList<BufferedImage> pngs = readPngs(stats);
        BufferedImage combinedPng = createCombinedPng(pngs, stats);
        BufferedImage jpg = createJpg(combinedPng, stats);
        writeJpg(jpg, stats);
    }

    public void ser(ArrayList<BufferedImage> pngs) {

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


        stats.readPngsDeltaSum += (t2 - t1);

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
        stats.createCombinedPngDeltaSum += (t2 - t1);

        return bgImage;
    }


    protected static BufferedImage readImage(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static BufferedImage readImage(InputStream is) {
        try {


            BufferedImage bi = ImageIO.read(is);

            return bi;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void writeJpg(BufferedImage input, Stats stats) {
        long t1 = System.currentTimeMillis();


        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("JPG");
        if (!iter.hasNext()) throw new IllegalStateException();

        ImageWriter writer = iter.next();
        ImageWriteParam iwp = writer.getDefaultWriteParam();

        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

        float q = (float) genDetails.getQuality() * .01F;

        iwp.setCompressionQuality(q);

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
        stats.writeJpgDeltaSum += (t2 - t1);

    }

    private static Log log = LogFactory.getLog(JpgGeneratorPureJava.class);

    protected File getJpgOutputFile() {
        return genRepo.getJpgFileName(jpgId);
    }

    protected void createParentDirs(File jpgFile) {
        try {
            Files.createParentDirs(jpgFile);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    protected BufferedImage createJpg(BufferedImage png, Stats stats) {
        long t1 = System.currentTimeMillis();


        BufferedImage bufferedImage;
        int pngWidth = png.getWidth(null);
        int pngHeight = png.getHeight(null);


        //Color.WHITE estes the background to white. You can use any other color

        JpgWidth width = jpgId.getWidth();

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
        stats.createJpgDeltaSum += (t2 - t1);

        return bufferedImage;

    }

    protected static FileImageOutputStream newFileImageOs(File outFile) {
        try {
            Files.createParentDirs(outFile);
            return new FileImageOutputStream(outFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void writeImage(ImageWriter writer, ImageWriteParam iwp, IIOImage image) {
        try {
            writer.write(null, image, iwp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedImage readPng(String pngShortSha) {
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
