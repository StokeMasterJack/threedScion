package c3i.imageModel.test;

import c3i.core.featureModel.server.JsonToFmJvm;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.threedModel.shared.ImFeatureModel;
import com.google.common.io.Resources;

import java.net.URL;

public class Avalon2014FeatureModel extends ImFeatureModel {

    public Avalon2014FeatureModel() {
        super(parse());
    }

    public static FeatureModel parse() {
        URL resource = Resources.getResource(Avalon2014FeatureModel.class, "avalon-fm.json");
        JsonToFmJvm u = new JsonToFmJvm();
        return u.parseJson(c3i.core.common.shared.SeriesKey.AVALON_2011, resource);
    }
}
