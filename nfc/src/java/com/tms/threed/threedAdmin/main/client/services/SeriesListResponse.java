package com.tms.threed.threedAdmin.main.client.services;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import java.util.ArrayList;
import java.util.List;

public class SeriesListResponse {

    private final JSONArray jsSeriesList;

    public SeriesListResponse(String jsonText) {
        this.jsSeriesList = JSONParser.parseStrict(jsonText).isArray();


    }

    public List<String> getSeriesNames() {
        List<String> a = new ArrayList<String>();
        for (int i = 0; i < jsSeriesList.size(); i++) {
            JSONValue value = jsSeriesList.get(i);
            JSONObject jsSeries = value.isObject();
            String seriesName = jsSeries.get("name").isString().stringValue();


            a.add(seriesName);
        }
        return a;
    }

    public List<Integer> getYearsForSeries(String seriesName) {
        List<Integer> a = new ArrayList<Integer>();
        for (int i = 0; i < jsSeriesList.size(); i++) {
            JSONValue value = jsSeriesList.get(i);
            JSONObject jsSeries = value.isObject();
            String sn = jsSeries.get("name").isString().stringValue();
            if (sn.equalsIgnoreCase(seriesName)) {
                JSONArray jsYears = jsSeries.get("years").isArray();
                for (int j = 0; j < jsYears.size(); j++) {
                    a.add((int) jsYears.get(j).isNumber().doubleValue());
                }
            }

        }
        return a;
    }


}
