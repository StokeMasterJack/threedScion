package threed.smartClient.client.api;

import com.google.common.collect.ImmutableSet;
import threed.core.featureModel.shared.FixResult;
import threed.core.featureModel.shared.boolExpr.Var;
import threed.core.imageModel.shared.ImSeries;
import threed.core.threedModel.shared.ThreedModel;
import smartsoft.util.gwt.client.events3.ChangeListener;

public class SeriesSession {

    private final ThreedModel threedModel;
    private final Profile profile;
    private final PicksSession picksSession;
    private final ImSeries imageModel;
    private final ViewSession[] viewSessions;

    private int currentViewIndex = 0;

    public SeriesSession(ThreedModel threedModel, Profile profile, ImageModeSession imageModeSession) {
        this.threedModel = threedModel;
        this.profile = profile;
        this.picksSession = new PicksSession(threedModel);
        this.imageModel = threedModel.getImageModel();

        viewSessions = new ViewSession[imageModel.getViewCount()];
        for (int i = 0; i < imageModel.getViewCount(); i++) {
            viewSessions[i] = new ViewSession(threedModel, imageModel.getView(i), profile, imageModeSession, picksSession, i == 0);
        }
    }

    public void setPicksRaw(final Iterable<String> newValue) {
        picksSession.setPicksRaw(newValue);
    }

    public void setPicks(ImmutableSet<Var> newValue) {
        picksSession.setPicks(newValue);
    }

    public void setPicksFixed(FixResult newValue) {
        picksSession.setPicksFixed(newValue);
    }


    public void previousAngle() {
        getCurrentViewSession().previousAngle();
    }

    public void nextAngle() {
        getCurrentViewSession().nextAngle();
    }

    public void setAngle(int newValue) {
        getCurrentViewSession().setCurrentAngle(newValue);
    }

    public void addImageChangeListener1(ImageChangeListener listener) {
        getCurrentViewSession().addImageChangeListener1(listener);
    }

    public void addImageChangeListener2(ImageChangeListener listener) {
        getCurrentViewSession().addImageChangeListener2(listener);
    }

    public void addAngleChangeListener(ChangeListener<ViewSession, Integer> listener) {
        getCurrentViewSession().addAngleChangeListener(listener);
    }

    public int getAngle() {
        return getCurrentViewSession().getCurrentAngle();
    }

    public ImageStack getImageStack() {
        return getCurrentViewSession().getImageStack();
    }

    @Override
    public String toString() {
        return threedModel.getSeriesKey() + "";
    }

    public ViewSession getCurrentViewSession() {
        return viewSessions[currentViewIndex];
    }

    public int getViewCount() {
        return threedModel.getImageModel().getViewCount();
    }

    public int getCurrentViewIndex() {
        return currentViewIndex;
    }

    public ViewSession getViewSession(int viewIndex) {
        return viewSessions[viewIndex];
    }

    public Profile getProfile() {
        return profile;
    }

}
