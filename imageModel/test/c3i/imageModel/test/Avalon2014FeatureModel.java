package c3i.imageModel.test;

import c3i.featureModel.server.JsonToFmJvm;
import c3i.featureModel.shared.FeatureModel;
import com.google.common.io.Resources;

import java.net.URL;

public class Avalon2014FeatureModel  {

    public static FeatureModel parse() {
        URL resource = Resources.getResource(Avalon2014FeatureModel.class, "avalon-fm.json");
        JsonToFmJvm u = new JsonToFmJvm();
        return u.parseJson(c3i.core.common.shared.SeriesKey.AVALON_2011, resource);
    }
}
