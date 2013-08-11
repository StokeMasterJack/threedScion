package c3i.admin.server;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.threedModel.shared.RootTreeId;
import org.junit.Test;

import java.io.File;

public class JpgSetTest {


    @Test
    public void test1() throws Exception {

        File cacheDir = new File("/configurator-content-toyota/.cache");



        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA,2014,"avalon");
        RootTreeId rootTreeId = new RootTreeId("18942e640eb38949d3fa6a7bad3958edd1283d7c");
        SeriesId seriesId = new SeriesId(seriesKey,rootTreeId);

        JpgSet.JpgSetKey jpgSetKey = new JpgSet.JpgSetKey(seriesId,"exterior",2);
        System.err.println("jpgSetKey[" + jpgSetKey + "]");

        JpgSet jpgSet = JpgSet.readJpgSetFile(cacheDir, jpgSetKey);

        System.err.println(jpgSet.size());

    }
}
