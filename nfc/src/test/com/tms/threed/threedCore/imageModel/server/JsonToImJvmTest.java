package com.tms.threed.threedCore.imageModel.server;

import com.google.common.io.Resources;
import com.tms.threed.threedCore.featureModel.server.JsonToFmJvm;
import com.tms.threed.threedCore.featureModel.shared.FeatureModel;
import com.tms.threed.threedCore.imageModel.shared.ImSeries;
import com.tms.threed.threedCore.threedModel.shared.SeriesInfoBuilder;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import com.tms.threed.threedCore.threedModel.shared.ThreedModel;
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