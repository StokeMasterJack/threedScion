package com.tms.threed.threedAdmin.main.client.services;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tms.threed.threedAdmin.main.client.UiContext;
import com.tms.threed.threedAdmin.main.shared.ThreedAdminServiceAsync;
import com.tms.threed.threedFramework.jpgGen.shared.ExecutorStatus;
import com.tms.threed.threedFramework.jpgGen.shared.JobId;
import com.tms.threed.threedFramework.jpgGen.shared.Stats;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.repo.shared.RootTreeId;
import com.tms.threed.threedFramework.threedModel.shared.SeriesId;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;
import com.tms.threed.threedFramework.util.gwtUtil.client.Console;
import com.tms.threed.threedFramework.util.lang.shared.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * These are the ThreedAdminService remote calls that use RequestBuilder + JSON (i.e. not gwt-rpc)
 */
public class JpgGenServiceAsync {

    private final UiContext ctx;

    public final ThreedAdminServiceAsync threedAdminServiceAsync;

    public JpgGenServiceAsync(UiContext ctx, ThreedAdminServiceAsync threedAdminServiceAsync) {
        this.ctx = ctx;
        this.threedAdminServiceAsync = threedAdminServiceAsync;
    }

    public static String getBaseUrl() {
        Path hostPageBaseURL = new Path(GWT.getHostPageBaseURL());
        Path url = hostPageBaseURL.append("threedAdminService.json");
        return url.toString();
    }

    public static JpgGenServiceAsync createJpgGenService(UiContext ctx, ThreedAdminServiceAsync threedAdminServiceAsync) {
        return new JpgGenServiceAsync(ctx, threedAdminServiceAsync);
    }



    public static class ServiceRequest {

        final String baseUrl;
        String command;
        Map<String, String> params = new HashMap<String, String>();

        public ServiceRequest() {
            baseUrl = ServiceRequest.getBaseUrl();
        }

        public static String getBaseUrl() {
            return JpgGenServiceAsync.getBaseUrl();
        }

        public void put(String name, String value) {
            params.put(name, value);
        }

        public void put(SeriesKey seriesKey) {
            put("seriesName", seriesKey.getName());
            put("seriesYear", seriesKey.getYear() + "");
        }

        public void put(SeriesId seriesId) {
            put(seriesId.getSeriesKey());
            put(RootTreeId.NAME, seriesId.getRootTreeId().getName());
        }

        public void put(JpgWidth jpgWidth) {
            put("jpgWidth", jpgWidth.stringValue());
        }

        public String buildFullUrl() {
            String ur = baseUrl + "?command=" + command;

            for (String name : params.keySet()) {
                String value = params.get(name);
                ur += "&" + name + "=" + value;
            }

            return ur;
        }
    }

    public void removeTerminal(final RemoveJobCallBack callBack) {
        ServiceRequest r = new ServiceRequest();
        r.command = "removeTerminal";

        String url = r.buildFullUrl();

        final RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                callBack.onSuccess();
            }

