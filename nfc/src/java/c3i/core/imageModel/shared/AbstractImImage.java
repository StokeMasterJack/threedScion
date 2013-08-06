package c3i.core.imageModel.shared;

import c3i.core.common.shared.SeriesKey;

abstract public class AbstractImImage implements ImImage {

    protected final Profile profile;

    public AbstractImImage(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }

    @Override
    public abstract SeriesKey getSeriesKey();

    @Override
    public boolean isScionImage() {
        return getSeriesKey().isScion();
    }


}
