package c3i.core.imageModel.client;

import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.imageModel.shared.ImFeature;
import c3i.core.imageModel.shared.ImFeatureOrPng;
import c3i.core.imageModel.shared.ImLayer;
import c3i.core.imageModel.shared.ImSeries;
import c3i.core.imageModel.shared.ImView;
import c3i.core.imageModel.shared.PngShortSha;
import c3i.core.imageModel.shared.SrcPng;
import c3i.core.imageModel.shared.ViewLiftSpec;
import c3i.core.threedModel.shared.SeriesInfo;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import smartsoft.util.gwt.client.Console;

import java.util.ArrayList;
import java.util.List;

public class JsonUnmarshallerIm {

    private final FeatureModel featureModel;

    public JsonUnmarshallerIm(FeatureModel featureModel) {
        this.featureModel = featureModel;
    }

    public ImSeries parseSeries(JsImageModel jsImageModel) {
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
            ImView view = parseView(jsView,iv);
            imViews.add(view);
        }
        return imViews;
    }

    ImView parseView(JSONObject jsView, int iv) {
        JSONValue jsName = jsView.get("name");
        if(jsName==null){
            Console.error("jsView json node does not have a viewName: " + jsView);
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
        return new ImView(1, name, iv,parseLayers(jsLayers), viewLiftSpec);
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
        return new ImLayer(2, layerName, featureOrPngs,lift);
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
            Console.error("jsFeatureOrPng should be a Object or an Array. This is not either: ");
            Console.error("\t jsFeatureOrPng: [" + jsFeatureOrPng + "]");
            Console.error("\t jsFeatureOrPng.toString(): [" + jsFeatureOrPng.toString() + "]");
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


}
