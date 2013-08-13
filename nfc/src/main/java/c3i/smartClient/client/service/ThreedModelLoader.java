package c3i.smartClient.client.service;

import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.util.shared.futures.AsyncFunction;
import c3i.util.shared.futures.Completer;
import c3i.util.shared.futures.Loader;
import smartsoft.util.gwt.client.rpc.FailureCallback;
import smartsoft.util.gwt.client.rpc.Req;
import smartsoft.util.gwt.client.rpc.SuccessCallback;

public class ThreedModelLoader extends Loader<SeriesId, ThreedModel> {

    private final ThreedModelClient client;
    private final SeriesId seriesId;


    public ThreedModelLoader(final ThreedModelClient threedModelClient, final SeriesId seriesId) {
        super(seriesId, new ThreedModelLoaderFunction(threedModelClient, seriesId));


        this.client = threedModelClient;
        this.seriesId = seriesId;

    }


    public SeriesKey getSeriesKey() {
        return seriesId.getSeriesKey();
    }

    public SeriesId getSeriesId() {
        return seriesId;
    }

    public static class ThreedModelLoaderFunction implements AsyncFunction<SeriesId, ThreedModel> {

        private final ThreedModelClient threedModelClient;
        private final SeriesId seriesId;

        public ThreedModelLoaderFunction(ThreedModelClient threedModelClient, SeriesId seriesId) {
            this.threedModelClient = threedModelClient;
            this.seriesId = seriesId;
        }

        @Override
        public void start(SeriesId arg, final Completer<ThreedModel> completer) throws Exception {

            threedModelClient.log("Loading ThreedModel [" + seriesId.getSeriesKey().getShortName() + "]...");
            Req<ThreedModel> r1 = threedModelClient.fetchThreedModel(seriesId);

            r1.onSuccess = new SuccessCallback<ThreedModel>() {
                @Override
                public void call(Req<ThreedModel> request) {
                    threedModelClient.log("\t Loading ThreedModel [" + seriesId.getSeriesKey().getShortName() + "] complete!");
                    completer.setResult(request.result);
                }
            };

            r1.onFailure = new FailureCallback() {
                @Override
                public void call(Req request) {
                    completer.setException(request.exception);
                }
            };
        }
    }
}
