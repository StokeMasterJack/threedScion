package com.tms.threed.repoServlets.web;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.servletUtil.http.headers.CacheUtil;
import com.tms.threed.threedFramework.threedCore.shared.SeriesId;
import com.tms.threed.threedFramework.threedModel.server.JsonMarshallerTm;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.GZIPOutputStream;

public class ThreedModelHandler extends RepoHandler<ThreedModelRequest> {

    private final ConcurrentMap<SeriesId, byte[]> jsonMap;

    public ThreedModelHandler(Repos repos, ServletContext application) {
        super(repos, application);


        jsonMap = new MapMaker()
                .concurrencyLevel(4)
                .maximumSize(1000)
                .makeComputingMap(
                        new Function<SeriesId, byte[]>() {
                            public byte[] apply(SeriesId seriesId) {
                                return createGzippedJson(seriesId);
                            }
                        });


    }

    @Override
    public void handle(ThreedModelRequest repoRequest) {
        SeriesId seriesId = repoRequest.getSeriesId();


        byte[] retVal = jsonMap.get(seriesId);


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
        String threedModelJsonText = JsonMarshallerTm.marshal(threedModel);

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


}
