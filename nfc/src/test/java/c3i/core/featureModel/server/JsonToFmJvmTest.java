package c3i.core.featureModel.server;

import com.google.common.io.Resources;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.common.shared.SeriesKey;
import org.junit.Test;

import java.net.URL;

public class JsonToFmJvmTest {

    @Test public void testAvalon() throws Exception {

        SeriesKey seriesKey = SeriesKey.AVALON_2011;

        URL resource = Resources.getResource("avalon-fm.json");

        JsonToFmJvm u = new JsonToFmJvm();
        FeatureModel fm = u.parseJson(seriesKey, resource);

        fm.getRootVar().printVarTree();


    }


    @Test public void testRav4() throws Exception {

        SeriesKey seriesKey = SeriesKey.RAV4_2011;

        URL resource = Resources.getResource("rav4-fm.json");

        JsonToFmJvm u = new JsonToFmJvm();
        FeatureModel fm = u.parseJson(seriesKey, resource);

        fm.getRootVar().printVarTree();


    }

}