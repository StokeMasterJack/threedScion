package smartClient.client;

import com.google.common.collect.ImmutableSet;
import com.tms.threed.threedCore.featureModel.shared.FixResult;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedCore.imageModel.shared.ImSeries;
import com.tms.threed.threedCore.threedModel.shared.ThreedModel;
import smartsoft.util.gwt.client.events3.ChangeListener;

public class SeriesSession {

    private final ThreedModel threedModel;
    private final Profile profile;
    private final PicksSession picksSession;
    private final ImSeries imageModel;
    private final ViewSession[] viewSessions;
    private final ViewSession viewSession;

    public SeriesSession(ThreedModel threedModel, Profile profile,ImageModeSession imageModeSession) {
        this.threedModel = threedModel;
        this.profile = profile;
        this.picksSession = new PicksSession(threedModel);
        this.imageModel = threedModel.getImageModel();

        viewSessions = new ViewSession[imageModel.getViewCount()];
        for (int i = 0; i < imageModel.getViewCount(); i++) {
            viewSessions[i] = new ViewSession(threedModel, imageModel.getView(i), profile,imageModeSession,picksSession,i==0);
        }
        viewSession = viewSessions[0];


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
        viewSession.previousAngle();
    }

    public void nextAngle() {
        viewSession.nextAngle();
    }

    public void setAngle(int newValue) {
        viewSession.setCurrentAngle(newValue);
    }

    public void addImageChangeListener1(ImageChangeListener listener) {
        viewSession.addImageChangeListener1(listener);
    }

    public void addImageChangeListener2(ImageChangeListener listener) {
        viewSession.addImageChangeListener2(listener);
    }

    public void addAngleChangeListener(ChangeListener<ViewSession, Integer> listener) {
        viewSession.addAngleChangeListener(listener);
    }

    public int getAngle() {
        return viewSession.getCurrentAngle();
    }

    public ImageBatch getImageBatch() {
        return viewSession.getImageBatch();
    }

    @Override
    public String toString() {
        return threedModel.getSeriesKey() + "";
    }

    public ViewSession getCurrentViewSession() {
        return viewSession;
    }
}