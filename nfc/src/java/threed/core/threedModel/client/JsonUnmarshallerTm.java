package threed.core.threedModel.client;

import threed.core.featureModel.client.JsFeatureModel;
import threed.core.featureModel.client.JsonUnmarshallerFm;
import threed.core.featureModel.shared.FeatureModel;
import threed.core.imageModel.client.JsImageModel;
import threed.core.imageModel.client.JsonUnmarshallerIm;
import threed.core.imageModel.shared.ImSeries;
import threed.core.threedModel.shared.SeriesInfo;
import threed.core.threedModel.shared.*;
import threed.core.threedModel.shared.SeriesInfoBuilder;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.lang.shared.Path;

public class JsonUnmarshallerTm {

    public static ThreedModel createModelFromJsInPage() {
        JsThreedModel jsModelFromJsInPage = getJsModelFromJsInPage();

        if (jsModelFromJsInPage == null) {
            throw new IllegalStateException("getJsModelFromJsInPage() returned null. See html comments for more details.");
        } else {
            Console.log("Pulled jsModelFromJsInPage from jsp page");
        }

        ThreedModel threedModel = createModelFromJs(jsModelFromJsInPage);
        threedModel.setRepoBaseUrl(getRepoBaseUrlFromPage());


        SubSeries subSeries = getSubSeriesFromPage();
        threedModel.setSubSeries(subSeries);
        return threedModel;
    }

    private static SubSeries getSubSeriesFromPage() {
        JsSubSeries jsSubSeries = getJsSubSeries();
        if(jsSubSeries==null){
            return null;
        }
        else{
            return new SubSeries(jsSubSeries.getLabel(),jsSubSeries.getYear());
        }
    }

    public static Path getRepoBaseUrlFromPage() {
        String jsRepoBaseUrl = getJsRepoBaseUrl();

        if (jsRepoBaseUrl == null) {
            throw new IllegalStateException("getJsRepoBaseUrl() returned null. See html comments for more details.");
        } else {
            Console.log("Pulled repoBaseDir[" + jsRepoBaseUrl + "] from jsp page");
        }

        return new Path(jsRepoBaseUrl);
    }

    public static ThreedModel createModelFromJsonText(String jsonText) {
        JsThreedModel jsThreedModel = getJsModelFromJsonText(jsonText);

        ThreedModel threedModel = createModelFromJs(jsThreedModel);
        SubSeries subSeries = getSubSeriesFromPage();
        threedModel.setSubSeries(subSeries);

        return threedModel;
    }

    private static native String getJsRepoBaseUrl() /*-{
        return $wnd.repoBaseUrl;
    }-*/;

    private static native JsSubSeries getJsSubSeries() /*-{
        if($wnd.subSeries){
            return $wnd.subSeries;
        }else{
            return null;
        }
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