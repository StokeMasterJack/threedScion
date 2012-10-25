package c3i.jpgGen.server.singleJpg;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesKey;
import c3i.core.imageModel.shared.Profile;
import c3i.core.threedModel.shared.BaseImageKey;
import c3i.jpgGen.shared.Stats;
import c3i.repo.server.Repos;
import org.junit.Before;
import org.junit.Test;
import smartsoft.util.lang.shared.Path;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class TestImages {

    Path inPath = new Path("configurator-content-chuck")
            .append("scion")
            .append("frs")
            .append("2013")
            .append("exterior")
            .append("02_BrakeDisk")
            .append("vr_1_02.png");

    Path outPath = new Path("Users")
            .append("dford")
            .append("temp")
            .append("io")
            .append("dave.png");

    Repos repos;

    @Before
    public void setup() throws Exception {
        repos = new Repos(BrandKey.TOYOTA, new File("/configurator-content-chuck"));
    }

    @Test
    public void test0() throws Exception {
        Stats stats = new Stats();
        SeriesKey sk = SeriesKey.FRS_2013;
        String fp = "f2cd70a-49f9b91-2926eca-0c9f56a-ce22027-01de875-957b422";

        List<Profile> profiles = repos.getProfiles().getList();

        for (Profile profile : profiles) {
            BaseImageKey baseImage = new BaseImageKey(sk, profile, fp);
            BaseImageGenerator g = new BaseImageGenerator(Repos.get(), baseImage);
            g.generate(stats);
        }


        stats.printDeltas();
    }

    @Test
    public void test1() throws Exception {
        long t1 = System.currentTimeMillis();

        File inFile = new File(inPath.toString());
        File outFile = new File(outPath.toString());


        BufferedImage bufferedImage = ImageIO.read(inFile);


        BufferedImage newImage = liftImage(bufferedImage, 100);


        ImageIO.write(newImage, "PNG", outFile);


        long t2 = System.currentTimeMillis();
        System.out.println(" Delta: " + (t2 - t1));
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


}
