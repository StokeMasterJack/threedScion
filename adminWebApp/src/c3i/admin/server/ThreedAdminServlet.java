package c3i.admin.server;

import c3i.admin.shared.BrandInit;
import c3i.admin.shared.ThreedAdminService;
import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesKey;
import c3i.core.imageModel.shared.Profiles;
import c3i.core.threedModel.shared.CommitId;
import c3i.core.threedModel.shared.CommitKey;
import c3i.core.threedModel.shared.RootTreeId;
import c3i.repo.server.BlinkCheckin;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.Repos;
import c3i.repo.server.SeriesRepo;
import c3i.repo.server.SrcRepo;
import c3i.repo.shared.CommitHistory;
import c3i.repo.shared.RepoHasNoHeadException;
import c3i.repo.shared.Series;
import c3i.repo.shared.Settings;
import com.google.common.base.Preconditions;
import com.google.gwt.rpc.server.RpcServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import smartsoft.util.lang.shared.Path;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.security.Principal;
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

    private ThreedAdminApp app;
    private Log log;
    private BrandRepos brandRepos;
    private String initErrorMessage;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log = LogFactory.getLog(ThreedAdminFilter.class);
        log.info("Initializing " + getClass().getSimpleName());

        try {
            app = ThreedAdminApp.getFromServletContext(config.getServletContext());
            Preconditions.checkNotNull(app);
            brandRepos = app.getBrandRepos();
            log.info(getClass().getSimpleName() + " initialization complete!");
        } catch (Throwable e) {
            this.initErrorMessage = "Problem initializing ThreedAdminServletJson: " + e;
            log.error(initErrorMessage, e);
        }

    }

    private void checkCacheFile(int msg) {
        String s = "/configurator-content-toyota/cache";
        File file = new File(s);
        if (file.exists()) {
            System.out.println(msg + " Yes");
        } else {
            System.out.println(msg + " No");
        }
    }

    @Override
    public BrandInit getInitData(BrandKey brandKey) {
        Preconditions.checkNotNull(brandKey);
        Preconditions.checkNotNull(app);
        Path repoContextPath = app.getRepoContextPath();


        Repos repos = brandRepos.getRepos(brandKey);

        ArrayList<Series> seriesNamesWithYears = repos.getSeriesNamesWithYears();
        Settings settings = repos.getSettings();
        log.info("serving [" + settings + "]");
        Principal userPrincipal = getThreadLocalRequest().getUserPrincipal();
        String userName;
        if (userPrincipal == null) {
            userName = "NullUserName";
        } else {
            userName = userPrincipal.getName();
            if (userName == null) {
                userName = "NullUserName";
            }
        }


        ArrayList<BrandKey> visibleBrandsForUser = getVisibleBrandsForUser();

        Profiles profiles = repos.getProfilesCache().getProfiles(brandKey);

        BrandInit brandInit = new BrandInit(brandKey, seriesNamesWithYears, settings, userName, visibleBrandsForUser, repoContextPath, profiles);
        return brandInit;
    }

    private ArrayList<BrandKey> getVisibleBrandsForUser() {
        HttpServletRequest request = getThreadLocalRequest();
        ArrayList<BrandKey> a = new ArrayList<BrandKey>();

        for (BrandKey brandKey : BrandKey.getAll()) {
            String key = brandKey.getKey();
            if (request.isUserInRole(key)) {
//                System.out.println("Current user IS in role[" + key + "]");
                a.add(brandKey);
            } else {
//                System.out.println("Current user is NOT in role[" + key + "]");

            }
        }

        return a;
    }

    /**
     * Redundant with rest-json call: /configurator-content/avalon/2011/vtc.txt
     */
    @Override
    public RootTreeId getVtcRootTreeId(SeriesKey seriesKey) {
        return brandRepos.getRepos(seriesKey.getBrandKey()).getVtcRootTreeId(seriesKey);
    }

    @Override
    public CommitHistory setVtc(SeriesKey seriesKey, CommitKey commitKey) {
        Repos repos = brandRepos.getRepos(seriesKey.getBrandKey());
        CommitHistory commitHistory = repos.setVtcCommitId(seriesKey, commitKey);
        log.info(seriesKey + " VTC set to [" + commitKey.getRootTreeId() + "]");
        return commitHistory;
    }

    @Override
    public Settings getSettings(BrandKey brandKey) {
        Repos repos = brandRepos.getRepos(brandKey);
        return repos.getSettingsHelper().read();
    }

    @Override
    public void saveSettings(BrandKey brandKey, Settings config) {
        Repos repos = brandRepos.getRepos(brandKey);
        repos.getSettingsHelper().save(config);
    }

    @Override
    public CommitHistory getCommitHistory(SeriesKey seriesKey) throws RepoHasNoHeadException {
        Repos repos = brandRepos.getRepos(seriesKey.getBrandKey());
        SeriesRepo seriesRepo = repos.getSeriesRepo(seriesKey);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();
        log.info("Building commit history on server..");
        final CommitHistory commitHistory = srcRepo.getHeadCommitHistory();
        log.info("Building commit history on server complete!");
        return commitHistory;
    }

    @Override
    public CommitHistory tagCommit(SeriesKey seriesKey, String newTagName, CommitId commitId) {
        log.info("Tagging commit[" + commitId + "] with tag[" + newTagName + "]");
        Repos repos = brandRepos.getRepos(seriesKey.getBrandKey());
        SeriesRepo seriesRepo = repos.getSeriesRepo(seriesKey);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();
        ObjectId objectId = srcRepo.tagCommit(newTagName, commitId);
        return srcRepo.getCommitHistory(objectId);
    }

    @Override
    public CommitHistory addAllAndCommit(SeriesKey seriesKey, String commitMessage, String tag) {

        try {
            Repos repos = brandRepos.getRepos(seriesKey.getBrandKey());
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
    public void purgeRepoCache(BrandKey brandKey) {
        Repos repos = brandRepos.getRepos(brandKey);
        repos.purgeCache();
    }


    @Override
    protected void doUnexpectedFailure(Throwable e) {
        log.error("Problem in RPC method", e);
        super.doUnexpectedFailure(e);
    }


}
