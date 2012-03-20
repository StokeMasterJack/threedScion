package com.tms.threed.jpgGen.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.tms.threed.jpgGen.shared.*;
import smartsoft.util.gwt.client.rpc.Req;
import smartsoft.util.gwt.client.rpc.RequestContext;
import smartsoft.util.gwt.client.rpc.UiContext;
import smartsoft.util.lang.shared.Path;

import java.util.ArrayList;

/**
 * These are the ThreedAdminService remote calls that use RequestBuilder + JSON (i.e. not gwt-rpc)
 */
public class JpgGenClient {

    private static final String CONFIGURATOR_CONTENT = "configurator-content";
    private static final String JPG_GEN_SERVICE = "jpgGenService";

    private final JpgGenServiceAsync service;
    private final RequestContext requestContext;

    public JpgGenClient(UiContext ctx) {
        Path baseUrl = JpgGenClient.getUrlOfJpgGenService();
        service = GWT.create(JpgGenService.class);
        ((ServiceDefTarget) service).setServiceEntryPoint(baseUrl.toString());
        requestContext = new RequestContext();
        requestContext.uiLog = ctx;
    }

    private <T> Req<T> newRequest(String opName) {
        return requestContext.newRequest(opName);
    }

    public static Path getUrlOfJpgGenService() {
        Path hostPageBaseURL = new Path(GWT.getHostPageBaseURL());
        Path url = hostPageBaseURL.append(JPG_GEN_SERVICE);
        return url;
    }

    public static Path getUrlOfRepoService() {
        Path hostPageBaseURL = new Path(GWT.getHostPageBaseURL());
        Path host = hostPageBaseURL.dotDot();
        Path url = host.append(CONFIGURATOR_CONTENT);
        return url;
    }

    public Req<Void> removeTerminal() {
        Req<Void> r = newRequest("removeTerminal");
        service.removeTerminal(r);
        return r;
    }

    public Req<Void> removeJob(JobId jobId) {
        Req<Void> r = newRequest("removeJob");
        service.removeJob(jobId, r);
        return r;
    }

    public Req<Void> cancelJob(JobId jobId) {
        Req<Void> r = newRequest("cancelJob");
        service.cancelJob(jobId, r);
        return r;
    }

    public Req<ArrayList<ExecutorStatus>> getQueueDetails(JobId jobId) {
        Req<ArrayList<ExecutorStatus>> r = newRequest("getQueueDetails");
        service.getQueueDetails(jobId, r);
        return r;
    }

    public Req<ArrayList<JobStatusItem>> getQueueStatus() {
        Req<ArrayList<JobStatusItem>> r = newRequest("getQueueStatus");
        service.getQueueStatus(r);
        return r;
    }

    public Req<Boolean> startJpgJob(final JobSpec jobSpec) {
        Req<Boolean> r = newRequest("startJpgJob");
        service.startJob(jobSpec, r);
        return r;
    }

    public Req<Stats> getJpgGenFinalStats(JobId jobId) {
        Req<Stats> r = newRequest("getJpgGenFinalStats");
        service.getJpgGenFinalStats(jobId, r);
        return r;
    }


}
