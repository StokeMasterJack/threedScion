package threed.repoWebService;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import threed.repo.server.Repos;
import smartsoft.util.servlet.http.headers.CacheUtil;
import threed.core.threedModel.shared.SeriesId;
import threed.core.threedModel.server.TmToJsonJvm;
import threed.core.threedModel.shared.ThreedModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPOutputStream;

public class ThreedModelHandler extends RepoHandler<ThreedModelRequest> {

    private final LoadingCache<SeriesId, byte[]> jsonMap;

    public ThreedModelHandler(Repos repos, ServletContext application) {
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

        log.debug("Received request for ThreedModel[" + seriesId.toString() + "]");
        byte[] retVal = new byte[0];
        try {
            retVal = jsonMap.get(seriesId);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }


        HttpServletResponse response = repoRequest.getResponse();
        response.setContentType("application/json");

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
        String threedModelJsonText = TmToJsonJvm.marshal(threedModel);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut;
        try {
            gzipOut = new GZIPOutputStream(os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        PrintWriter out = new PrintWriter(gzipOut);
        out.print(threedModelJsonText);

        out.close();

        return os.toByteArray();
    }

    protected static Log log = LogFactory.getLog(ThreedModelHandler.class);


}
