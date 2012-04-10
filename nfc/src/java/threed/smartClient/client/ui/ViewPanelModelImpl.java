package threed.smartClient.client.ui;

import threed.smartClient.client.api.ImageStack;
import threed.smartClient.client.api.ViewSession;
import threed.smartClient.client.api.ImageChangeListener;
import threed.smartClient.client.api.ThreedSession;
import smartsoft.util.gwt.client.events3.ChangeListener;
import smartsoft.util.lang.shared.ImageSize;

 public class ViewPanelModelImpl implements ViewPanelModel {

    private final ThreedSession threedSession;
    private final int panelIndex;

    public ViewPanelModelImpl(ThreedSession threedSession, int panelIndex) {
        this.threedSession = threedSession;
        this.panelIndex = panelIndex;
    }

    @Override
    public ImageSize getImageSize() {
        return threedSession.getProfile().getImage();
    }

    @Override
    public ViewSession getViewSession() {
        return threedSession.getViewSessionForPanel(panelIndex);
    }

    @Override
    public ImageStack getImageStack() {
        return getViewSession().getImageStack();
    }

    @Override
    public boolean isVisible() {
        return getViewSession().isVisible();
    }

    @Override
    public void addImageChangeListener1(ImageChangeListener listener) {
        getViewSession().addImageChangeListener1(listener);
    }

    @Override
    public void addImageChangeListener2(ImageChangeListener listener) {
        getViewSession().addImageChangeListener2(listener);
    }

    @Override
    public void addAngleChangeListener(ChangeListener<ViewSession, Integer> listener) {
        getViewSession().addAngleChangeListener(listener);
    }

}
