package c3i.admin.client;

import c3i.admin.client.jpgGen.JpgGenClient;
import c3i.admin.client.messageLog.UserLog;
import c3i.admin.shared.BrandInit;
import c3i.featureModel.shared.common.BrandKey;
import c3i.featureModel.shared.common.SeriesKey;
import c3i.repo.shared.CommitHistory;
import c3i.smartClient.client.service.ThreedModelClient;
import c3i.util.shared.futures.Future;
import c3i.util.shared.futures.Loader;
import com.google.gwt.user.client.Window;
import smartsoft.util.gwt.client.rpc.Req;
import smartsoft.util.gwt.client.rpc.RequestContext;
import smartsoft.util.gwt.client.rpc.SuccessCallback;
import smartsoft.util.gwt.client.ui.tabLabel.TabCreator;
import smartsoft.util.shared.Path;

import static smartsoft.util.shared.StringUtil.isEmpty;

/**
 * Note: the repoContextPath in configFile is never used
 */
public class App {

    private final TabCreator tabCreator;
    private final BrandKey brandKey;
    private final Path repoBaseUrl;
    private final UserLog userLog;

    private final RequestContext requestContext;

    private final ThreedModelClient threedModelClient;
    private final ThreedAdminClient threedAdminClient;
    private final JpgGenClient jpgGenClient;
    private final Loader<BrandKey, BrandInit> loader;

    public App(TabCreator tabCreator) {
        this.tabCreator = tabCreator;

        brandKey = initBrandKey();
        repoBaseUrl = initRepoBaseUrl();

        userLog = UserLog.get();

        requestContext = new RequestContext();
        requestContext.setUserLog(userLog);

        threedAdminClient = new ThreedAdminClient(requestContext);

        jpgGenClient = new JpgGenClient(requestContext,brandKey);

        threedModelClient = new ThreedModelClient(repoBaseUrl);
        threedModelClient.setUserLog(userLog);

        loader = new BrandLoader(brandKey, threedAdminClient);
        loader.ensureLoaded();

    }

    public static BrandKey initBrandKey() {
        String brand = Window.Location.getParameter("brand");
        if (isEmpty(brand)) {
            return BrandKey.TOYOTA;
        } else {
            return BrandKey.fromString(brand);
        }
    }

    public static Path initRepoBaseUrl() {
        String repoBaseUrl = Window.Location.getParameter("repoBaseUrl");
        if (isEmpty(repoBaseUrl)) {
            return ThreedModelClient.DEFAULT_REPO_BASE_URL;
        } else {
            return new Path(repoBaseUrl);
        }
    }

    public TabCreator getTabCreator() {
        return tabCreator;
    }

    public ThreedAdminClient getThreedAdminClient() {
        return threedAdminClient;
    }

    public ThreedModelClient getThreedModelClient() {
        return threedModelClient;
    }

    public JpgGenClient getJpgGenClient() {
        return jpgGenClient;
    }

    public void localCheckin(final SeriesKey sk) {
        log("Check-in in [" + sk.getShortName() + "]...");
        Req<CommitHistory> r = threedAdminClient.addAllAndCommit(sk, null, null);
        r.onSuccess = new SuccessCallback<CommitHistory>() {
            @Override
            public void call(Req<CommitHistory> request) {
                log("Checkin of [" + sk.getShortName() + "] complete!");
            }
        };
    }

    public UserLog getUserLog() {
        return userLog;
    }

    public Future<BrandInit> ensureLoaded() {
        return loader.ensureLoaded();
    }

    public BrandKey getBrandKey() {
        return brandKey;
    }

    public Path getRepoBaseUrl() {
        return repoBaseUrl;
    }

    public void log(String msg){
        userLog.log(msg);
    }
}
