package com.tms.threed.threedCore.threedModel.client;

import com.tms.threed.threedCore.threedModel.shared.RootTreeId;
import com.tms.threed.threedCore.threedModel.shared.SeriesId;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import com.tms.threed.threedCore.threedModel.shared.ThreedModel;
import smartsoft.util.gwt.client.rpc.FailureCallback;
import smartsoft.util.gwt.client.rpc.Req;
import smartsoft.util.gwt.client.rpc.SuccessCallback;
import smartsoft.util.gwt.client.rpc2.Future;

import java.util.ArrayList;

public class ThreedModelLoader {

    private final ThreedModelClient client;

    private final ArrayList<Future<ThreedModel>> futures = new ArrayList<Future<ThreedModel>>();

    private final SeriesKey seriesKey;
    private RootTreeId rootTreeId;
    private ThreedModel threedModel;
    private Throwable exception;
    private boolean sendOnSetRootTreeId;
    private boolean sent;

    public ThreedModelLoader(ThreedModelClient client, SeriesKey seriesKey) {
        this(client, seriesKey, null);
    }

    public ThreedModelLoader(ThreedModelClient client, SeriesKey seriesKey, RootTreeId rootTreeId) {
        this.client = client;
        this.seriesKey = seriesKey;
        this.rootTreeId = rootTreeId;
    }

    public void setRootTreeId(RootTreeId rootTreeId) {
        this.rootTreeId = rootTreeId;
        if (sendOnSetRootTreeId) {
            send();
            sendOnSetRootTreeId = false;
        }
    }

    private void send() {
        assert seriesKey != null;
        assert rootTreeId != null;

        SeriesId seriesId = new SeriesId(seriesKey, rootTreeId);

        Req<ThreedModel> r1 = client.fetchThreedModel(seriesId);

        r1.onSuccess = new SuccessCallback<ThreedModel>() {
            @Override
            public void call(Req<ThreedModel> request) {
                onSuccessInternal(request.result);
            }
        };

        r1.onFailure = new FailureCallback() {
            @Override
            public void call(Req request) {
                onFailureInternal(request.exception);
            }
        };

        this.sent = true;
    }

    private void onSuccessInternal(ThreedModel result) {
        threedModel = result;
        while (futures.size() > 0) {
            Future<ThreedModel> f = futures.remove(0);
            f.setResult(result);
        }
    }

    private void onFailureInternal(Throwable e) {
        exception = e;
        while (futures.size() > 0) {
            Future<ThreedModel> f = futures.remove(0);
            f.setException(e);
        }
    }

    public SeriesId getSeriesId() {
        return new SeriesId(seriesKey, rootTreeId);
    }

    public ThreedModel getThreeModel() {
        return threedModel;
    }


    public boolean isComplete() {
        return (threedModel != null && exception == null) || (threedModel == null && exception != null);
    }

    public Future<ThreedModel> ensureLoaded() {

        final Future<ThreedModel> f = new Future<ThreedModel>();

        if (isComplete()) {
            if (isSuccess()) {
                f.setResult(threedModel);
            } else if (isFailure()) {
                f.setException(exception);
            } else {
                throw new IllegalStateException();
            }
        } else {
            futures.add(f);
            if (!sent && rootTreeId != null) {
                send();
            } else {
                sendOnSetRootTreeId = true;
            }
        }
        return f;
    }

    private boolean isSuccess() {
        return threedModel != null && exception == null;
    }

    private boolean isFailure() {
        return threedModel == null && exception != null;
    }


}
