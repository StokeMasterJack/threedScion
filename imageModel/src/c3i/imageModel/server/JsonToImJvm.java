package c3i.imageModel.server;

import c3i.featureModel.shared.FeatureModel;
import c3i.featureModel.shared.common.SeriesKey;
import c3i.imageModel.shared.ImFeature;
import c3i.imageModel.shared.ImFeatureOrPng;
import c3i.imageModel.shared.ImLayer;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.ImageModel;
import c3i.imageModel.shared.JsonParseException;
import c3i.imageModel.shared.PngShortSha;
import c3i.imageModel.shared.SrcPng;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class JsonToImJvm {

    private final FeatureModel featureModel;

    public static ImageModel parse(FeatureModel featureModel, String imageModelJson) throws JsonParseException {
        checkNotNull(featureModel.getKey());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode imageModeAsJsonNode;
        try {
            imageModeAsJsonNode = mapper.readValue(imageModelJson, JsonNode.class);
        } catch (IOException e) {
            throw new JsonParseException(e);
        }
        return parse(featureModel, imageModeAsJsonNode);
    }

    public static ImageModel parse(FeatureModel featureModel, JsonNode imageModelAsJsonNode) {
        checkNotNull(featureModel.getKey());
        JsonToImJvm parser = new JsonToImJvm(featureModel);
        return parser.parseSeries(imageModelAsJsonNode);
    }

    private JsonToImJvm(FeatureModel featureModel) {
        this.featureModel = featureModel;
    }

    private ImageModel parseSeries(JsonNode jsImageModel) {
        JsonNode jsonArray = jsImageModel.get("views");
        assert jsonArray != null;
        List<ImView> imViews = parseViews(jsonArray);


        SeriesKey seriesKey = featureModel.getKey();
        checkState(seriesKey != null);


        ImageModel imageModel = new ImageModel(0, imViews, seriesKey);
        return imageModel;
    }

    private List<ImView> parseViews(JsonNode jsViews) {
        ArrayList<ImView> imViews = new ArrayList<ImView>();
        for (int iv = 0; iv < jsViews.size(); iv++) {


            JsonNode jsView = jsViews.get(iv);

            String viewName = jsView.get("name").getValueAsText();

            ArrayList<ImLayer> imLayers = new ArrayList<ImLayer>();

            JsonNode jsLayers = jsView.get("layers");

            for (int il = 0; il < jsLayers.size(); il++) {

                JsonNode jsLayer = jsLayers.get(il);


                String layerName = jsLayer.get("name").getValueAsText();
                JsonNode jsFeaturesOrPngs = jsLayer.get("children");


                List<ImFeatureOrPng> imFeatureOrPngs = parseFeaturesOrPngs(3, jsFeaturesOrPngs);
                ImLayer imLayer = new ImLayer(2, layerName, imFeatureOrPngs, false);   //todo df: lift is not being serialized
                imLayers.add(imLayer);
            }
            ImView imView = new ImView(1, viewName, iv, imLayers, null);   //todo df: lift is not being serialized
            imViews.add(imView);
        }
        return imViews;
    }

    public List<ImFeatureOrPng> parseFeaturesOrPngs(int depth, JsonNode jsFeaturesOrPngs) {
        checkNotNull(jsFeaturesOrPngs);
        List<ImFeatureOrPng> imFeatureOrPngs = new ArrayList<ImFeatureOrPng>();
        for (int i = 0; i < jsFeaturesOrPngs.size(); i++) {
            JsonNode jsFeatureOrPng = jsFeaturesOrPngs.get(i);

            ImFeatureOrPng featureOrPng = parseFeatureOrPng(depth, jsFeatureOrPng);
            imFeatureOrPngs.add(featureOrPng);
        }
        return imFeatureOrPngs;
    }

    public ImFeatureOrPng parseFeatureOrPng(int depth, JsonNode jsFeatureOrPng) {

        if (jsFeatureOrPng.isObject()) {
            return parseFeature(depth, jsFeatureOrPng);
        } else if (jsFeatureOrPng.isArray()) {
            return parsePng(depth, jsFeatureOrPng);
        } else {
            log.severe("jsFeatureOrPng should be a Object or an Array. This is not either: ");
            log.severe("jsFeatureOrPng: [" + jsFeatureOrPng + "]");
            log.severe("jsFeatureOrPng.toString(): [" + jsFeatureOrPng.toString() + "]");
            throw new IllegalArgumentException("jsFeatureOrPng should be a Object or an Array");
        }

    }

    private ImFeatureOrPng parsePng(int depth, JsonNode jsPng) {
        JsonNode a = jsPng;


        int angle = (int) a.get(0).getIntValue();
        String shortSha = a.get(1).getTextValue();


        return new SrcPng(depth, angle, new PngShortSha(shortSha));
    }


    private ImFeature parseFeature(int depth, JsonNode jsFeature) {

        String var = jsFeature.getFieldNames().next();
        if (var == null) {
            throw new IllegalStateException("varCode[" + var + "] is not contained in fm[" + featureModel + "]");
        }
        JsonNode jsFeaturesOrPngs = jsFeature.get(var);
        List<ImFeatureOrPng> imFeatureOrPngs = parseFeaturesOrPngs(depth, jsFeaturesOrPngs);
        return new ImFeature(depth, var, imFeatureOrPngs);
    }

    private static Logger log = Logger.getLogger(JsonToImJvm.class.getName());

}
