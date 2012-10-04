package c3i.smartClientJvm;

import com.google.common.io.Closeables;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.server.JsonToFmJvm;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.imageModel.server.JsonToImJvm;
import c3i.core.imageModel.shared.ImSeries;
import c3i.core.threedModel.shared.SeriesInfoBuilder;
import c3i.core.threedModel.shared.ThreedModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class JsonUnmarshallerTm {

    /**
     * null rootTreeId implies "created from workDir"
     */
    public ThreedModel createModelFromJs(SeriesKey seriesKey, URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        try {
            urlConnection.connect();
        } catch (IOException e) {
            log.error("RepoClient - urlConnection.connect() failed for url[" + url + "] ", e);
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


        JsonToFmJvm uFm = new JsonToFmJvm();
        FeatureModel fm = uFm.parseJson(seriesKey, jsFm);


        JsonToImJvm uIm = new JsonToImJvm(fm);
        ImSeries im = uIm.parseSeries(jsIm);

        ThreedModel threedModel = new ThreedModel(fm, im);
        return threedModel;

    }

    private static Log log = LogFactory.getLog(JsonUnmarshallerTm.class);


}