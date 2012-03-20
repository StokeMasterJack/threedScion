package com.tms.threed.threedAdmin.server;

import com.google.gwt.rpc.server.ClientOracle;
import com.google.gwt.rpc.server.RpcServlet;
import com.google.gwt.user.client.rpc.SerializationException;
import com.tms.threed.repo.server.*;
import com.tms.threed.repo.shared.*;
import com.tms.threed.threedAdmin.shared.InitData;
import com.tms.threed.threedAdmin.shared.ThreedAdminService;
import com.tms.threed.threedCore.threedModel.shared.RootTreeId;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;

import static smartsoft.util.StringUtil.notEmpty;
import static smartsoft.util.date.shared.StringUtil.isEmpty;


/**
 * Processes calls to <repo-url-base>/threedAdminService.json
 * <p/>
 * POST requests are handled in this servlet as gwt-rpc calls
 * GET requests are handled by RestHandler
 */
public class ThreedAdminServlet extends RpcServlet implements ThreedAdminService {

    private final static Log log;

    static {
        log = LogFactory.getLog(ThreedAdminServlet.class);
    }

    private Repos repos;

    private String initErrorMessage;

    public ThreedAdminServlet() {
        log.info("Initializing " + getClass().getSimpleName());
        File repoBaseDir = ThreedConfig.getRepoBaseDir();
        Repos.setRepoBaseDir(repoBaseDir);

        try {
            this.repos = Repos.get();
            log.info(getClass().getSimpleName() + " initialization complete!");
        } catch (Throwable e) {
            this.initErrorMessage = "Problem initializing ThreedAdminServletJson: " + e;
            log.error(initErrorMessage, e);
        }

    }

    @Override
    public InitData getInitData() {
        ArrayList<Series> seriesNamesWithYears = repos.getSeriesNamesWithYears();
        Settings settings = repos.getSettings();
        log.info("serving [" + settings + "]");
        return new InitData(seriesNamesWithYears, settings);
    }

    /**
     * Redundant with rest-json call: /configurator-content/avalon/2011/vtc.txt
     */
    @Override
    public RootTreeId getVtcRootTreeId(SeriesKey seriesKey) {
        return repos.getVtcRootTreeId(seriesKey);
    }

    @Override
    public void setVtcRootTreeId(SeriesKey seriesKey, RootTreeId rootTreeId) {
        repos.setVtcCommitId(seriesKey, rootTreeId);
        log.info(seriesKey + " VTC set to [" + rootTreeId + "]");
    }

    @Override
    public Settings getSettings() {
        return repos.getSettingsHelper().read();
    }

    @Override
    public void saveSettings(Settings config) {
        repos.getSettingsHelper().save(config);
    }

    @Override
    public CommitHistory getCommitHistory(SeriesKey seriesKey) throws RepoHasNoHeadException {
        SeriesRepo seriesRepo = repos.getSeriesRepo(seriesKey);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();
        log.info("Building commit history on server..");
        final CommitHistory commitHistory = srcRepo.getHeadCommitHistory();
        log.info("Building commit history on server complete!");
        return commitHistory;
    }

    @Override
    public void processCall(ClientOracle clientOracle, String payload, OutputStream stream) throws SerializationException {
        super.processCall(clientOracle, payload, stream);
    }

    @Override
    public CommitHistory tagCommit(SeriesKey seriesKey, String newTagName, CommitId commitId) {
        log.info("Tagging commit[" + commitId + "] with tag[" + newTagName + "]");
        SeriesRepo seriesRepo = repos.getSeriesRepo(seriesKey);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();
        final ObjectId objectId = srcRepo.tagCommit(newTagName, commitId);
        final CommitHistory commitHistory = srcRepo.getCommitHistory(objectId);
        return commitHistory;
    }

    @Override
    public CommitHistory addAllAndCommit(SeriesKey seriesKey, String commitMessage, String tag) {

        try {
            SeriesRepo seriesRepo = repos.getSeriesRepo(seriesKey);

            SrcRepo srcRepo = seriesRepo.getSrcRepo();

            if (isEmpty(commitMessage)) {
                commitMessage = System.currentTimeMillis() + "";
            }

            RevCommit revCommit = srcRepo.addAllAndCommit(commitMessage);
            ObjectId newCommitId = revCommit.getId();
            BlinkCheckin.processBlinks(srcRepo.getGitRepo(), revCommit);

            if (notEmpty(tag)) {
                srcRepo.tagCommit(tag, revCommit);
            }

            CommitHistory commitHistory = srcRepo.getCommitHistory(newCommitId, true);

            return commitHistory;
        } catch (Exception e) {
            e.printStackTrace();
            final String msg = "Error in ThreedAdminServlet.addAllAndCommit(" + seriesKey + "," + commitMessage + "," + tag + "). See server log for details.";
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }

    }

    @Override
    public void purgeRepoCache() {
        repos.purgeCache();
    }


    @Override
    protected void doUnexpectedFailure(Throwable e) {
        log.error("Problem in RPC method", e);
        super.doUnexpectedFailure(e);
    }


}
