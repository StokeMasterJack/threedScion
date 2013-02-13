package c3i.imageModel.test;

import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.server.JsonToFmJvm;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.threedModel.shared.ImFeatureModel;
import com.google.common.io.Resources;

import java.net.URL;

public class TundraFeatureModel extends ImFeatureModel {

    public TundraFeatureModel() {
        super(parse());
    }

    public static FeatureModel parse() {
        URL resource = Resources.getResource(TundraFeatureModel.class, "tundra-fm.json");
        JsonToFmJvm u = new JsonToFmJvm();
        return u.parseJson(SeriesKey.TUNDRA_2011, resource);
    }
}
