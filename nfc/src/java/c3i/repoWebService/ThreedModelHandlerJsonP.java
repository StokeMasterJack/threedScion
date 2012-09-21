package c3i.repoWebService;

import c3i.core.common.shared.SeriesId;
import c3i.core.threedModel.server.TmToJsonJvm;
import c3i.core.threedModel.shared.ThreedModel;
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

public class ThreedModelHandlerJsonP extends RepoHandler<ThreedModelRequest> {

    protected final LoadingCache<SeriesId, byte[]> jsonMap;

    public ThreedModelHandlerJsonP(Repos repos, ServletContext application) {
        super(repos, application);


        jsonMap = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(1000)
                .build(
                        new CacheLoader<SeriesId, byte[]>() {
                            public byte[] load(SeriesId seriesId) {
                                return createGzippedJson(seriesId);
                            }
                        });


    }

    @Override
    public void handle(ThreedModelRequest repoRequest) {
        SeriesId seriesId = repoRequest.getSeriesId();

        log.debug("ThreedModelHandlerJsonP: Received request for ThreedModel[" + seriesId.toString() + "]");
        byte[] retVal = new byte[0];
        try {
            retVal = jsonMap.get(seriesId);
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

    private byte[] createGzippedJson(SeriesId seriesId) {
        ThreedModel threedModel = repos.getThreedModel(seriesId);
        String jsonText = TmToJsonJvm.toJson(threedModel, true);
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
