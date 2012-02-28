package com.tms.threed.threedFramework.imageModel.server;

import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.imageModel.shared.ImFeature;
import com.tms.threed.threedFramework.imageModel.shared.ImFeatureOrPng;
import com.tms.threed.threedFramework.imageModel.shared.ImLayer;
import com.tms.threed.threedFramework.imageModel.shared.ImPng;
import com.tms.threed.threedFramework.imageModel.shared.ImSeries;
import com.tms.threed.threedFramework.imageModel.shared.ImView;
import com.tms.threed.threedFramework.imageModel.shared.PngShortSha;
import com.tms.threed.threedFramework.threedModel.shared.SeriesInfo;
import com.tms.threed.threedFramework.util.gwtUtil.client.Console;
import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class JsonUnmarshallerIm {

    private final FeatureModel featureModel;
    private final SeriesInfo seriesInfo;

    public JsonUnmarshallerIm(FeatureModel featureModel, SeriesInfo seriesInfo) {
        this.featureModel = featureModel;
        this.seriesInfo = seriesInfo;
    }

    public ImSeries parseSeries(JsonNode jsImageModel) {
        JsonNode jsonArray = jsImageModel.get("views");
        assert jsonArray != null;
        List<ImView> imViews = parseViews(jsonArray);
        ImSeries imSeries = new ImSeries(0, imViews, seriesInfo);
        return imSeries;
    }

    private List<ImView> parseViews(JsonNode jsViews) {
        ArrayList<ImView> imViews = new ArrayList<ImView>();
        for (int iv = 0; iv < jsViews.size(); iv++) {


            JsonNode jsView = jsViews.get(iv);

            String viewName = jsView.getFieldNames().next();


            ArrayList<ImLayer> imLayers = new ArrayList<ImLayer>();

            JsonNode jsLayers = jsView.get(viewName);

            for (int il = 0; il < jsLayers.size(); il++) {

                JsonNode jsLayer = jsLayers.get(il);


                String layerName = jsLayer.getFieldNames().next();
                JsonNode jsFeaturesOrPngs = jsLayer.get(layerName);


                List<ImFeatureOrPng> imFeatureOrPngs = parseFeaturesOrPngs(3, jsFeaturesOrPngs);
                ImLayer imLayer = new ImLayer(2, layerName, imFeatureOrPngs);
                imLayers.add(imLayer);
            }
            ImView imView = new ImView(1, viewName, imLayers);
            imViews.add(imView);
        }
        return imViews;
    }

    public List<ImFeatureOrPng> parseFeaturesOrPngs(int depth, JsonNode jsFeaturesOrPngs) {
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
            Console.error("jsFeatureOrPng should be a Object or an Array. This is not either: ");
            Console.error("jsFeatureOrPng: [" + jsFeatureOrPng + "]");
            Console.error("jsFeatureOrPng.toString(): [" + jsFeatureOrPng.toString() + "]");
            throw new IllegalArgumentException("jsFeatureOrPng should be a Object or an Array");
        }

    }

    private ImFeatureOrPng parsePng(int depth, JsonNode jsPng) {
        JsonNode a = jsPng;


        int angle = (int) a.get(0).getIntValue();
        String shortSha = a.get(1).getTextValue();

        boolean blink = a.size() > 2;

        System.out.println("blink C = " + blink);

        return new ImPng(depth, angle, new PngShortSha(shortSha), blink);
    }


    private ImFeature parseFeature(int depth, JsonNode jsFeature) {

        String varCode = jsFeature.getFieldNames().next();


        Var var = featureModel.get(varCode);
        JsonNode jsFeaturesOrPngs = jsFeature.get(varCode);
        List<ImFeatureOrPng> imFeatureOrPngs = parseFeaturesOrPngs(depth, jsFeaturesOrPngs);
        return new ImFeature(depth, var, imFeatureOrPngs);
    }

}
