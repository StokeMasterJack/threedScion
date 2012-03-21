package com.tms.threed.threedCore.threedModel.shared;

import com.tms.threed.threedCore.threedModel.client.ThreedModelClient;
import smartsoft.util.gwt.client.rpc2.Future;
import smartsoft.util.gwt.client.rpc2.SuccessCb;

public class VtcMapLoader extends Future<VtcMap> {

    public VtcMapLoader(final ThreedModelClient client) {
        final Future<VtcMap> futureInternal = client.getVtcMap();

        futureInternal.success(new SuccessCb() {
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
