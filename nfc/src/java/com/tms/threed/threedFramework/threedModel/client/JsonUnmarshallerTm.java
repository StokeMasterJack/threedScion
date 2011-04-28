package com.tms.threed.threedFramework.threedModel.client;

import com.tms.threed.threedFramework.featureModel.client.JsFeatureModel;
import com.tms.threed.threedFramework.featureModel.client.JsonUnmarshallerFm;
import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.imageModel.client.JsImageModel;
import com.tms.threed.threedFramework.imageModel.client.JsonUnmarshallerIm;
import com.tms.threed.threedFramework.imageModel.shared.ImSeries;
import com.tms.threed.threedFramework.threedCore.shared.SeriesInfo;
import com.tms.threed.threedFramework.threedCore.shared.SeriesInfoBuilder;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import com.tms.threed.threedFramework.util.gwtUtil.client.Console;
import com.tms.threed.threedFramework.util.lang.shared.Path;

public class JsonUnmarshallerTm {

    public static ThreedModel createModelFromJsInPage() {
        JsThreedModel jsModelFromJsInPage = getJsModelFromJsInPage();

         if (jsModelFromJsInPage == null) {
            throw new IllegalStateException("getJsModelFromJsInPage() returned null. See html comments for more details.");
        } else{
            Console.log("Pulled jsModelFromJsInPage from jsp page");
        }

        ThreedModel threedModel = createModelFromJs(jsModelFromJsInPage);
        threedModel.setRepoBaseUrl(getRepoBaseUrlFromPage());
        return threedModel;
    }

    public static Path getRepoBaseUrlFromPage() {
        String jsRepoBaseUrl = getJsRepoBaseUrl();

        if (jsRepoBaseUrl == null) {
            throw new IllegalStateException("getJsRepoBaseUrl() returned null. See html comments for more details.");
        } else{
            Console.log("Pulled repoBaseDir[" + jsRepoBaseUrl + "] from jsp page");
        }

        return new Path(jsRepoBaseUrl);
    }

    public static ThreedModel createModelFromJsonText(String jsonText) {
        JsThreedModel jsThreedModel = getJsModelFromJsonText(jsonText);
        return createModelFromJs(jsThreedModel);
    }

    private static native String getJsRepoBaseUrl() /*-{
        return $wnd.repoBaseUrl;
    }-*/;

    private static native JsThreedModel getJsModelFromJsInPage() /*-{
        return $wnd.threedModel;
    }-*/;

    private static native JsThreedModel getJsModelFromJsonText(String jsonText) /*-{
        eval("var xTmp = " + jsonText);
        return xTmp;
    }-*/;


    private static ThreedModel createModelFromJs(JsThreedModel jsThreedModel) {
        JsonUnmarshallerFm jsonFeatureModelBuilder = new JsonUnmarshallerFm();

        JsFeatureModel jsFeatureModel = jsThreedModel.getJsFeatureModel();
        FeatureModel featureModel = jsonFeatureModelBuilder.createFeatureModelFromJson(jsFeatureModel);

        SeriesInfo seriesInfo = SeriesInfoBuilder.createSeriesInfo(featureModel.getSeriesKey());
        JsonUnmarshallerIm imJsonParser = new JsonUnmarshallerIm(featureModel, seriesInfo);

        JsImageModel jsImageModel = jsThreedModel.getJsImageModel();

        ImSeries imSeries = imJsonParser.parseSeries(jsImageModel);


        return new ThreedModel(featureModel, imSeries);
    }


}