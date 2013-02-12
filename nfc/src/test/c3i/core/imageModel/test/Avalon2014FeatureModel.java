package c3i.core.imageModel.test;

import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.server.JsonToFmJvm;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.imageModel.shared.SimpleFeatureModel;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;

public class Avalon2014FeatureModel implements SimpleFeatureModel {

    private FeatureModel fm;

    public Avalon2014FeatureModel() throws IOException {
        SeriesKey seriesKey = SeriesKey.AVALON_2011;
        URL resource = Resources.getResource(this.getClass(), "avalon-fm.json");
        JsonToFmJvm u = new JsonToFmJvm();
        fm = u.parseJson(seriesKey, resource);
    }

    @Override
    public Var get(String varCode) {
        return fm.get(varCode);
    }

    @Override
    public SeriesKey getSeriesKey() {
        return fm.getSeriesKey();
    }
}
