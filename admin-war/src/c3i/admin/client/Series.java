package c3i.admin.client;

import c3i.admin.client.featurePicker.CurrentUiPicks;
import c3i.admin.shared.BrandInit;
import c3i.featureModel.shared.FixedPicks;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.common.BrandKey;
import c3i.featureModel.shared.common.SeriesId;
import c3i.imageModel.shared.Profile;
import c3i.repo.shared.CommitHistory;
import c3i.smartClient.client.model.ViewsSession;
import c3i.threedModel.shared.ThreedModel;
import c3i.util.shared.futures.AsyncKeyValue;
import c3i.util.shared.futures.RWValue;
import smartsoft.util.shared.Path;

import java.util.Set;

public class Series {

    private final App app;
    private final BrandInit brand;
    private final ThreedModel threedModel;

    private final CurrentUiPicks currentUiPicks;
    private final ViewsSession viewsSession;

    private final RWValue<CommitHistory> commit;

    private ThreedAdminModel threedAdminModel;

    //cache
    private final SeriesId seriesId;

    public Series(App app, BrandInit brand, ThreedModel threedModel, final RWValue<CommitHistory> commit, String initView) {
        this.app = app;
        this.brand = brand;
        this.threedModel = threedModel;

        this.commit = commit;

        this.seriesId = new SeriesId(threedModel.getSeriesKey(), commit.get().getRootTreeId());

        Profile defaultProfile = brand.getProfiles().getDefaultProfile();
        defaultProfile.getBaseImageType();

        currentUiPicks = new CurrentUiPicks(threedModel);

        AsyncKeyValue<Set<Var>, FixedPicks> fixedPicks = currentUiPicks.getFixedPicks();
        viewsSession = new ViewsSession(app.getRepoBaseUrl(), threedModel, defaultProfile, fixedPicks);
        if (initView != null && viewsSession.isValidViewName(initView)) {
            viewsSession.setView(initView);
        }
    }

    public CurrentUiPicks getCurrentUiPicks() {
        return currentUiPicks;
    }

    public ThreedAdminModel getThreedAdminModel() {
        if (threedAdminModel == null) {
            BrandKey brandKey = brand.getBrandKey();
            threedAdminModel = new ThreedAdminModel(this, viewsSession, this.commit, brand.getProfiles());
        }
        return threedAdminModel;
    }

    public BrandKey getBrandKey() {
        return brand.getBrandKey();
    }

    public App getApp() {
        return app;
    }

    public ViewsSession getViewsSession() {
        return viewsSession;
    }

    public ThreedModel getThreedModel() {
        return threedModel;
    }

    public Path getThreedModelUrl() {
        return app.getThreedModelClient().getThreedModelUrl(seriesId);
    }

    public SeriesId getSeriesId() {
        return seriesId;
    }


    public void log(String msg) {
        app.getUserLog().log(msg);
    }
}
