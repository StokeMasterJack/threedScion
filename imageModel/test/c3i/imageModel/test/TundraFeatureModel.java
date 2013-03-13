package c3i.imageModel.test;

import c3i.featureModel.shared.common.SeriesKey;
import c3i.featureModel.server.JsonToFmJvm;
import c3i.featureModel.shared.FeatureModel;
import com.google.common.io.Resources;

import java.net.URL;

public class TundraFeatureModel{



    public static FeatureModel parse() {
        URL resource = Resources.getResource(TundraFeatureModel.class, "tundra-fm.json");
        JsonToFmJvm u = new JsonToFmJvm();
        return u.parseJson(SeriesKey.TUNDRA_2011, resource);
    }
}
