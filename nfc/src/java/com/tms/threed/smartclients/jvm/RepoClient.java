package com.tms.threed.smartClients.jvm;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.Resources;
import com.tms.threed.threedCore.threedModel.shared.RootTreeId;
import com.tms.threed.threedCore.threedModel.shared.SeriesId;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import com.tms.threed.threedCore.threedModel.shared.ThreedModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import smartsoft.util.lang.shared.Path;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RepoClient {

    private final LoadingCache<SeriesId, ThreedModel> threedModelCache;
    private final LoadingCache<SeriesKey, RootTreeId> vtcCache;

    private final Path repoBaseUrlReverseProxy;

    public RepoClient(Path repoBaseUrlReverseProxy) {
        if (repoBaseUrlReverseProxy == null) {
            throw new IllegalArgumentException("repoBaseUrl must be non-null");
        }

        if (!repoBaseUrlReverseProxy.isHttpUrl()) {
            throw new IllegalArgumentException("Invalid repoUrl[" + repoBaseUrlReverseProxy + "]");
        }


        this.repoBaseUrlReverseProxy = repoBaseUrlReverseProxy;


        threedModelCache = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(1000)
                .build(
                        new CacheLoader<SeriesId, ThreedModel>() {
                            @Override
                            public ThreedModel load(SeriesId seriesId) throws Exception {
                                try {
                                    return getThreedModel(seriesId);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });


        vtcCache = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(1000)
                .expireAfterWrite(24, TimeUnit.HOURS)
                .build(
                        new CacheLoader<SeriesKey, RootTreeId>() {
                            public RootTreeId load(SeriesKey seriesKey) throws Exception {
                                try {
                                    return getVtc(seriesKey);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });


    }


    public ThreedModel getThreedModel(SeriesId seriesId) throws IOException {
        URL threedModelUrl = getThreedModelUrl(seriesId);
        log.info("RepoClient - Using threedModelUrl[" + threedModelUrl + "]");
        JsonUnmarshallerTm u = new JsonUnmarshallerTm();
        ThreedModel threedModel = u.createModelFromJs(seriesId.getSeriesKey(), threedModelUrl);
        threedModel.setRepoBaseUrl(repoBaseUrlReverseProxy);
        return threedModel;
    }

    /**
     * http://localhost:8080/configurator-content/avalon/2011/3d/models/2c05ba6f8d52e4ba85ae650756dc2d1423d9395d.json
     */
    public URL getThreedModelUrl(SeriesId seriesId) {

        String n = seriesId.getName();
        int y = seriesId.getYear();
        String sha = seriesId.getRootTreeId().getName();

        Path p = new Path(n).append(y + "").append("3d").append("models").append(sha).appendName(".json");
        try {
            String sUrl = repoBaseUrlReverseProxy.append(p).toString();
            return new URL(sUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * http://localhost:8080/configurator-content/avalon/2011/vtc.txt
     * @return
     */
    public URL getVtcUrl(SeriesKey seriesKey) {

        if (repoBaseUrlReverseProxy == null) {
            throw new IllegalStateException("repoUrl must be non-null before calling getVtcUrl(..)");
        }

        if (!repoBaseUrlReverseProxy.isHttpUrl()) {
            throw new IllegalStateException("Invalid repoUrl[" + repoBaseUrlReverseProxy + "]");
        }

        String n = seriesKey.getName();
        int y = seriesKey.getYear();
        Path p = new Path(n).append(y + "").append("vtc").appendName(".txt");
        try {
            String sUrl = repoBaseUrlReverseProxy.append(p).toString();
            return new URL(sUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


    public RootTreeId getVtc(SeriesKey seriesKey) throws IOException {
        URL vtcUrl = getVtcUrl(seriesKey);
        log.info("RepoClient - Using vtcUrl[" + vtcUrl + "]");
        String rootTreeId;
        try {
            rootTreeId = Resources.toString(vtcUrl, Charset.defaultCharset());
        } catch (IOException e) {
            log.error("RepoClient - failed to load url [" + vtcUrl + "]", e);
            throw e;
        }
        return new RootTreeId(rootTreeId);
    }

    public ThreedModel getCachedThreedModel(SeriesId seriesId) {
        try {
            return threedModelCache.get(seriesId);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public RootTreeId getCachedVtc(SeriesKey seriesKey) {
        try {
            return vtcCache.get(seriesKey);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearThreedModelCache() {
        threedModelCache.invalidateAll();
    }

    public void clearVtcCache() {
        vtcCache.invalidateAll();
    }

    public ThreedModel getCachedVtcThreedModel(SeriesKey seriesKey) {
        RootTreeId cachedVtc = getCachedVtc(seriesKey);
        SeriesId seriesId = new SeriesId(seriesKey, cachedVtc);
        return getCachedThreedModel(seriesId);
    }


    private static Log log = LogFactory.getLog(RepoClient.class);


}
