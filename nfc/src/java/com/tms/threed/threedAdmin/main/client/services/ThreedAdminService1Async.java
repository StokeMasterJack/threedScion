package com.tms.threed.threedAdmin.main.client.services;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.tms.threed.threedAdmin.main.client.UiContext;
import com.tms.threed.threedAdmin.main.shared.ThreedAdminService2;
import com.tms.threed.threedAdmin.main.shared.ThreedAdminService2Async;
import com.tms.threed.threedFramework.jpgGen.shared.ExecutorStatus;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.repo.shared.RootTreeId;
import com.tms.threed.threedFramework.threedCore.shared.SeriesId;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;
import com.tms.threed.threedFramework.util.gwtUtil.client.Console;
import com.tms.threed.threedFramework.util.lang.shared.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.notEmpty;

public class ThreedAdminService1Async {

    private final UiContext ctx;

    public final ThreedAdminService2Async service2;

    public ThreedAdminService1Async(UiContext ctx) {
        this.ctx = ctx;

        String baseUrl = ServiceRequest.getBaseUrl();
        this.service2 = GWT.create(ThreedAdminService2.class);
        ((ServiceDefTarget) service2).setServiceEntryPoint(baseUrl);


    }

    public void addAllAndCommit(SeriesKey seriesKey, String commitMessage, final AsyncCallback<String> callback) {

        ServiceRequest r = new ServiceRequest();
        r.command = "addAllAndCommit";
        r.put(seriesKey);
        if (notEmpty(commitMessage)) {
            r.put("commitMessage", commitMessage);
        }

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, r.buildFullUrl());


        requestBuilder.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {


                String text = response.getText();
                JSONObject o = JSONParser.parseStrict(text).isObject();

                if (o == null) {
                    callback.onFailure(new RuntimeException("Commit failed. See server log for details."));
                }

                String newCommitId = o.get("newCommitId").isString().stringValue();

                callback.onSuccess(newCommitId);

            }

