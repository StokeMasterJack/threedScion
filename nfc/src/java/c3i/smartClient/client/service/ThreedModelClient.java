package c3i.smartClient.client.service;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.threedModel.client.JsonUnmarshallerTm;
import c3i.core.threedModel.shared.Brand;
import c3i.core.threedModel.shared.RootTreeId;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.util.shared.futures.AsyncFunction;
import c3i.util.shared.futures.Completer;
import c3i.util.shared.futures.CompleterImpl;
import c3i.util.shared.futures.Future;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.gwt.client.rpc.Req;
import smartsoft.util.gwt.client.rpc.RequestContext;
import smartsoft.util.lang.shared.Path;

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
public class ThreedModelClient {

    private static final Path DEFAULT_REPO_BASE_URL = new Path("/configurator-content");

    private final String urlTemplate = "${repoBase.url}/${brandName}/${seriesName}/${seriesYear}/3d/models/${rootTreeId}.json";

    private final JsonUnmarshallerTm jsonThreedModelBuilder;
    private final Path repoBaseUrl;
    private final RequestContext requestContext;

    public ThreedModelClient(Path repoBaseUrl) {
        if (repoBaseUrl == null) {
            throw new IllegalArgumentException("repoBaseUrl must be non-null");
        }

        this.jsonThreedModelBuilder = new JsonUnmarshallerTm();
        this.repoBaseUrl = repoBaseUrl;
        requestContext = new RequestContext();
    }

    public ThreedModelClient() {
        this.jsonThreedModelBuilder = new JsonUnmarshallerTm();
        requestContext = new RequestContext();
        repoBaseUrl = null;
    }

    private <T> Req<T> newRequest(String opName) {
        return requestContext.newRequest(opName);
    }

    public ThreedModel parseJsonThreedModel(String threedModelJsonText) {
        if (repoBaseUrl == null) {
            throw new IllegalArgumentException("repoBaseUrl must be non-null before calling parseJsonThreedModel(..)");
        }
        ThreedModel threedModel = jsonThreedModelBuilder.createModelFromJsonText(threedModelJsonText);
        return threedModel;
    }

    public Path getThreedModelUrl(final SeriesId seriesId) {
        return getThreedModelUrl(seriesId.getSeriesKey(), seriesId.getRootTreeId());
    }

    public Path getVtcMapUrl(BrandKey brandKey) {
        if (repoBaseUrl == null) {
            throw new IllegalStateException("repoBaseUrl must be non-null before calling getThreedModelUrl(..)");
        }
        return new Path("vtcMap.txt").prepend(brandKey.getKey()).prepend(repoBaseUrl);
    }

    public Path getVtcUrl(SeriesKey seriesKey) {
        if (repoBaseUrl == null) {
            throw new IllegalStateException("repoBaseUrl must be non-null before calling getThreedModelUrl(..)");
        }

        return repoBaseUrl
                .append(seriesKey.getBrandKey().getKey())
                .append(seriesKey.getName())
                .append(seriesKey.getYear())
                .append("vtc.txt");

    }

    public Path getThreedModelUrl(final SeriesKey seriesKey, RootTreeId rootTreeId) {
        if (repoBaseUrl == null) {
            throw new IllegalStateException("repoBaseUrl must be non-null before calling getThreedModelUrl(..)");
        }
        String url = urlTemplate.replace("${brandName}", seriesKey.getBrandKey().getKey());
        url = url.replace("${seriesName}", seriesKey.getName());
        url = url.replace("${seriesYear}", seriesKey.getYear() + "");
        url = url.replace("${rootTreeId}", rootTreeId.getName());
        url = url.replace("${repoBase.url}", repoBaseUrl.toString());

        return new Path(url);
    }

    public Future<Brand> getBrandInit(BrandKey brandKey) throws Exception {
        final Completer<Brand> f = new CompleterImpl<Brand>();
        brandLoaderFunction.start(brandKey, f);
        return f.getFuture();
    }

