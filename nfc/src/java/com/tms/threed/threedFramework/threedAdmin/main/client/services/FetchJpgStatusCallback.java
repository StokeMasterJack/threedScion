package com.tms.threed.threedFramework.threedAdmin.main.client.services;

import com.google.gwt.json.client.JSONArray;

public interface FetchJpgStatusCallback {
    void onSuccess(JSONArray htmlSnippet);

    void onError(int statusCode, String statusText, String responseText);
}
