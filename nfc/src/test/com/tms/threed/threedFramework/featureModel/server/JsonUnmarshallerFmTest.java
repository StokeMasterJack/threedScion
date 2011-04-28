package com.tms.threed.threedFramework.featureModel.server;

import com.google.common.io.Resources;
import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;
import org.junit.Test;

import java.net.URL;

public class JsonUnmarshallerFmTest {

    @Test public void testAvalon() throws Exception {

        SeriesKey seriesKey = SeriesKey.AVALON_2011;

        URL resource = Resources.getResource(this.getClass(), "avalon-fm.json");

        JsonUnmarshallerFm u = new JsonUnmarshallerFm();
        FeatureModel fm = u.parseJson(seriesKey, resource);

        fm.getRootVar().printVarTree();


    }


    @Test public void testRav4() throws Exception {

        SeriesKey seriesKey = SeriesKey.RAV4_2011;

        URL resource = Resources.getResource(this.getClass(), "rav4-fm.json");

        JsonUnmarshallerFm u = new JsonUnmarshallerFm();
        FeatureModel fm = u.parseJson(seriesKey, resource);

        fm.getRootVar().printVarTree();


    }

}