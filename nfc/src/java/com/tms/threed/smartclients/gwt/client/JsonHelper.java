package com.tms.threed.smartClients.gwt.client;

import com.google.gwt.json.client.JSONObject;
import com.tms.threed.threedCore.threedModel.shared.RootTreeId;
import com.tms.threed.threedCore.threedModel.shared.SeriesId;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;

public class JsonHelper {

    public static SeriesKey getSeriesKey(JSONObject o) {
        String sn = o.get("seriesName").isString().stringValue();
        double d = o.get("seriesYear").isNumber().doubleValue();
        int sy = (int) d;

        return new SeriesKey(sy, sn);
    }

    public static SeriesId getSeriesId(JSONObject o) {
        SeriesKey seriesKey = getSeriesKey(o);
        String c = o.get(RootTreeId.NAME).isString().stringValue();
        return new SeriesId(seriesKey, new RootTreeId(c));
    }


}
