package smartClient.client;

import com.google.common.collect.ImmutableList;
import com.tms.threed.threedCore.threedModel.shared.SeriesId;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import com.tms.threed.threedCore.threedModel.shared.VtcMap;
import org.timepedia.exporter.client.Exportable;
import smartsoft.util.lang.shared.ImageSize;

public class Brand implements Exportable {

    private final BrandKey brandKey;
    private final VtcMap vtcMap;
    private final ImmutableList<Profile> profiles;

    public Brand(BrandKey brandKey, VtcMap vtcMap, ImmutableList<Profile> profiles) {
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

    public ImmutableList<Profile> getProfiles() {
        return profiles;
    }

    public Profile getProfile(String profileKey) {
        //temp todo DF
        if (profileKey.equalsIgnoreCase("wStd")) {
            return new Profile("wStd", ImageSize.STD_PNG, ImageSize.STD_PNG);
        }
        for (Profile profile : profiles) {
            if (profile.getKey().equals(profileKey)) {
                return profile;
            }
        }
        throw new IllegalArgumentException("Invalid profile key[" + profileKey + "]");
    }

    public SeriesId getSeriesId(SeriesKey seriesKey) {
        return vtcMap.getSeriesId(seriesKey);
    }
}
