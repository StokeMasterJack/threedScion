package com.tms.threedToyota.ebro;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.tms.threed.smartClients.jvm.RepoClient;
import com.tms.threed.threedCore.featureModel.shared.FixResult;
import com.tms.threed.threedCore.imageModel.shared.ImageStack;
import com.tms.threed.threedCore.threedModel.shared.JpgWidth;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import com.tms.threed.threedCore.threedModel.shared.ThreedModel;
import com.tms.threed.threedCore.threedModel.shared.ViewKey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import smartsoft.util.lang.shared.Path;

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
        ImmutableSet<String> iRawPicks;
        if (rawPicks instanceof ImmutableSet) {
            iRawPicks = (ImmutableSet<String>) rawPicks;
        } else {
            iRawPicks = ImmutableSet.copyOf(rawPicks);
        }
        fixResult = this.threedModel.fixupRaw(iRawPicks);

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
        ImageStack imageStack = threedModel.getImageStack(viewName, angle, fixResult.getAssignments());

        ImmutableList<Path> urlsJpgMode = imageStack.getUrlListSmart(JpgWidth.W_STD);

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
