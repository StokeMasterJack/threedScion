package c3i.core.threedModel.shared;

import c3i.core.featureModel.shared.FeatureModel;
import c3i.imageModel.shared.SeriesKey;
import c3i.imageModel.shared.SimpleFeatureModel;

public class ImFeatureModel implements SimpleFeatureModel {

    protected final FeatureModel featureModel;

    public ImFeatureModel(FeatureModel featureModel) {
        this.featureModel = featureModel;
    }

    @Override
    public Object get(String varCode) {
        return featureModel.get(varCode);
    }

    @Override
    public SeriesKey getSeriesKey() {
        return fmToMmSeriesKey(featureModel.getSeriesKey());
    }

    @Override
    public boolean containsCode(String varCode) {
        return featureModel.containsCode(varCode);
    }

    public static c3i.core.common.shared.SeriesKey imToFmSeriesKey(c3i.imageModel.shared.SeriesKey seriesKey) {
        return new c3i.core.common.shared.SeriesKey(seriesKey.getBrand(), seriesKey.getYear(), seriesKey.getName());
    }

    public static c3i.imageModel.shared.SeriesKey fmToMmSeriesKey(c3i.core.common.shared.SeriesKey seriesKey) {
        return new c3i.imageModel.shared.SeriesKey(
                seriesKey.getBrandKey().getKey(),
                seriesKey.getYear(),
                seriesKey.getName());
    }

}
