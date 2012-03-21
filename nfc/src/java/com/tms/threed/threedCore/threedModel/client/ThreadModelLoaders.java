package com.tms.threed.threedCore.threedModel.client;

import com.tms.threed.threedCore.threedModel.shared.*;
import smartsoft.util.gwt.client.rpc2.Future;
import smartsoft.util.gwt.client.rpc2.SuccessCb;

import java.util.HashMap;
import java.util.Map;

public class ThreadModelLoaders {

    private final ThreedModelClient client;
    private final VtcMapLoader vtcMapLoader;
    private final HashMap<SeriesKey, ThreedModelLoader> threedModelLoaders;

    public ThreadModelLoaders(ThreedModelClient client) {
        this.client = client;
        vtcMapLoader = new VtcMapLoader(client);
        threedModelLoaders = new HashMap<SeriesKey, ThreedModelLoader>();

        vtcMapLoader.success(new SuccessCb() {
            @Override
            public void call() {
                setVtcMap(vtcMapLoader.result);
            }
        });

    }

    public void setVtcMap(VtcMap vtcMap) {
        Map<SeriesKey, RootTreeId> m = vtcMap.toMap();
        for (Map.Entry<SeriesKey, RootTreeId> entry : m.entrySet()) {
            SeriesKey seriesKey = entry.getKey();
            RootTreeId rootTreeId = entry.getValue();
            setRootTreeId(seriesKey, rootTreeId);
        }
    }

    private ThreedModelLoader getLoader(SeriesKey seriesKey) {
        ThreedModelLoader loader = threedModelLoaders.get(seriesKey);
        if (loader == null) {
            loader = new ThreedModelLoader(client, seriesKey);
            threedModelLoaders.put(seriesKey, loader);
        }
        return loader;
    }

    public Future<ThreedModel> ensureLoaded(SeriesKey seriesKey) {
        ThreedModelLoader loader = getLoader(seriesKey);
        return loader.ensureLoaded();
    }

    private void setRootTreeId(SeriesKey seriesKey, RootTreeId rootTreeId) {
        ThreedModelLoader loader = getLoader(seriesKey);
        loader.setRootTreeId(rootTreeId);
    }
}
