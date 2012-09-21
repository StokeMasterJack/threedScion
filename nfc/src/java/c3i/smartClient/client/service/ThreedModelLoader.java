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


    public ThreedModelLoader(final ThreedModelClient client, final SeriesId seriesId) {
        super(seriesId, new AsyncFunction<SeriesId, ThreedModel>() {
            @Override
            public void start(SeriesId input, final Completer<ThreedModel> completer) throws Exception {
                Req<ThreedModel> r1 = client.fetchThreedModel(seriesId);

                r1.onSuccess = new SuccessCallback<ThreedModel>() {
                    @Override
                    public void call(Req<ThreedModel> request) {
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
        });


        this.client = client;
        this.seriesId = seriesId;

    }


    public SeriesKey getSeriesKey() {
        return seriesId.getSeriesKey();
    }

    public SeriesId getSeriesId() {
        return seriesId;
    }

}
