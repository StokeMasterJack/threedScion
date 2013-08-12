package c3i.admin.client;

import c3i.admin.shared.BrandInit;
import c3i.admin.shared.ThreedAdminService;
import c3i.admin.shared.ThreedAdminServiceAsync;
import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesKey;
import c3i.core.threedModel.shared.CommitId;
import c3i.core.threedModel.shared.CommitKey;
import c3i.core.threedModel.shared.RootTreeId;
import c3i.repo.shared.CommitHistory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import smartsoft.util.gwt.client.rpc.Req;
import smartsoft.util.gwt.client.rpc.RequestContext;
import smartsoft.util.shared.Path;

public class ThreedAdminClient {

    private static final String THREED_ADMIN_SERVICE = "threedAdminService";

    private final ThreedAdminServiceAsync service;
    private final RequestContext requestContext;

    public ThreedAdminClient(RequestContext requestContext) {
        this.requestContext = requestContext;
        Path url = ThreedAdminClient.getUrlOfThreedAdminService();
        service = GWT.create(ThreedAdminService.class);
        ((ServiceDefTarget) service).setServiceEntryPoint(url.toString());
        requestContext = new RequestContext();
    }

    private <T> Req<T> newRequest(String opName) {
        return requestContext.newRequest(opName);
    }

    public static Path getUrlOfThreedAdminService() {
        Path hostPageBaseURL = new Path(GWT.getHostPageBaseURL());
        Path url = hostPageBaseURL.append(THREED_ADMIN_SERVICE);
        return url;
    }

    public static Path getFullRepoBaseUrl(Path repoContextPath) {
        Path hostPageBaseURL = new Path(GWT.getHostPageBaseURL());
        Path host = hostPageBaseURL.dotDot();
        Path url = host.append(repoContextPath);
        return url;
    }

    public Req<CommitHistory> getCommitHistory(SeriesKey seriesKey) {
        System.err.println("ThreedAdminClient.getCommitHistory");
        Req<CommitHistory> r = newRequest("getCommitHistory");
        service.getCommitHistory(seriesKey, r);
        return r;
    }

    public Req<BrandInit> getInitData(BrandKey brandKey) {
        Req<BrandInit> r = newRequest("getInitData");
        service.getInitData(brandKey, r);
        return r;
    }

    public Req<CommitHistory> addAllAndCommit(SeriesKey seriesKey, String commitMessage, String tag) {
        Req<CommitHistory> r = newRequest("addAllAndCommit");
        service.addAllAndCommit(seriesKey, commitMessage, tag, r);
        return r;
    }

    public Req<CommitHistory> tagCommit(SeriesKey seriesKey, String newTagName, CommitId commitId) {
        Req<CommitHistory> r = newRequest("tagCommit");
        service.tagCommit(seriesKey, newTagName, commitId, r);
        return r;
    }


    public Req<RootTreeId> getVtcRootTreeId(SeriesKey seriesKey) {
        Req<RootTreeId> r = newRequest("getVtcRootTreeId");
        service.getVtcRootTreeId(seriesKey, r);
        return r;
    }

    public Req<CommitHistory> setVtc(SeriesKey seriesKey, CommitKey commitKey) {
        Req<CommitHistory> r = newRequest("setVtcRootTreeId");
        service.setVtc(seriesKey, commitKey, r);
        return r;
    }

    public Req<Void> purgeRepoCache(BrandKey brandKey) {
        Req<Void> r = newRequest("purgeRepoCache");
        service.purgeRepoCache(brandKey, r);
        return r;
    }

    public void log(String msg) {
        requestContext.log(msg);
    }

}