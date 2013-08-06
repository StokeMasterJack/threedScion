package c3i.core.threedModel.shared;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.imageModel.shared.Profile;
import c3i.core.imageModel.shared.Profiles;
import c3i.smartClient.client.model.Img;
import com.google.common.base.Preconditions;
import smartsoft.util.shared.Path;

import java.util.Map;

import static smartsoft.util.shared.Strings.isEmpty;
import static smartsoft.util.shared.Strings.notEmpty;

public class Brand {

    public static final String IMAGE_REPO_BASE_URL_KEY = "imageRepoBaseUrl";

    private final BrandKey brandKey;
    private final VtcMap vtcMap;
    private final Profiles profiles;
    private final Map<String, String> config;

    public Brand(BrandKey brandKey, VtcMap vtcMap, Profiles profiles, Map<String, String> config) {
        this.brandKey = brandKey;
        this.vtcMap = vtcMap;
        this.profiles = profiles;
        this.config = config;
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

    public Map<String, String> getConfig() {
        return config;
    }

    public Path getImageRepoBaseUrl() {
        Map<String, String> cfg = getConfig();
        String s;
        if (cfg == null) {
            return Img.SCION_IMAGE_REPO_BASE;
        } else {
            String imageRepoBaseUrl = cfg.get(IMAGE_REPO_BASE_URL_KEY);
            if (isEmpty(imageRepoBaseUrl)) {
                if (isScion()) {
                    return Img.SCION_IMAGE_REPO_BASE;
                } else {
                    return Img.TOYOTA_IMAGE_REPO_BASE;
                }
            } else {
                return new Path(imageRepoBaseUrl);
            }
        }
    }

    private boolean isScion() {
        return brandKey.isScion();
    }

}
