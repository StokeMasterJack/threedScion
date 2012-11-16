package c3i.core.threedModel.client;

import java.util.logging.Level;import java.util.logging.Logger;
import smartsoft.util.shared.Path;
import c3i.core.featureModel.client.JsFeatureModel;
import c3i.core.featureModel.client.JsonUnmarshallerFm;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.imageModel.client.JsImageModel;
import c3i.core.imageModel.client.JsonUnmarshallerIm;
import c3i.core.imageModel.shared.ImSeries;
import c3i.core.threedModel.shared.SeriesInfo;
import c3i.core.threedModel.shared.SeriesInfoBuilder;
import c3i.core.threedModel.shared.SubSeries;
import c3i.core.threedModel.shared.ThreedModel;

public class JsonUnmarshallerTm {

    public static ThreedModel createModelFromJsInPage() {
        JsThreedModel jsModelFromJsInPage = getJsModelFromJsInPage();

        if (jsModelFromJsInPage == null) {
            throw new IllegalStateException("getJsModelFromJsInPage() returned null. See html comments for more details.");
        } else {
            log.log(Level.INFO, "Pulled jsModelFromJsInPage from jsp page");
        }

        ThreedModel threedModel = createModelFromJs(jsModelFromJsInPage);

        SubSeries subSeries = getSubSeriesFromPage();
        threedModel.setSubSeries(subSeries);
        return threedModel;
    }

    private static SubSeries getSubSeriesFromPage() {
        JsSubSeries jsSubSeries = getJsSubSeries();
        if (jsSubSeries == null) {
            return null;
        } else {
            return new SubSeries(jsSubSeries.getLabel(), jsSubSeries.getYear());
        }
    }

    public static Path getRepoBaseUrlFromPage() {
        String jsRepoBaseUrl = getJsRepoBaseUrl();

        if (jsRepoBaseUrl == null) {
            throw new IllegalStateException("getJsRepoBaseUrl() returned null. See html comments for more details.");
        } else {
            log.log(Level.INFO, "Pulled repoBaseDir[" + jsRepoBaseUrl + "] from jsp page");
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
        if ($wnd.subSeries) {
            return $wnd.subSeries;
        } else {
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


    public static ThreedModel createModelFromJs(JsThreedModel jsThreedModel) {
        JsonUnmarshallerFm jsonFeatureModelBuilder = new JsonUnmarshallerFm();

        JsFeatureModel jsFeatureModel = jsThreedModel.getJsFeatureModel();
        FeatureModel featureModel = jsonFeatureModelBuilder.createFeatureModelFromJson(jsFeatureModel);

//        SeriesInfo seriesInfo = SeriesInfoBuilder.createSeriesInfo(featureModel.getSeriesKey());
        JsonUnmarshallerIm imJsonParser = new JsonUnmarshallerIm(featureModel);

        JsImageModel jsImageModel = jsThreedModel.getJsImageModel();

        ImSeries imSeries = imJsonParser.parseSeries(jsImageModel);



        return new ThreedModel(featureModel, imSeries);
    }

    private static Logger log = Logger.getLogger(JsonUnmarshallerTm.class.getName());

}