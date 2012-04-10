package threed.smartClient.client.api;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import threed.core.threedModel.shared.ThreedModel;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.NoExport;
import threed.smartClient.client.ui.ViewPanelModel;
import threed.smartClient.client.ui.ViewPanelModelImpl;
import smartsoft.util.gwt.client.events3.ChangeListener;
import smartsoft.util.lang.shared.ImageSize;

@Export
public class ThreedSession implements Exportable, ViewPanelModel {

    private final SeriesSession seriesSession;

    private final ImageModeSession imageModeSession;

    @NoExport
    public ThreedSession(final ThreedModel threedModel, final Profile profile) {
        this.imageModeSession = new ImageModeSession();
        this.seriesSession = new SeriesSession(threedModel, profile, imageModeSession);
    }

    @Export
    public void setAngle(final int angle) {
        seriesSession.setAngle(angle);
    }

    @Export
    public int getAngle() {
        return seriesSession.getAngle();
    }

    @Export
    public void nextAngle() {
        seriesSession.nextAngle();
    }

    @Export
    public void previousAngle() {
        seriesSession.previousAngle();
    }

    @NoExport
    public void setPicksRaw(final Iterable<String> picksRaw) {
        seriesSession.setPicksRaw(picksRaw);
    }

    @Export
    public void setPicks(String[] picksRaw) {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (String varCode : picksRaw) {
            builder.add(varCode);
        }
        setPicksRaw(builder.build());
    }

    /**
     * Fires an event when the image stack changes.
     */
    @Export
    public void addImageChangeListener1(ImageChangeListener listener) {
        seriesSession.addImageChangeListener1(listener);
    }

    /**
     * Fires an event when the image stack changes and all of the images
     * in the stack completed loading (or error out).
     */
    @Export
    public void addImageChangeListener2(ImageChangeListener listener) {
        seriesSession.addImageChangeListener2(listener);
    }


    @Export
    public ImageStack getImageStack() {
        return seriesSession.getImageStack();
    }

    @Export
    public String getView() {
        return seriesSession.getCurrentViewSession().getView().getName();
    }

    @Export
    @Override
    public String toString() {
        return seriesSession.toString();
    }

    @NoExport
    public void setImageMode(ImageMode imageMode) {
        imageModeSession.setImageMode(imageMode);
    }

    @NoExport
    public ImageMode getImageMode() {
        return imageModeSession.getImageMode();
    }

    @Export
    public void setImageModeString(String imageModeString) {
        Preconditions.checkNotNull(imageModeString);
        ImageMode imageMode = ImageMode.valueOf(imageModeString.toUpperCase());
        imageModeSession.setImageMode(imageMode);
    }

    public void addImageModeChangeListener(ChangeListener<ImageModeSession, ImageMode> listener) {
        imageModeSession.addImageModeChangeListener(listener);
    }

    @NoExport
    public ViewSession getCurrentViewSession() {
        return seriesSession.getCurrentViewSession();
    }

    @NoExport
    public ViewSession getViewSessionForPanel(int panelIndex) {
        int viewCount = seriesSession.getViewCount();
        int currentView = seriesSession.getCurrentViewIndex();
        int panelViewIndex = (currentView + panelIndex) % viewCount;
        return seriesSession.getViewSession(panelViewIndex);
    }

    @NoExport
    public Profile getProfile() {
        return seriesSession.getProfile();
    }

    @NoExport
    public ViewPanelModel createViewPanelModel(int panelIndex) {
        return new ViewPanelModelImpl(this, panelIndex);
    }

    @NoExport
    public ViewPanelModel createViewPanelModel() {
        return new ViewPanelModelImpl(this, 0);
    }

    @NoExport
    @Override
    public ImageSize getImageSize() {
        return getProfile().getImage();
    }

    @Override
    public ViewSession getViewSession() {
        return getCurrentViewSession();
    }

    @Override
    public boolean isVisible() {
        return getViewSession().isVisible();
    }

    @Override
    public void addAngleChangeListener(ChangeListener<ViewSession, Integer> listener) {
        getViewSession().addAngleChangeListener(listener);
    }
}
