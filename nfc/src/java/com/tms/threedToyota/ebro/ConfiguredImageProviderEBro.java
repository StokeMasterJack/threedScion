package com.tms.threedToyota.ebro;

import com.tms.threed.threedFramework.featureModel.shared.FixResult;
import com.tms.threed.threedFramework.imageModel.shared.IImageStack;
import com.tms.threed.threedFramework.repoClient.RepoClient;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.shared.ViewKey;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import com.tms.threed.threedFramework.util.lang.shared.Path;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *  This provides a bridge (an adapter) between the eBrochure cover page
 *  and the ConfiguredImage subsystem.
 */
public class ConfiguredImageProviderEBro {

    private final ThreedModel threedModel;
    private final FixResult fixResult;

    public ConfiguredImageProviderEBro(SeriesKey seriesKey, Set<String> rawPicks, RepoClient repoClient) {
        log.debug("ConfiguredImageProviderEBro init [" + seriesKey + "]");

        log.debug("Getting cached vtc ThreedModel");
        this.threedModel = repoClient.getCachedVtcThreedModel(seriesKey);

        log.debug("User picks (featureSet) before fixUp: [" + rawPicks + "]");
        fixResult = this.threedModel.fixupPicks2(rawPicks);

        log.debug("User picks (featureSet) after fixUp: [" + fixResult + "]");

    }

    public List<URL> getExteriorConfiguredImages() {
        List<URL> configuredImages = getConfiguredImages(ViewKey.EXTERIOR, ViewKey.HERO_ANGLE);
        log.debug("eBro ExteriorConfiguredImages: " + configuredImages);
        return configuredImages;
    }

    public List<URL> getInteriorConfiguredImages() {
        List<URL> configuredImages = getConfiguredImages(ViewKey.INTERIOR, ViewKey.DASH_ANGLE);
        log.debug("eBro InteriorConfiguredImages: " + configuredImages);
        return configuredImages;
    }

    public List<URL> getConfiguredImages(String viewName, int angle) {
        IImageStack imageStack = threedModel.getImageStack(viewName, angle, fixResult.getAssignments());
        List<Path> urlsJpgMode = imageStack.getUrlsJpgMode();
        ArrayList<URL> urls = new ArrayList<URL>();
        for (Path path : urlsJpgMode) {
            urls.add(pathToUrl(path));
        }

        return urls;
    }

    private static URL pathToUrl(Path path) {
        try {
            return new URL(path.toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Log log = LogFactory.getLog(ConfiguredImageProviderEBro.class);
}
