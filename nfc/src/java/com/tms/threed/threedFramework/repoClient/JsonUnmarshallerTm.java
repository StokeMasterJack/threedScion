package com.tms.threed.threedFramework.repoClient;

import com.google.common.io.Closeables;
import com.tms.threed.threedFramework.featureModel.server.JsonUnmarshallerFm;
import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.imageModel.server.JsonUnmarshallerIm;
import com.tms.threed.threedFramework.imageModel.shared.ImSeries;
import com.tms.threed.threedFramework.threedCore.shared.SeriesInfoBuilder;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class JsonUnmarshallerTm {

    public ThreedModel createModelFromJs(SeriesKey seriesKey, URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        try {
            urlConnection.connect();
        } catch (IOException e) {
            log.error("RepoClient - urlConnection.connect() failed for url["+url+"] ",e);
            throw e;
        }

        String encoding = urlConnection.getContentEncoding();

        InputStream is = null;

        JsonNode jsThreedModel;
        try {
            if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
                is = new GZIPInputStream(urlConnection.getInputStream());
            } else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
                is = new InflaterInputStream(urlConnection.getInputStream(), new Inflater(true));
            } else {
                is = urlConnection.getInputStream();
            }

            ObjectMapper mapper = new ObjectMapper();
            jsThreedModel = mapper.readValue(is, JsonNode.class);
        } finally {
            Closeables.closeQuietly(is);
        }


        JsonNode jsFm = jsThreedModel.get("featureModel");
        JsonNode jsIm = jsThreedModel.get("imageModel");


        JsonUnmarshallerFm uFm = new JsonUnmarshallerFm();
        FeatureModel fm = uFm.parseJson(seriesKey, jsFm);


        JsonUnmarshallerIm uIm = new JsonUnmarshallerIm(fm, SeriesInfoBuilder.createSeriesInfo(seriesKey));
        ImSeries im = uIm.parseSeries(jsIm);

        ThreedModel threedModel = new ThreedModel(fm, im);
        return threedModel;

    }

    private static Log log = LogFactory.getLog(JsonUnmarshallerTm.class);


}