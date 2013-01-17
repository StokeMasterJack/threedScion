package c3i.admin.client;

import smartsoft.util.shared.Path;
import c3i.admin.client.featurePicker.CurrentUiPicks;
import c3i.core.common.shared.SeriesId;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.FixedPicks;
import c3i.smartClient.client.model.ImageStack;
import c3i.smartClient.client.model.ViewSession;

public class StatusPanelModel {

    private ViewSession viewSession;
    private Series series;

    public StatusPanelModel(ViewSession viewSession, Series series) {
        this.viewSession = viewSession;
        this.series = series;
    }

    public ImageStack getImageStack() {
        return viewSession.getImageStack();
    }

    public Path getRepoBaseUrl() {
        return viewSession.getRepoBaseURl();
    }

    public String getUserPicks() {
        CurrentUiPicks currentUiPicks = series.getCurrentUiPicks();
        if (currentUiPicks == null) return "";
        else return currentUiPicks.getCurrentTrueUiVars() + "";
    }

    public String getFixedPicksAsString() {
        FixedPicks fixedPicks = getFixedPicks();
        return fixedPicks.toStringLong();
    }

    public FixedPicks getFixedPicks() {
        return viewSession.getFixedPicks();
    }


    public CurrentUiPicks getCurrentUiPicks() {
        return series.getCurrentUiPicks();
    }

    public FeatureModel getFeatureModel() {
        return series.getThreedModel().getFeatureModel();
    }


    public String getThreedModelUrl() {
        return series.getThreedModelUrl() + "";
    }

    public SeriesId getSeriesId() {
        return series.getSeriesId();
    }

    public ViewSession getViewSession() {
        return viewSession;
    }
}
