package c3i.core.imageModel.server;

import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.server.JsonToFmJvm;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.imageModel.shared.ImSeries;
import com.google.common.io.Resources;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

public class ParseTestImJson {

    @Test
    public void testJsonToIm() throws Exception {
        ImSeries testImageModel = getTestImageModel();
    }

    @Test
    public void testImToJson() throws Exception {
        ImSeries imSeries = getTestImageModel(); //slow
        ImToJsonJvm m2 = new ImToJsonJvm();
        ObjectNode jsonNode = m2.jsonForSeries(imSeries);

    }


    private ImSeries getTestImageModel() throws IOException {
        SeriesKey seriesKey = SeriesKey.AVALON_2011;

        URL urlFm = Resources.getResource("avalon-fm.json");

        JsonToFmJvm uFm = new JsonToFmJvm();
        FeatureModel fm = uFm.parseJson(seriesKey, urlFm);


        URL urlIm = Resources.getResource( "avalon-im.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsImageModel = mapper.readValue(urlIm, JsonNode.class);

        JsonToImJvm uIm = new JsonToImJvm(fm);
        return uIm.parseSeries(jsImageModel);
    }

    private static Logger log = Logger.getLogger(ParseTestImJson.class.getName());


}