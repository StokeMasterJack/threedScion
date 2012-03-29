package smartClient.client;

import com.google.common.base.Preconditions;
import com.tms.threed.threedCore.threedModel.client.ThreedModelClient;
import com.tms.threed.threedCore.threedModel.shared.SeriesId;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import com.tms.threed.threedCore.threedModel.shared.ThreedModel;
import smartsoft.util.gwt.client.rpc.FailureCallback;
import smartsoft.util.gwt.client.rpc.Req;
import smartsoft.util.gwt.client.rpc.SuccessCallback;

import javax.annotation.Nonnull;

public class SeriesLoader extends Loader<ThreedModel> {

    private final ThreedModelClient client;

    private final SeriesId seriesId;

    public SeriesLoader(SeriesId seriesId) {
        super("SeriesLoader");
        Preconditions.checkNotNull(seriesId);
        this.client = ThreedModelClient.create();
        this.seriesId = seriesId;
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

    @Override
    public SeriesFuture createFuture(String name) {
        return new SeriesFuture();
    }

    @Nonnull
    @Override
    public SeriesFuture ensureLoaded() {
        return (SeriesFuture) super.ensureLoaded();
    }

}
