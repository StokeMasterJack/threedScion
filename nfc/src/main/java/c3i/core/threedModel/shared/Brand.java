package c3i.core.threedModel.shared;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.imageModel.shared.Profile;
import c3i.core.imageModel.shared.Profiles;
import com.google.common.base.Preconditions;
import smartsoft.util.shared.Path;

import java.util.Map;
import java.util.logging.Logger;

import static smartsoft.util.shared.Strings.notEmpty;

public class Brand {

    public static final String IMAGE_BASE_URL_KEY = "imageBaseUrl";

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

    public String getConfigProperty(String propName) {
        Map<String, String> cfg = getConfig();
        String s;
        if (cfg == null) {
            return null;
        } else {
            return cfg.get(propName);
        }
    }

    public Path getImageBaseUrl() {
        log.info("IMAGE_BASE_URL_KEY = " + IMAGE_BASE_URL_KEY);
        String configProperty = getConfigProperty(IMAGE_BASE_URL_KEY);
        log.info("configProperty = " + configProperty);
        if (configProperty == null) {
            return null;
        } else {
            return new Path(configProperty);
        }
    }

    @Override
    public String toString() {
        return "Brand[" + brandKey + "]";
    }

    private boolean isScion() {
        return brandKey.isScion();
    }

    private static Logger log = Logger.getLogger(Brand.class.getName());

}
