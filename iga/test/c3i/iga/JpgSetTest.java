package c3i.iga;

import c3i.featureModel.shared.common.BrandKey;
import c3i.featureModel.shared.common.SeriesId;
import c3i.featureModel.shared.common.SimplePicks;
import c3i.imageModel.shared.ImageModel;
import c3i.imageModel.shared.RawImageStack;
import c3i.imageModel.shared.Slice;
import c3i.imageModel.test.Avalon2014Picks;
import c3i.imgGen.ImgGenApp;
import c3i.imgGen.api.ThreedModelService;
import c3i.repo.server.BrandRepos;
import c3i.threedModel.client.ThreedModel;
import junit.framework.TestCase;

public class JpgSetTest extends TestCase {

    SeriesId id;

    ThreedModelService threedModelService;
    ImgGenApp app;

    BrandRepos brandRepos;

    @Override
    protected void setUp() throws Exception {
        id = new SeriesId(BrandKey.TOYOTA, "avalon", 2014, "18942e640eb38949d3fa6a7bad3958edd1283d7c");
        app = new ImgGenApp();
        brandRepos = app.getBrandRepos();
        threedModelService = app.getThreedModelService();
    }

    public void testJpgSet() throws Exception {
        Slice slice = new Slice("exterior", 2);
        ThreedModel threedModel = threedModelService.getThreedModel(id);

        JpgSetTask jpgSetTask = Util.createJpgSetTask(threedModel, slice);
        jpgSetTask.start();
        JpgSet jpgSet = jpgSetTask.getJpgSet();
        assertEquals(189, jpgSet.size());
    }

    public void testJpgSets() throws Exception {
        ThreedModel threedModel = threedModelService.getThreedModel(id);
        JpgSetsTask task = Util.createJpgSetsTask(threedModel);
        task.start();
        JpgSets jpgSets = task.getJpgSets();
        assertEquals(2385, jpgSets.getJpgCount());
    }

    public void testImageModel() throws Exception {

        String viewName = "exterior";
        int angle = 2;
        Avalon2014Picks simplePicks = new Avalon2014Picks();

        ThreedModel threedModel = threedModelService.getThreedModel(id);

        ImageModel imageModel = threedModel.getImageModel();

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


}