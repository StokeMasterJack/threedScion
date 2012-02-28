package com.tms.threed.threedFramework.imageModel.server;

import com.google.common.io.Resources;
import com.tms.threed.threedFramework.featureModel.server.JsonUnmarshallerFm;
import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.imageModel.shared.ImSeries;
import com.tms.threed.threedFramework.threedModel.shared.SeriesInfoBuilder;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.net.URL;

public class JsonUnmarshallerImTest {

    @Test public void test1() throws Exception {

        SeriesKey seriesKey = SeriesKey.AVALON_2011;

        URL urlFm = Resources.getResource(JsonUnmarshallerFm.class, "avalon-fm.json");


        JsonUnmarshallerFm uFm = new JsonUnmarshallerFm();
        FeatureModel fm = uFm.parseJson(seriesKey, urlFm);



        URL urlIm = Resources.getResource(this.getClass(), "avalon-im.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsImageModel = mapper.readValue(urlIm, JsonNode.class);


        JsonUnmarshallerIm uIm = new JsonUnmarshallerIm(fm, SeriesInfoBuilder.createSeriesInfo(seriesKey));
        ImSeries im = uIm.parseSeries(jsImageModel);

        ThreedModel threedModel = new ThreedModel(fm,im);


    }


}