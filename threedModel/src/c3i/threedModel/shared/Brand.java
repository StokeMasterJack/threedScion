package c3i.threedModel.shared;

import c3i.featureModel.shared.common.BrandKey;
import c3i.featureModel.shared.common.SeriesId;
import c3i.featureModel.shared.common.SeriesKey;
import c3i.imageModel.shared.Profile;
import c3i.imageModel.shared.Profiles;
import com.google.common.base.Preconditions;

import static smartsoft.util.shared.Strings.notEmpty;

public class Brand {

    private final BrandKey brandKey;
    private final VtcMap vtcMap;
    private final Profiles profiles;

    public Brand(BrandKey brandKey, VtcMap vtcMap, Profiles profiles) {
        this.brandKey = brandKey;
        this.vtcMap = vtcMap;
        this.profiles = profiles;
    }

    public BrandKey getBrandKey() {
        return brandKey;
    }

    public VtcMap getVtcMap() {
        return vtcMap;
    }

    public Profiles getProfiles() {
        return profiles;
    }

    public Profile getProfile(String profileKey) {
        Preconditions.checkArgument(notEmpty(profileKey));
        return profiles.get(profileKey);
    }

    public SeriesId getSeriesId(SeriesKey seriesKey) {
        return vtcMap.getSeriesId(seriesKey);
    }
}
