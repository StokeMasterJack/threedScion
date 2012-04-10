package threed.smartClient.client.api;

import com.google.common.base.Preconditions;
import threed.core.threedModel.shared.SeriesId;
import threed.core.threedModel.shared.SeriesKey;
import threed.core.threedModel.shared.ThreedModel;
import threed.smartClient.client.util.futures.Loader;
import smartsoft.util.gwt.client.rpc.FailureCallback;
import smartsoft.util.gwt.client.rpc.Req;
import smartsoft.util.gwt.client.rpc.SuccessCallback;
import smartsoft.util.gwt.client.rpc.UiLog;
import smartsoft.util.lang.shared.Path;

public class SeriesLoader extends Loader<ThreedModel> {

    private final ThreedModelClient client;

    private final SeriesId seriesId;

    public SeriesLoader(SeriesId seriesId, Path repoBaseUrl) {
        super("SeriesLoader");
        Preconditions.checkNotNull(seriesId);
        this.seriesId = seriesId;
        this.client = new ThreedModelClient(UiLog.DEFAULT, repoBaseUrl);
        send();
    }

    private void send() {

        assert seriesId != null;

        Req<ThreedModel> r1 = client.fetchThreedModel(seriesId);

        r1.onSuccess = new SuccessCallback<ThreedModel>() {
            @Override
            public void call(Req<ThreedModel> request) {
                setResult(request.result);
            }
        };

        r1.onFailure = new FailureCallback() {
            @Override
            public void call(Req request) {
                setException(request.exception);
            }
        };

    }

    public SeriesKey getSeriesKey() {
        return seriesId.getSeriesKey();
    }

    public SeriesId getSeriesId() {
        return seriesId;
    }

}
