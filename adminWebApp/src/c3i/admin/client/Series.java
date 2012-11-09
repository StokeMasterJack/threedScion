package c3i.admin.client;

import c3i.admin.client.featurePicker.CurrentUiPicks;
import c3i.admin.shared.BrandInit;
import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.imageModel.shared.Profile;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.repo.shared.CommitHistory;
import c3i.smartClient.client.model.ViewsSession;
import c3i.smartClient.client.service.ThreedModelClient;
import c3i.util.shared.futures.AsyncKeyValue;
import c3i.util.shared.futures.RWValue;
import smartsoft.util.lang.shared.Path;

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

    public Series(App app, BrandInit brand, ThreedModel threedModel, final RWValue<CommitHistory> commit) {
        this.app = app;
        this.brand = brand;
        this.threedModel = threedModel;

        this.commit = commit;

        this.seriesId = new SeriesId(threedModel.getSeriesKey(), commit.get().getRootTreeId());

        Profile defaultProfile = brand.getProfiles().getDefaultProfile();
        defaultProfile.getBaseImageType();

        currentUiPicks = new CurrentUiPicks(threedModel);

        AsyncKeyValue<Set<Var>, FixedPicks> fixedPicks = currentUiPicks.getFixedPicks();
        viewsSession = new ViewsSession(brand.getRepoBaseUrl(), threedModel, defaultProfile, fixedPicks);
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
        ThreedModelClient threedModelClient = brand.getThreedModelClient();
        return threedModelClient.getThreedModelUrl(seriesId);
    }

    public SeriesId getSeriesId() {
        return seriesId;
    }


    public Path getRepoBaseURl() {
        return brand.getRepoBaseUrl();
    }
}
