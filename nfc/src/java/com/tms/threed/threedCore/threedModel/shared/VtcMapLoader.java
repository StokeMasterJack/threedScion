package com.tms.threed.threedCore.threedModel.shared;

import com.tms.threed.threedCore.threedModel.client.ThreedModelClient;
import smartClient.client.util.futures.Future;
import smartClient.client.util.futures.OnSuccess;

public class VtcMapLoader extends Future<VtcMap> {

    public VtcMapLoader(final ThreedModelClient client) {
        final Future<VtcMap> futureInternal = client.getVtcMap();

        futureInternal.success(new OnSuccess() {
            @Override
            public void call() {
                setResult(futureInternal.result);
            }
        });
    }

    public VtcMap getVtcMap() {
        return result;
    }

}
