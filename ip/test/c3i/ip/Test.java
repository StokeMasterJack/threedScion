package c3i.ip;

import c3i.featureModel.shared.common.BrandKey;
import c3i.featureModel.shared.common.SeriesKey;
import c3i.imageModel.shared.BaseImageKey;
import c3i.imageModel.shared.Profile;
import c3i.imageModel.shared.Profiles;
import c3i.repo.server.BrandRepo;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.SeriesRepo;
import c3i.repo.server.SrcPngLoaderRepo;
import c3i.repo.server.rt.RtRepo;
import junit.framework.TestCase;
import smartsoft.util.shared.Path;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class Test extends TestCase {

    Path inPath = new Path("/configurator-content-scion")
            .append("frs")
            .append("2013")
            .append("exterior")
            .append("03_BrakeDisk")
            .append("vr_1_02.png");

    Path outPath = new Path("Users")
            .append("dford")
            .append("temp")
            .append("io")
            .append("dave.png");

    BrandRepos brandRepos;
    BrandRepo brandRepo;

    public void setUp() throws Exception {
        File repoBaseDir = new File("/configurator-content-scion");
        brandRepos = BrandRepos.createSingleBrand(BrandKey.SCION, repoBaseDir);
        brandRepo = new BrandRepo(BrandKey.TOYOTA, repoBaseDir);
    }

    public void testImgGen() throws Exception {
        SeriesKey sk = SeriesKey.FRS_2013;

        String fp = "f2cd70a-49f9b91-2926eca-0c9f56a-ce22027-01de875-957b422";

        Profiles profiles1 = brandRepo.getProfiles();
        List<Profile> profiles = profiles1.getList();

        for (Profile profile : profiles) {
            BaseImageKey baseImage = new BaseImageKey(sk, profile, fp);
            SeriesRepo seriesRepo = brandRepo.getSeriesRepo(sk);
            RtRepo rtRepo = seriesRepo.getRtRepo();
            File outFile = rtRepo.getBaseImageFileName(baseImage);
            SrcPngLoader srcPngLoader = new SrcPngLoaderRepo(brandRepos);
            BaseImageGenerator g = new BaseImageGenerator(outFile, baseImage, srcPngLoader);
            g.generate();
            System.out.println("outFile = " + outFile);
        }


    }


    public void testLift() throws Exception {
        long t1 = System.currentTimeMillis();

        File inFile = new File(inPath.toString());
        File outFile = new File(outPath.toString());


        System.out.println("inFile = " + inFile);
        System.out.println("inFile.exists = " + inFile.exists());
        System.out.println("inFile.canRead = " + inFile.canRead());
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
