package threed.admin.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import threed.repo.shared.CommitHistory;
import threed.repo.shared.CommitId;
import threed.repo.shared.Settings;
import threed.admin.shared.InitData;
import threed.admin.shared.ThreedAdminService;
import threed.admin.shared.ThreedAdminServiceAsync;
import threed.core.threedModel.shared.RootTreeId;
import threed.core.threedModel.shared.SeriesKey;
import smartsoft.util.gwt.client.rpc.Req;
import smartsoft.util.gwt.client.rpc.RequestContext;
import smartsoft.util.gwt.client.rpc.UiContext;
import smartsoft.util.lang.shared.Path;

public class ThreedAdminClient {

    private static final String CONFIGURATOR_CONTENT = "configurator-content";
    private static final String THREED_ADMIN_SERVICE = "threedAdminService";

    private final ThreedAdminServiceAsync service;
    private final RequestContext requestContext;

    public ThreedAdminClient(UiContext ctx) {
        Path url = ThreedAdminClient.getUrlOfThreedAdminService();
        service = GWT.create(ThreedAdminService.class);
        ((ServiceDefTarget) service).setServiceEntryPoint(url.toString());
        requestContext = new RequestContext();
        requestContext.uiLog = ctx;
    }

    private <T> Req<T> newRequest(String opName) {
        return requestContext.newRequest(opName);
    }

    public static Path getUrlOfThreedAdminService() {
        Path hostPageBaseURL = new Path(GWT.getHostPageBaseURL());
        Path url = hostPageBaseURL.append(THREED_ADMIN_SERVICE);
        return url;
    }

    public static Path getUrlOfRepoService() {
        Path hostPageBaseURL = new Path(GWT.getHostPageBaseURL());
        Path host = hostPageBaseURL.dotDot();
        Path url = host.append(CONFIGURATOR_CONTENT);
        return url;
    }

    public Req<CommitHistory> getCommitHistory(SeriesKey seriesKey) {
        Req<CommitHistory> r = newRequest("getCommitHistory");
        service.getCommitHistory(seriesKey, r);
        return r;
    }

    public Req<InitData> getInitData() {
        Req<InitData> r = newRequest("getInitData");
        service.getInitData(r);
        return r;
    }

    public Req<CommitHistory> addAllAndCommit(SeriesKey seriesKey, String commitMessage, String tag) {
        Req<CommitHistory> r = newRequest("addAllAndCommit");
        service.addAllAndCommit(seriesKey, commitMessage, tag, r);
        return r;
    }

    public Req<Void> saveSettings(Settings settings) {
        Req<Void> r = newRequest("saveSettings");
        service.saveSettings(settings, r);
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

    public Req<Void> setVtcRootTreeId(SeriesKey seriesKey, RootTreeId rootTreeId) {
        Req<Void> r = newRequest("setVtcRootTreeId");
        service.setVtcRootTreeId(seriesKey, rootTreeId, r);
        return r;
    }

    public Req<Void> purgeRepoCache() {
        Req<Void> r = newRequest("purgeRepoCache");
        service.purgeRepoCache(r);
        return r;
    }


}