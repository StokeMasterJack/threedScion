package c3i.imgGen;

import c3i.featureModel.shared.common.BrandKey;
import c3i.featureModel.shared.common.SeriesId;
import c3i.featureModel.shared.common.SimplePicks;
import c3i.imageModel.shared.ImageModel;
import c3i.imageModel.shared.RawImageStack;
import c3i.imageModel.test.Avalon2014Picks;
import c3i.imgGen.generic.ImgGenService;
import c3i.imgGen.repoImpl.FmIm;
import c3i.imgGen.repoImpl.KitRepo;
import c3i.imgGen.server.JpgSet;
import c3i.imgGen.server.JpgSets;
import c3i.repo.server.BrandRepos;
import junit.framework.TestCase;

import java.io.File;

public class ImgGenTest extends TestCase {

    File TOYOTA_REPO_BASE_DIR = new File("/configurator-content-toyota");

    SeriesId id;

    ImgGenService imgGenService;

    BrandRepos brandRepos;

    @Override
    protected void setUp() throws Exception {
        brandRepos = BrandRepos.createSingleBrand(BrandKey.TOYOTA, TOYOTA_REPO_BASE_DIR);
        id = new SeriesId(BrandKey.TOYOTA, "avalon", 2014, "18942e640eb38949d3fa6a7bad3958edd1283d7c");
        imgGenService = createImageGenFactoryRepo();
    }

    public void testJpgSetOneSlice() throws Exception {
        String viewName = "exterior";
        int angle = 2;

        JpgSet jpgSet = imgGenService.getJpgSet(id, viewName, angle);

        assertEquals(189, jpgSet.size());
    }

    public void testJpgSetAllSlices() throws Exception {
        JpgSets jpgSets = imgGenService.getJpgSets(id);
        assertEquals(2385, jpgSets.getJpgCount());
    }

    public void testImageModel() throws Exception {

        String viewName = "exterior";
        int angle = 2;
        SimplePicks simplePicks = new Avalon2014Picks();

        FmIm fmIm = imgGenService.getFmIm(id);

        ImageModel imageModel = fmIm.getImageModel();

        RawImageStack imageStack = imageModel.getImageStack(viewName, angle, simplePicks);

        System.out.println(imageStack.getAllPngs1());
        System.out.println(imageStack.getBasePngs1());
        System.out.println(imageStack.getZPngs1());

        System.out.println();

        System.out.println(imageStack.getAllPngs2());
        System.out.println(imageStack.getBasePngs2());
        System.out.println(imageStack.getZPngs2());

        System.out.println();

        System.out.println(imageStack.getBasePngs3());

        System.out.println(imageStack.getContextPath());

    }


    ImgGenService createImageGenFactory() {
//        return createImageGenFactoryRepo();
        return createImageGenFactoryRepo();
    }

    ImgGenService createImageGenFactoryRepo() {
        KitRepo kit = new KitRepo(brandRepos);
        return new ImgGenService<SeriesId>(kit);
    }

//    ImgGenService createImageGenFactoryDave() {
//        return new ImgGenServiceDave(brandRepos);
//    }
}