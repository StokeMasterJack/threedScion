package c3i.imgGen;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.imageModel.server.JsonToImJvm;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.ImageModel;
import c3i.imageModel.shared.RawImageStack;
import c3i.imageModel.shared.Slice2;
import c3i.imageModel.test.Avalon2014Picks;
import c3i.imgGen.external.ImgGenContext;
import c3i.imgGen.external.ImgGenContextFactory;
import c3i.imgGen.external.ImgGenContextFactoryDave;
import c3i.imgGen.server.JpgSetTask;
import c3i.imgGen.server.JpgSetsTask;
import c3i.repo.server.BrandRepos;
import junit.framework.TestCase;

import java.io.File;

public class ImgGenTest extends TestCase {

    File TOYOTA_REPO_BASE_DIR = new File("/configurator-content-toyota");

    BrandRepos brandRepos;
    ImgGenContextFactory factory;
    Object contextKey;
    ImgGenContext ctx;

    @Override
    protected void setUp() throws Exception {
        brandRepos = BrandRepos.createSingleBrand(BrandKey.TOYOTA, TOYOTA_REPO_BASE_DIR);
        factory = createImageGenFactory();
        contextKey = createImageGenContextKey();
        ctx = factory.getImgGenContext(contextKey);
    }

    public void testImageModel() throws Exception {

        ImageModel imageModel = createImageModel(ctx);

        String viewName = "exterior";
        int angle = 2;
        Avalon2014Picks picksAvalon = new Avalon2014Picks();

        RawImageStack imageStack = imageModel.getImageStack(viewName, angle, picksAvalon);

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

    public void testJpgSetOneSlice() throws Exception {

        String viewName = "exterior";
        int angle = 2;

        ImageModel imageModel = createImageModel(ctx);

        ImView view = imageModel.getView(viewName);

        Slice2 slice2 = new Slice2(view, angle);

        JpgSetTask jpgSetTask = new JpgSetTask(ctx, slice2);
        jpgSetTask.start();
        assertEquals(189, jpgSetTask.getJpgCount());
    }

    public void testJpgSetAllSlices() throws Exception {
        JpgSetsTask jpgSetsTask = new JpgSetsTask(ctx);
        jpgSetsTask.start();
        assertEquals(2385, jpgSetsTask.getJpgCount());
    }

    ImageModel createImageModel(ImgGenContext ctx) {
        String imageModelJson = ctx.getImageModelJson();
        return JsonToImJvm.parse(ctx, imageModelJson);
    }

    Object createImageGenContextKey() {
        return new SeriesId(BrandKey.TOYOTA, "avalon", 2014, "18942e640eb38949d3fa6a7bad3958edd1283d7c");
    }

    ImgGenContextFactory createImageGenFactory() {
        return new ImgGenContextFactoryDave(brandRepos);
    }
}