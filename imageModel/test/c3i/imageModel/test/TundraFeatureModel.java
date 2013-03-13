package c3i.imageModel.test;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.server.JsonToFmJvm;
import c3i.core.featureModel.shared.FeatureModel;
import com.google.common.io.Resources;

import java.net.URL;

public class TundraFeatureModel{



    public static FeatureModel parse() {
        URL resource = Resources.getResource(TundraFeatureModel.class, "tundra-fm.json");
        JsonToFmJvm u = new JsonToFmJvm();
        return u.parseJson(SeriesKey.TUNDRA_2011, resource);
    }
}