            @Override
            public void onError(Request request, Throwable e) {
                Window.alert(e.toString());
                e.printStackTrace();
            }
        });

        try {
            requestBuilder.send();
        } catch (RequestException e) {
            Window.alert(e.toString());
            e.printStackTrace();
        }
    }

    public void removeJob(String jobId, final RemoveJobCallBack callBack) {
        ServiceRequest r = new ServiceRequest();
        r.command = "removeJob";
        r.put("jobId", jobId);

        String url = r.buildFullUrl();

        final RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                callBack.onSuccess();
            }

            @Override
            public void onError(Request request, Throwable e) {
                Window.alert(e.toString());
                e.printStackTrace();
            }
        });

        try {
            requestBuilder.send();
        } catch (RequestException e) {
            Window.alert(e.toString());
            e.printStackTrace();
        }
    }

    public void cancelJob(String jobId, final CancelJobCallBack callBack) {

        ServiceRequest r = new ServiceRequest();
        r.command = "cancelJob";
        r.put("jobId", jobId);

        String url = r.buildFullUrl();

        final RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                callBack.onSuccess();
            }

            @Override
            public void onError(Request request, Throwable e) {
                Window.alert(e.toString());
                e.printStackTrace();
            }
        });

        try {
            requestBuilder.send();
        } catch (RequestException e) {
            Window.alert(e.toString());
            e.printStackTrace();
        }


    }

    public void fetchJpgQueueDetails(String jobId, final FetchQueueDetailsCallback callback) {

        ServiceRequest r = new ServiceRequest();
        r.command = "jpgQueueDetails";
        r.put("jobId", jobId);

        String url = r.buildFullUrl();

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {

                if (response.getStatusCode() != 200) {
                    callback.onError(response.getText());
                    return;
                }

                JSONArray jsonArray = JSONParser.parseStrict(response.getText()).isArray();

                if (jsonArray.size() == 0) {
                    callback.badJobId();
                    return;
                }


                List<ExecutorStatus> a = new ArrayList<ExecutorStatus>();


                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsObject = jsonArray.get(i).isObject();
                    ExecutorStatus d = new ExecutorStatus(
                            jsObject.get("name").isString().stringValue(),
                            jsObject.get("shutdown").isBoolean().booleanValue(),
                            jsObject.get("terminated").isBoolean().booleanValue(),
                            (int) jsObject.get("activeTaskCount").isNumber().doubleValue(),
                            (int) jsObject.get("taskCount").isNumber().doubleValue(),
                            (int) jsObject.get("completedTaskCount").isNumber().doubleValue()
                    );


                    a.add(d);
                }


                callback.onSuccess(a);
            }

            @Override
            public void onError(Request request, Throwable e) {
                Window.alert(e.toString());
                e.printStackTrace();
            }
        });

        try {
            requestBuilder.send();
        } catch (RequestException e) {
            Window.alert(e.toString());
            e.printStackTrace();
        }


    }

    public void fetchJpgQueueStatus(final FetchJpgStatusCallback callback) {

        ServiceRequest r = new ServiceRequest();
        r.command = "jpgQueueStatus";

        String url = r.buildFullUrl();

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                int statusCode = response.getStatusCode();
                if (statusCode != 200) {
                    String msg = "Bad Response: " + response.getText();
                    Console.log(msg);
                    callback.onError(statusCode, response.getStatusText(), response.getText());
                } else {
                    JSONArray jsArray = JSONParser.parseStrict(response.getText()).isArray();
                    callback.onSuccess(jsArray);
                }
            }

            @Override
            public void onError(Request request, Throwable e) {
                Window.alert(e.toString());
                e.printStackTrace();
            }
        });

        try {
            requestBuilder.send();
        } catch (RequestException e) {
            Window.alert(e.toString());
            e.printStackTrace();
        }


    }

    public void startJpgJob(final SeriesId seriesId, final JpgWidth jpgWidth) {

        ServiceRequest r = new ServiceRequest();
        r.command = "startJpgJob";
        r.put(seriesId);
        r.put(jpgWidth);

        String url = r.buildFullUrl();

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                JSONValue jsonValue = JSONParser.parseStrict(response.getText());
                if (jsonValue.isObject() != null && jsonValue.isObject().get("message").isString() != null) {
                    ctx.showMessage("Jpg job started");
                }
            }

            @Override
            public void onError(Request request, Throwable e) {
                ctx.showMessage("Problem starting JPG job: " + e.toString() + ". See server log for more info.");
                e.printStackTrace();
            }
        });

        try {
            requestBuilder.send();
        } catch (RequestException e) {
            Window.alert(e.toString());
            e.printStackTrace();
        }

    }

    public void getJpgGenFinalStats(JobId jobId, AsyncCallback<Stats> async) {
        threedAdminServiceAsync.getJpgGenFinalStats(jobId, async);
    }


    public static interface CancelJobCallBack {

        void onSuccess();
    }

    public static interface FetchJpgStatusCallback {
        void onSuccess(JSONArray htmlSnippet);

        void onError(int statusCode, String statusText, String responseText);
    }

    public static interface FetchQueueDetailsCallback {
        void onSuccess(List<ExecutorStatus> queueDetails);

        void onError(String text);

        void badJobId();
    }

    public static interface RemoveJobCallBack {

        void onSuccess();
    }
}
