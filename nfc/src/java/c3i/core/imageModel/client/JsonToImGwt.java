package c3i.core.imageModel.client;

import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.imageModel.shared.ImFeature;
import c3i.core.imageModel.shared.ImFeatureOrPng;
import c3i.core.imageModel.shared.ImLayer;
import c3i.core.imageModel.shared.ImSeries;
import c3i.core.imageModel.shared.ImView;
import c3i.core.imageModel.shared.PngShortSha;
import c3i.core.imageModel.shared.SimpleFeatureModel;
import c3i.core.imageModel.shared.SrcPng;
import c3i.core.imageModel.shared.ViewLiftSpec;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonToImGwt {

    private final SimpleFeatureModel featureModel;

    private JsonToImGwt(SimpleFeatureModel featureModel) {
        this.featureModel = featureModel;
    }

    public static ImSeries parse(SimpleFeatureModel featureModel, JsImageModel jsImageModel) {
        JsonToImGwt parser = new JsonToImGwt(featureModel);
        return parser.parseSeries(jsImageModel);
    }

    public static ImSeries parse(SimpleFeatureModel featureModel, String imageModelAsJsonText) {
        JsonToImGwt parser = new JsonToImGwt(featureModel);
        JsImageModel jsImageModel = getJsImageModelFromJsonText(imageModelAsJsonText);
        return parser.parseSeries(jsImageModel);
    }

    private static native JsImageModel getJsImageModelFromJsonText(String jsonText) /*-{
        eval("var xTmp = " + jsonText);
        return xTmp;
    }-*/;

    private ImSeries parseSeries(JsImageModel jsImageModel) {
        JSONArray jsonArray = jsImageModel.getViews();
        assert jsonArray != null;
        List<ImView> imViews = parseViews(jsonArray);
        ImSeries imSeries = new ImSeries(0, imViews, featureModel.getSeriesKey());
        return imSeries;
    }

    private List<ImView> parseViews(JSONArray jsViews) {
        ArrayList<ImView> imViews = new ArrayList<ImView>();
        for (int iv = 0; iv < jsViews.size(); iv++) {
            JSONObject jsView = jsViews.get(iv).isObject();
            ImView view = parseView(jsView, iv);
            imViews.add(view);
        }
        return imViews;
    }

    ImView parseView(JSONObject jsView, int iv) {
        JSONValue jsName = jsView.get("name");
        if (jsName == null) {
            log.severe("jsView json node does not have a viewName: " + jsView);
            throw new IllegalStateException("jsView json node does not have a viewName: " + jsView);
        }
        String name = jsName.isString().stringValue();
        JSONArray jsLayers = jsView.get("layers").isArray();
        JSONValue jsLift = jsView.get("lift");
        ViewLiftSpec viewLiftSpec;
        if (jsLift != null) {
            viewLiftSpec = parseViewLiftSpec(jsLift.isObject());
        } else {
            viewLiftSpec = null;
        }
        return new ImView(1, name, iv, parseLayers(jsLayers), viewLiftSpec);
    }

    List<ImLayer> parseLayers(JSONArray jsLayers) {
        ArrayList<ImLayer> a = new ArrayList<ImLayer>();
        for (int il = 0; il < jsLayers.size(); il++) {
            JSONObject jsLayer = jsLayers.get(il).isObject();
            ImLayer imLayer = parseLayer(jsLayer);
            a.add(imLayer);
        }
        return a;
    }

    ImLayer parseLayer(JSONObject jsLayer) {
        String layerName = jsLayer.get("name").isString().stringValue();
        boolean lift = jsLayer.get("lift").isBoolean().booleanValue();
        JSONArray jsFeaturesOrPngs = jsLayer.get("children").isArray();
        List<ImFeatureOrPng> featureOrPngs = parseFeaturesOrPngs(3, jsFeaturesOrPngs);
        return new ImLayer(2, layerName, featureOrPngs, lift);
    }


    ViewLiftSpec parseViewLiftSpec(JSONObject jsLift) {
        String triggerFeatureVarCode = jsLift.get("triggerFeature").isString().stringValue();
        Var triggerFeature = featureModel.get(triggerFeatureVarCode);
        int deltaY = (int) jsLift.get("deltaY").isNumber().doubleValue();
        return new ViewLiftSpec(triggerFeature, deltaY);
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
            log.log(Level.SEVERE, "jsFeatureOrPng should be a Object or an Array. This is not either: ");
            log.log(Level.SEVERE, "\t jsFeatureOrPng: [" + jsFeatureOrPng + "]");
            log.log(Level.SEVERE, "\t jsFeatureOrPng.toString(): [" + jsFeatureOrPng.toString() + "]");
            throw new IllegalArgumentException("jsFeatureOrPng should be a Object or an Array: ");
        }

    }

    private ImFeatureOrPng parsePng(int depth, JSONArray jsPng) {
        JSONArray a = jsPng.isArray();


        int angle = (int) a.get(0).isNumber().doubleValue();
        String shortSha = a.get(1).isString().stringValue();

        boolean blink = a.size() > 2;

        return new SrcPng(depth, angle, new PngShortSha(shortSha), blink);
    }


    private ImFeature parseFeature(int depth, JSONObject jsFeature) {
        String varCode = jsFeature.keySet().iterator().next();
        Var var = featureModel.get(varCode);
        JSONArray jsFeaturesOrPngs = jsFeature.get(varCode).isArray();
        List<ImFeatureOrPng> imFeatureOrPngs = parseFeaturesOrPngs(depth, jsFeaturesOrPngs);
        return new ImFeature(depth, var, imFeatureOrPngs);
    }

    private static Logger log = Logger.getLogger(JsonToImGwt.class.getName());

}
