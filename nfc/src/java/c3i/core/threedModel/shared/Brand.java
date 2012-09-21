package c3i.core.threedModel.shared;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.imageModel.shared.Profile;
import c3i.core.imageModel.shared.Profiles;
import com.google.common.base.Preconditions;

import static smartsoft.util.lang.shared.Strings.notEmpty;

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
