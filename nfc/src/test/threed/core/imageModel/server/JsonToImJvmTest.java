package threed.core.imageModel.server;

import com.google.common.io.Resources;
import threed.core.featureModel.server.JsonToFmJvm;
import threed.core.featureModel.shared.FeatureModel;
import threed.core.imageModel.shared.ImSeries;
import threed.core.threedModel.shared.SeriesInfoBuilder;
import threed.core.threedModel.shared.SeriesKey;
import threed.core.threedModel.shared.ThreedModel;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.net.URL;

public class JsonToImJvmTest {

    @Test public void test1() throws Exception {

        SeriesKey seriesKey = SeriesKey.AVALON_2011;

        URL urlFm = Resources.getResource(JsonToFmJvm.class, "avalon-fm.json");


        JsonToFmJvm uFm = new JsonToFmJvm();
        FeatureModel fm = uFm.parseJson(seriesKey, urlFm);



        URL urlIm = Resources.getResource(this.getClass(), "avalon-im.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsImageModel = mapper.readValue(urlIm, JsonNode.class);


        JsonToImJvm uIm = new JsonToImJvm(fm, SeriesInfoBuilder.createSeriesInfo(seriesKey));
        ImSeries im = uIm.parseSeries(jsImageModel);

        ThreedModel threedModel = new ThreedModel(fm,im);


    }


}