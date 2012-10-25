package c3i.jpgGen.client;

import c3i.core.common.shared.BrandKey;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import c3i.jpgGen.shared.*;
import smartsoft.util.gwt.client.rpc.Req;
import smartsoft.util.gwt.client.rpc.RequestContext;
import smartsoft.util.gwt.client.ui.UiContext;
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

    private final BrandKey brandKey;

    public JpgGenClient(BrandKey brandKey) {
        this.brandKey = brandKey;
        Path baseUrl = JpgGenClient.getUrlOfJpgGenService();
        service = GWT.create(JpgGenService.class);
        ((ServiceDefTarget) service).setServiceEntryPoint(baseUrl.toString());
        requestContext = new RequestContext();
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
        service.removeTerminal(brandKey, r);
        return r;
    }

    public Req<Void> removeJob(JobId jobId) {
        Req<Void> r = newRequest("removeJob");
        service.removeJob(brandKey, jobId, r);
        return r;
    }

    public Req<Void> cancelJob(JobId jobId) {
        Req<Void> r = newRequest("cancelJob");
        service.cancelJob(brandKey, jobId, r);
        return r;
    }

    public Req<ArrayList<ExecutorStatus>> getQueueDetails(JobId jobId) {
        Req<ArrayList<ExecutorStatus>> r = newRequest("getQueueDetails");
        service.getQueueDetails(brandKey, jobId, r);
        return r;
    }

    public Req<ArrayList<JobStatusItem>> getQueueStatus() {
        Req<ArrayList<JobStatusItem>> r = newRequest("getQueueStatus");
        service.getQueueStatus(brandKey, r);
        return r;
    }

    public Req<Boolean> startJpgJob(final JobSpec jobSpec) {
        jobSpec.getProfile().getBaseImageType();
        Req<Boolean> r = newRequest("startJpgJob");
        service.startJob(jobSpec, r);
        return r;
    }

    public Req<Stats> getJpgGenFinalStats(JobId jobId) {
        Req<Stats> r = newRequest("getJpgGenFinalStats");
        service.getJpgGenFinalStats(brandKey, jobId, r);
        return r;
    }


}
