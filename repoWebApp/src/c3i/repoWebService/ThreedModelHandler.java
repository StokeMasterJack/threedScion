package c3i.repoWebService;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.core.threedModel.server.TmToJsonJvm;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.Repos;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.Closeables;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import smartsoft.util.servlet.http.headers.CacheUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPOutputStream;

/**
 * gzip/Level 3 issue:
 *
 * Had some problems with Level 3 (Scion's CDN) because
 * we were serving gzip response even if the client's request
 * indicated he didn't want gzip via an Accept Encoding
 * request header (or lack there of). Level 3 choked on this.
 *
 *
 * The situation we have here is caused by our use of two semi-unrelated optimization techniques:
 *
 *      1.  CDN caching which generally uses the url as the cache key
 *      2.  gzip response content (but only sometimes, based on client's ability to support gzip)
 *
 * When hard-coded to always gzip the response (as it was before), all is good in caching land:
 * The content is a function of the cache key (url)
 * But after adding support for gzip and non-gzip clients
 * (Level 3 forced this upon us)
 * the content is no longer a function of the url alone.
 * It now depend upon url+content-encoding.
 *
 * Akamai and Amazon CloudFront both handle this situation
 * correctly (albiet in different ways).
 *
 * Here was the fix i used:
 *
 *      if(brand is toyota) i.e. cdn is akamai
 *           leave it the same.
 *           this is because Akamai is super smart with regard to gzip.
 *           if the client can't handle gzip content, akamai actually
 *           unzips it and then serves it.
 *           Akamai seems to be working great and i don't want to change anything.
 *      else if (brand is scion) i.e. cdn is Level 3
 *          obey the client's acceptEncoding serving gzip of appropriate
 *          hopefully Level 3 caches both copies (like CloudFrom does)
 *
 */
public class ThreedModelHandler extends RepoHandler<ThreedModelRequest> {

    private final LoadingCache<SeriesId, byte[]> jsonMapGzipped;
    private final LoadingCache<SeriesId, byte[]> jsonMapNotZipped;

    public ThreedModelHandler(BrandRepos repos) {
        super(repos);


        jsonMapGzipped = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(1000)
                .build(
                        new CacheLoader<SeriesId, byte[]>() {
                            public byte[] load(SeriesId seriesId) {
                                return createGzippedJson(seriesId);
                            }
                        });


        jsonMapNotZipped = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(1000)
                .build(
                        new CacheLoader<SeriesId, byte[]>() {
                            public byte[] load(SeriesId seriesId) {
                                return createJson(seriesId);
                            }
                        });


    }

    //Accept-Encoding:gzip,deflate,sdch
    //Accept-Encoding: gzip;q=0,identity;q=1, deflate;q=0
    private boolean clientAcceptsGzip(ThreedModelRequest repoRequest) {

        boolean akamai = repoRequest.getSeriesKey().getBrandKey().isToyota();

        if (akamai) {
            return true; //akamai handles gzip well
        }

        //this is for scion's Level 3 CDN

        String acceptEncoding = repoRequest.getRequest().getHeader("Accept-Encoding");
        if (acceptEncoding == null) {
            return false;
        }
        acceptEncoding = acceptEncoding.toLowerCase();

        if (!acceptEncoding.contains("gzip")) {
            return false;
        }

        if (acceptEncoding.contains("gzip;q=0")) {
            return false;
        }

        if (acceptEncoding.contains("deflate;q=0")) {
            return false;
        }

        if (acceptEncoding.contains("identity;q=1")) {
            return false;
        }

        return true;
    }

    @Override
    public void handle(ThreedModelRequest repoRequest) {
        SeriesId seriesId = repoRequest.getSeriesId();

        log.debug("Received request for ThreedModel[" + seriesId.toString() + "]");


        boolean gzip = clientAcceptsGzip(repoRequest);

        byte[] retVal;
        try {
            if (gzip) {
                retVal = jsonMapGzipped.get(seriesId);
            } else {
                retVal = jsonMapNotZipped.get(seriesId);
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }


        HttpServletResponse response = repoRequest.getResponse();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        CacheUtil.addCacheForeverResponseHeaders(response);


        if (gzip) {
            response.setHeader("Content-Encoding", "gzip");
        }

        response.setContentLength(retVal.length);

        response.setHeader("X-Content-Type-Options", "nosniff");


        try {
            ServletOutputStream out = response.getOutputStream();
            out.write(retVal);
            out.flush();
        } catch (IOException e) {
            throw new NotFoundException("Problem streaming jsonText to client", e);
        }


    }

    private byte[] createJson(SeriesId seriesId) {
        BrandKey brandKey = seriesId.getSeriesKey().getBrandKey();
        Repos repos = getRepos(brandKey);
        ThreedModel threedModel = repos.getThreedModel(seriesId);
        String jsonText = TmToJsonJvm.toJson(threedModel, false);
        byte[] jsonBytes = jsonText.getBytes(Charset.forName("UTF-8"));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            os.write(jsonBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return os.toByteArray();
    }

    private byte[] createGzippedJson(SeriesId seriesId) {
        BrandKey brandKey = seriesId.getSeriesKey().getBrandKey();
        Repos repos = getRepos(brandKey);
        ThreedModel threedModel = repos.getThreedModel(seriesId);
        String jsonText = TmToJsonJvm.toJson(threedModel, false);
        byte[] jsonBytes = jsonText.getBytes(Charset.forName("UTF-8"));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = null;
        try {
            try {
                gzipOut = new GZIPOutputStream(os);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            try {
                gzipOut.write(jsonBytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } finally {
            Closeables.closeQuietly(gzipOut);
        }


        return os.toByteArray();
    }

    protected static Log log = LogFactory.getLog(ThreedModelHandler.class);


}
