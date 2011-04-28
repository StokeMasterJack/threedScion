package com.tms.threed.threedFramework.previewPane.client.threedServiceClient;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.tms.threed.threedFramework.repo.shared.RootTreeId;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.client.JsonUnmarshallerTm;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import com.tms.threed.threedFramework.util.gwtUtil.client.Console;
import com.tms.threed.threedFramework.util.lang.shared.Path;

/**
 *
 * <repo-url-base>/<repo-name>/3d/models/<commit-sha>.json
 *
 * http://smartsoftdev.net/configurator-content/avalon/3d/models/d1ed8dcb174ee13018ff19ed0ced61f60666ae76.json
 * http://localhost:8080/configurator-content/avalon/3d/models/d1ed8dcb174ee13018ff19ed0ced61f60666ae76.json
 *
 *
 * http://127.0.0.1:8888/com.tms.threed.testHarness.TestHarness/repos/avalon/3d/models/d1ed8dcb174ee13018ff19ed0ced61f60666ae76.json
 *
 */
public class ThreedModelServiceJson {

    private final String urlTemplate = "${repoBase.url}/${seriesName}/${seriesYear}/3d/models/${rootTreeId}.json";

    private final JsonUnmarshallerTm jsonThreedModelBuilder;
    private final Path repoBaseUrl;

    /**
     * Pulls repoBaseUrl from jsp page
     */
    public ThreedModelServiceJson() {
        this.jsonThreedModelBuilder = new JsonUnmarshallerTm();
        Path repoBaseUrl = JsonUnmarshallerTm.getRepoBaseUrlFromPage();
        if (repoBaseUrl == null) {
            throw new IllegalArgumentException("repoBaseUrl must be non-null");
        }
        this.repoBaseUrl = repoBaseUrl;
    }

    public ThreedModelServiceJson(Path repoBaseUrl) {
        if (repoBaseUrl == null) {
            throw new IllegalArgumentException("repoBaseUrl must be non-null");
        }
        this.jsonThreedModelBuilder = new JsonUnmarshallerTm();
        this.repoBaseUrl = repoBaseUrl;
    }

    public ThreedModel parseJsonThreedModel(String threedModelJsonText) {
        if (repoBaseUrl == null) {
            throw new IllegalArgumentException("repoBaseUrl must be non-null before calling parseJsonThreedModel(..)");
        }
        ThreedModel threedModel = jsonThreedModelBuilder.createModelFromJsonText(threedModelJsonText);
        threedModel.setRepoBaseUrl(repoBaseUrl);
        return threedModel;
    }


    public String getThreedModelUrl(final SeriesKey seriesKey, RootTreeId rootTreeId) {
        if (repoBaseUrl == null) {
            throw new IllegalStateException("repoBaseUrl must be non-null before calling getThreedModelUrl(..)");
        }
        String url = urlTemplate.replace("${seriesName}", seriesKey.getName());
        url = url.replace("${seriesYear}", seriesKey.getYear() + "");
        url = url.replace("${rootTreeId}", rootTreeId.getName());
        url = url.replace("${repoBase.url}", repoBaseUrl.toString());

        return url;
    }

    public String fetchThreedModel2(final SeriesKey seriesKey, RootTreeId rootTreeId, final Callback callback) {
        assert callback != null;

        String url = getThreedModelUrl(seriesKey, rootTreeId);


        Console.log("Requesting threedModel: " + url);

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);

        requestBuilder.setCallback(new RequestCallback() {
            @Override public void onResponseReceived(Request request, Response response) {
                String jsonResponseText = response.getText();

                assert jsonResponseText != null;
                Console.log("\tParsing ThreedModel[" + seriesKey + "] JSON...");
                ThreedModel threedModel = parseJsonThreedModel(jsonResponseText);
                assert threedModel != null;
                SeriesKey returnedSeriesKey = threedModel.getSeriesKey();
                assert returnedSeriesKey.equals(seriesKey) : "Returned seriesKey [" + returnedSeriesKey + "] does not match request seriesKey[" + seriesKey + "]";
                Console.log("\tRefreshAfterThreedModelChange[" + seriesKey + "] ...");

                callback.onThreeModelReceived(threedModel);
            }

            @Override public void onError(Request request, Throwable e) {
                e.printStackTrace();
            }
        });

        try {
            requestBuilder.send();
        } catch (RequestException e) {
            e.printStackTrace();
        }

        return url;

    }

    public ThreedModel fetchThreedModelFromPage() {
        return jsonThreedModelBuilder.createModelFromJsInPage();
    }

    public static interface Callback {
        void onThreeModelReceived(ThreedModel threedModel);
    }

}