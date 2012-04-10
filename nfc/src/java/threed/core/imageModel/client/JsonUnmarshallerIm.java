package threed.core.imageModel.client;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import threed.core.featureModel.shared.FeatureModel;
import threed.core.featureModel.shared.boolExpr.Var;
import threed.core.imageModel.shared.ImFeature;
import threed.core.imageModel.shared.ImFeatureOrPng;
import threed.core.imageModel.shared.ImLayer;
import threed.core.imageModel.shared.ImPng;
import threed.core.imageModel.shared.ImSeries;
import threed.core.imageModel.shared.ImView;
import threed.core.imageModel.shared.PngShortSha;
import threed.core.threedModel.shared.SeriesInfo;
import smartsoft.util.gwt.client.Console;

import java.util.ArrayList;
import java.util.List;

public class JsonUnmarshallerIm {

    private final FeatureModel featureModel;
    private final SeriesInfo seriesInfo;

    public JsonUnmarshallerIm(FeatureModel featureModel, SeriesInfo seriesInfo) {
        this.featureModel = featureModel;
        this.seriesInfo = seriesInfo;
    }

    public ImSeries parseSeries(JsImageModel jsImageModel) {
        JSONArray jsonArray = jsImageModel.getViews();
        assert jsonArray != null;
        List<ImView> imViews = parseViews(jsonArray);
        ImSeries imSeries = new ImSeries(0, imViews, seriesInfo);
        return imSeries;
    }

    private List<ImView> parseViews(JSONArray jsViews) {
        ArrayList<ImView> imViews = new ArrayList<ImView>();
        for (int iv = 0; iv < jsViews.size(); iv++) {
            JSONObject jsView = jsViews.get(iv).isObject();
            String viewName = jsView.keySet().iterator().next();
            ArrayList<ImLayer> imLayers = new ArrayList<ImLayer>();
            JSONArray jsLayers = jsView.get(viewName).isArray();
            for (int il = 0; il < jsLayers.size(); il++) {
                JSONObject jsLayer = jsLayers.get(il).isObject();
                String layerName = jsLayer.keySet().iterator().next();
                JSONArray jsFeaturesOrPngs = jsLayer.get(layerName).isArray();
                List<ImFeatureOrPng> imFeatureOrPngs = parseFeaturesOrPngs(3, jsFeaturesOrPngs);
                ImLayer imLayer = new ImLayer(2, layerName, imFeatureOrPngs);
                imLayers.add(imLayer);
            }
            ImView imView = new ImView(1, viewName, imLayers);
            imViews.add(imView);
        }
        return imViews;
    }

    public List<ImFeatureOrPng> parseFeaturesOrPngs(int depth, JSONArray jsFeaturesOrPngs) {
        List<ImFeatureOrPng> imFeatureOrPngs = new ArrayList<ImFeatureOrPng>();
        for (int i = 0; i < jsFeaturesOrPngs.size(); i++) {
            JSONValue jsFeatureOrPng = jsFeaturesOrPngs.get(i);

            ImFeatureOrPng featureOrPng = parseFeatureOrPng(depth, jsFeatureOrPng);
            imFeatureOrPngs.add(featureOrPng);
        }
        return imFeatureOrPngs;
    }

    public ImFeatureOrPng parseFeatureOrPng(int depth, JSONValue jsFeatureOrPng) {

        if (jsFeatureOrPng.isObject() != null) {
            return parseFeature(depth, jsFeatureOrPng.isObject());
        } else if (jsFeatureOrPng.isArray() != null) {
            return parsePng(depth, jsFeatureOrPng.isArray());
        } else {
            Console.error("jsFeatureOrPng should be a Object or an Array. This is not either: ");
            Console.error("jsFeatureOrPng: [" + jsFeatureOrPng + "]");
            Console.error("jsFeatureOrPng.toString(): [" + jsFeatureOrPng.toString() + "]");
            throw new IllegalArgumentException("jsFeatureOrPng should be a Object or an Array");
        }

    }

    private ImFeatureOrPng parsePng(int depth, JSONArray jsPng) {
        JSONArray a = jsPng.isArray();


        int angle = (int) a.get(0).isNumber().doubleValue();
        String shortSha = a.get(1).isString().stringValue();

        boolean blink = a.size() > 2;

        return new ImPng(depth, angle, new PngShortSha(shortSha), blink);
    }


    private ImFeature parseFeature(int depth, JSONObject jsFeature) {
        String varCode = jsFeature.keySet().iterator().next();
        Var var = featureModel.get(varCode);
        JSONArray jsFeaturesOrPngs = jsFeature.get(varCode).isArray();
        List<ImFeatureOrPng> imFeatureOrPngs = parseFeaturesOrPngs(depth, jsFeaturesOrPngs);
        return new ImFeature(depth, var, imFeatureOrPngs);
    }

}
