package c3i.core.imageModel.server;

import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.server.JsonToFmJvm;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.imageModel.shared.ImSeries;
import c3i.core.imageModel.shared.SimpleFeatureModel;
import c3i.core.threedModel.shared.ThreedModel;
import com.google.common.io.Resources;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.net.URL;
import java.util.logging.Logger;

public class JsonToImJvmTest {

    @Test
    public void test1() throws Exception {

        SeriesKey seriesKey = SeriesKey.AVALON_2011;

        URL urlFm = Resources.getResource(JsonToFmJvm.class, "avalon-fm.json");


        JsonToFmJvm uFm = new JsonToFmJvm();
        final FeatureModel fm = uFm.parseJson(seriesKey, urlFm);


        URL urlIm = Resources.getResource(this.getClass(), "avalon-im.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsImageModel = mapper.readValue(urlIm, JsonNode.class);

        SimpleFeatureModel sfm = new SimpleFeatureModel() {
            @Override
            public Var get(String varCode) {
                return fm.get(varCode);
            }

            @Override
            public SeriesKey getSeriesKey() {
                return fm.getSeriesKey();
            }
        };

        JsonToImJvm uIm = new JsonToImJvm(sfm);
        ImSeries im = uIm.parseSeries(jsImageModel);

        ThreedModel threedModel = new ThreedModel(fm, im);


    }

    private static Logger log = Logger.getLogger(JsonToImJvmTest.class.getName());


}