            @Override
            public void onError(Request request, Throwable e) {
                callback.onFailure(e);
            }
        });


        try {
            requestBuilder.send();
        } catch (RequestException e) {
            callback.onFailure(e);
        }


    }


    public void fetchJpgGenStatus(SeriesId seriesId, JpgWidth jpgWidth, final FetchJpgGenStatusCallback callback) {
        ServiceRequest r = new ServiceRequest();
        r.command = "jpgGenStatus";
        r.put(seriesId);
        r.put(jpgWidth);

        String url = r.buildFullUrl();

//        System.out.println("url = " + url);
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);

        requestBuilder.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {

                String text = response.getText();
                JSONArray jsSlices = JSONParser.parseStrict(text).isArray();


                callback.onSuccess(jsSlices);

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


    public void fetchViews(SeriesId seriesId, final FetchSlicesCallback callback) {

        ServiceRequest r = new ServiceRequest();
        r.command = "views";
        r.put(seriesId);

        String url = r.buildFullUrl();

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);

        requestBuilder.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {

                String text = response.getText();
                JSONArray jsViews = JSONParser.parseStrict(text).isArray();


                callback.onSuccess(jsViews);

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

//
//    public void fetchJpgWidths(final FetchJpgWidthsCallback callback) {
//
//        ServiceRequest r = new ServiceRequest();
//        r.command = "jpgWidths";
//
//        String url = r.buildFullUrl();
//        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
//
//        requestBuilder.setCallback(new RequestCallback() {
//            @Override
//            public void onResponseReceived(Request request, Response response) {
//
//                List<JpgWidth> a = new ArrayList<JpgWidth>();
//                String text = response.getText();
//                JSONArray jsJpgWidths = JSONParser.parseStrict(text).isArray();
//
//                for (int i = 0; i < jsJpgWidths.size(); i++) {
//                    JSONValue width = jsJpgWidths.get(i);
//                    JpgWidth jpgWidth;
//                    if (width == null) {
//                        jpgWidth = JpgWidth.W_STD;
//                    } else if (width.isNumber() != null) {
//                        double doubleValue = width.isNumber().doubleValue();
//                        jpgWidth = new JpgWidth((int) doubleValue);
//                    } else if (width.isString() != null) {
//                        String stringValue = width.isString().stringValue();
//                        jpgWidth = new JpgWidth(stringValue);
//                    } else {
//                        throw new IllegalStateException();
//                    }
//
//
//                    a.add(jpgWidth);
//                }
//
//                callback.onSuccess(a);
//
//            }
//
//            @Override
//            public void onError(Request request, Throwable e) {
//                Window.alert(e.toString());
//                e.printStackTrace();
//            }
//        });
//
//
//        try {
//            requestBuilder.send();
//        } catch (RequestException e) {
//            Window.alert(e.toString());
//            e.printStackTrace();
//        }
//    }
//
//    public void saveJpgWidths(String jpgWidths, final AsyncCallback callback) {
//        ServiceRequest r = new ServiceRequest();
//        r.command = "saveJpgWidths";
//        r.put("jpgWidths", jpgWidths);
//
//        String url = r.buildFullUrl();
//
//        final RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
//        requestBuilder.setCallback(new RequestCallback() {
//            @Override public void onResponseReceived(Request request, Response response) {
//                if (response.getStatusCode() != 200) {
//                    callback.onFailure(new HttpServiceException(response));
//                } else {
//                    callback.onSuccess(null);
//                }
//            }
//
//            @Override public void onError(Request request, Throwable e) {
//                callback.onFailure(e);
//            }
//        });
//
//        try {
//            requestBuilder.send();
//        } catch (RequestException e) {
//            Window.alert(e.toString());
//            e.printStackTrace();
//        }
//
//    }

    public void createNewRepo(String seriesName, String seriesYear, AsyncCallback<String> asyncCallback) {

    }

    public void tagCurrentVersion(SeriesKey seriesKey, String tagName, final AsyncCallback<Void> callback) {
        ServiceRequest r = new ServiceRequest();
        r.command = "tagCurrentVersion";
        r.put(seriesKey);
        r.put("tagName", tagName);

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, r.buildFullUrl());

        requestBuilder.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                callback.onSuccess(null);
            }

            @Override
            public void onError(Request request, Throwable e) {
                callback.onFailure(e);
            }
        });


        try {
            requestBuilder.send();
        } catch (RequestException e) {
            callback.onFailure(e);
        }


    }

    public static class HttpServiceException extends RuntimeException {

        private final int statusCode;
        private final String statusText;
        private final String text;

        public HttpServiceException(int statusCode, String statusText, String text) {
            this.statusCode = statusCode;
            this.statusText = statusText;
            this.text = text;
        }

        public HttpServiceException(Response response) {
            statusCode = response.getStatusCode();
            statusText = response.getStatusText();
            text = response.getText();
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getStatusText() {
            return statusText;
        }

        public String getText() {
            return text;
        }

        @Override public String getMessage() {
            return statusCode + " " + statusText + " " + text;
        }
    }


    public static class ServiceRequest {
        final String baseUrl;
        String command;
        Map<String, String> params = new HashMap<String, String>();


        public ServiceRequest() {
            baseUrl = ServiceRequest.getBaseUrl();
        }

        public static String getBaseUrl() {
            Path hostPageBaseURL = new Path(GWT.getHostPageBaseURL());
            Path url = hostPageBaseURL.append("threedAdminService.json");
            return url.toString();
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
            @Override public void onResponseReceived(Request request, Response response) {
                callBack.onSuccess();
            }

            @Override public void onError(Request request, Throwable e) {
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
            @Override public void onResponseReceived(Request request, Response response) {
                callBack.onSuccess();
            }

            @Override public void onError(Request request, Throwable e) {
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
            @Override public void onResponseReceived(Request request, Response response) {
                callBack.onSuccess();
            }

            @Override public void onError(Request request, Throwable e) {
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
            @Override public void onResponseReceived(Request request, Response response) {

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

            @Override public void onError(Request request, Throwable e) {
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
            @Override public void onResponseReceived(Request request, Response response) {
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

            @Override public void onError(Request request, Throwable e) {
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
            @Override public void onResponseReceived(Request request, Response response) {
                JSONValue jsonValue = JSONParser.parseStrict(response.getText());
                if (jsonValue.isObject() != null && jsonValue.isObject().get("message").isString() != null) {
                    ctx.showMessage("Jpg job started");
                }
            }

            @Override public void onError(Request request, Throwable e) {
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

    public ThreedAdminService2Async getThreedAdminService2Async() {
        return service2;
    }




}
