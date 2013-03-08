package c3i.imgGen;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.imageModel.server.JsonToImJvm;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.ImageModel;
import c3i.imageModel.shared.RawImageStack;
import c3i.imageModel.test.Avalon2014Picks;
import c3i.imgGen.external.ImgGenContext;
import c3i.imgGen.external.ImgGenContextFactory;
import junit.framework.TestCase;

public class ImgGenTest extends TestCase {

    ImgGenContextFactory factory;
    Object contextKey;
    ImgGenContext ctx;

    @Override
    protected void setUp() throws Exception {
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

    public void testJpgCountOneSlice() throws Exception {

        String viewName = "exterior";
        int angle = 2;

        ImageModel imageModel = createImageModel(ctx);

        ImView view = imageModel.getView(viewName);

        JpgSetTask jpgSetTask = new JpgSetTask(ctx, view, angle);
        jpgSetTask.start();
        assertEquals(189, jpgSetTask.getJpgCount());
    }

    public void testJpgCount() throws Exception {
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
        return new ImgGenContextFactoryDave();
    }
}