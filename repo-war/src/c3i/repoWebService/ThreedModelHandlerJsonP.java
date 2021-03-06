package c3i.repoWebService;

import c3i.repo.server.TmToJsonJvm;
import c3i.threedModel.client.ThreedModel;
import c3i.featureModel.shared.common.BrandKey;
import c3i.featureModel.shared.common.SeriesId;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.BrandRepo;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.Closeables;
import java.util.logging.Logger;

import smartsoft.util.servlet.http.headers.CacheUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPOutputStream;

public class ThreedModelHandlerJsonP extends RepoHandler<ThreedModelRequest> {

    protected final LoadingCache<Key, byte[]> jsonMap;

    public static class Key{
        SeriesId seriesId;
        String callback;

        public Key(SeriesId seriesId, String callback) {
            this.seriesId = seriesId;
            this.callback = callback;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (!callback.equals(key.callback)) return false;
            if (!seriesId.equals(key.seriesId)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = seriesId.hashCode();
            result = 31 * result + callback.hashCode();
            return result;
        }

        public SeriesId getSeriesId() {
            return seriesId;
        }

        public String getCallback() {
            return callback;
        }
    }

    public ThreedModelHandlerJsonP(BrandRepos brandRepos) {
        super(brandRepos);

        jsonMap = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(1000)
                .build(
                        new CacheLoader<Key, byte[]>() {
                            public byte[] load(Key key) {
                                return createGzippedJson(key);
                            }
                        });


    }

    @Override
    public void handle(ThreedModelRequest repoRequest) {
        SeriesId seriesId = repoRequest.getSeriesId();

        String callback = repoRequest.getRequest().getParameter("callback");

        Key key = new Key(seriesId, callback);

        log.fine("ThreedModelHandlerJsonP: Received request for ThreedModel[" + seriesId.toString() + "]");
        byte[] retVal = new byte[0];
        try {
            retVal = jsonMap.get(key);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }


        HttpServletResponse response = repoRequest.getResponse();
        response.setContentType("text/javascript");
        response.setCharacterEncoding("UTF-8");

        CacheUtil.addCacheForeverResponseHeaders(response);

        response.setHeader("Content-Encoding", "gzip");
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

    private byte[] createGzippedJson(Key key) {
        SeriesId seriesId = key.getSeriesId();
        String callback = key.getCallback();
        BrandKey brandKey = seriesId.getSeriesKey().getBrandKey();
        BrandRepo brandRepo = getRepos(brandKey);
        ThreedModel threedModel = brandRepo.getThreedModel(seriesId);
        String jsonText = TmToJsonJvm.toJson(threedModel, callback);
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

    protected static Logger log = Logger.getLogger(ThreedModelHandler.class.getName());


}
