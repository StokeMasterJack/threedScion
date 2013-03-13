package c3i.featureModel.server;

import c3i.featureModel.shared.common.SeriesKey;
import c3i.featureModel.shared.FeatureModel;
import com.google.common.io.Resources;
import org.junit.Test;

import java.net.URL;

public class JsonToFmJvmTest {

    @Test
    public void testAvalon() throws Exception {

        SeriesKey seriesKey = SeriesKey.AVALON_2011;
        URL resource = Resources.getResource(this.getClass(), "avalon-fm.json");
        JsonToFmJvm u = new JsonToFmJvm();
        FeatureModel fm = u.parseJson(seriesKey, resource);

        fm.getRootVar().printVarTree();


    }


    @Test
    public void testRav4() throws Exception {

        SeriesKey seriesKey = SeriesKey.RAV4_2011;

        URL resource = Resources.getResource(this.getClass(), "rav4-fm.json");

        JsonToFmJvm u = new JsonToFmJvm();
        FeatureModel fm = u.parseJson(seriesKey, resource);

        fm.getRootVar().printVarTree();


    }

}