    public Future<String> getVtc(SeriesKey seriesKey) throws Exception {
        final Completer<String> f = new CompleterImpl<String>();
        vtcLoaderFunction.start(seriesKey, f);
        return f.getFuture();
    }


    public Req<ThreedModel> fetchThreedModel(final SeriesId seriesId) {
        final Req<ThreedModel> r = newRequest("fetchThreedModel");

        Path url = getThreedModelUrl(seriesId);
        Console.log("Requesting threedModel: " + url);

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url.toString());

        requestBuilder.setCallback(new RequestCallback() {

            @Override
            public void onResponseReceived(Request request, Response response) {
                String jsonResponseText = response.getText();
                assert jsonResponseText != null;
                Console.log("\tParsing ThreedModel[" + seriesId.getSeriesKey() + "] JSON...");
                ThreedModel threedModel = parseJsonThreedModel(jsonResponseText);
                assert threedModel != null;
                SeriesKey returnedSeriesKey = threedModel.getSeriesKey();
                assert returnedSeriesKey.equals(seriesId.getSeriesKey()) : "Returned seriesKey [" + returnedSeriesKey + "] does not match request seriesKey[" + seriesId.getSeriesKey() + "]";
                Console.log("\tRefreshAfterThreedModelChange[" + seriesId.getSeriesKey() + "] ...");
                r.onSuccess(threedModel);
            }

            @Override
            public void onError(Request request, Throwable e) {
                e.printStackTrace();
                r.onFailure(e);
            }

        });

        try {
            requestBuilder.send();

        } catch (RequestException e) {
            r.onFailure(e);
        }

        return r;
    }


    public Path fetchThreedModel2(final BrandKey brandKey, final SeriesId seriesId, final Callback callback) {
        return fetchThreedModel2(brandKey, seriesId.getSeriesKey(), seriesId.getRootTreeId(), callback);
    }

    public Path fetchThreedModel2(final BrandKey brandKey, final SeriesKey seriesKey, RootTreeId rootTreeId, final Callback callback) {
        assert callback != null;

        Path url = getThreedModelUrl(seriesKey, rootTreeId);
        Console.log("Requesting threedModel: " + url);

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url.toString());

        requestBuilder.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
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

            @Override
            public void onError(Request request, Throwable e) {
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

    public AsyncFunction<BrandKey, Brand> brandLoaderFunction = new AsyncFunction<BrandKey, Brand>() {

        @Override
        public void start(BrandKey brandKey, final Completer<Brand> f) throws RuntimeException {
            Path vtcMapUrl = getVtcMapUrl(brandKey);

            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, vtcMapUrl.toString());
            requestBuilder.setCallback(new RequestCallback() {

                @Override
                public void onResponseReceived(Request request, final Response response) {
                    if (response.getStatusCode() != 200) {
                        f.setException(new RuntimeException("getVtcMap return non-200 response[" + response.getStatusCode() + "]. Response text: " + response.getText()));
                    } else {
                        Brand brandInitData = BrandParser.parse(response.getText());
                        f.setResult(brandInitData);
                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    f.setException(exception);
                }

            });

            try {
                requestBuilder.send();
            } catch (RequestException e) {
                e.printStackTrace();
                f.setException(e);
            }

        }
    };

    public AsyncFunction<SeriesKey, String> vtcLoaderFunction = new AsyncFunction<SeriesKey, String>() {

        @Override
        public void start(SeriesKey seriesKey, final Completer<String> f) throws RuntimeException {
            Path vtcMapUrl = getVtcUrl(seriesKey);

            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, vtcMapUrl.toString());
            requestBuilder.setCallback(new RequestCallback() {

                @Override
                public void onResponseReceived(Request request, final Response response) {
                    if (response.getStatusCode() != 200) {
                        f.setException(new RuntimeException("getVtc return non-200 response[" + response.getStatusCode() + "]. Response text: " + response.getText()));
                    } else {
                        f.setResult(response.getText());
                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    f.setException(exception);
                }

            });

            try {
                requestBuilder.send();
            } catch (RequestException e) {
                e.printStackTrace();
                f.setException(e);
            }

        }
    };

}