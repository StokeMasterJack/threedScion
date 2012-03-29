package smartClient.client;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.tms.threed.threedCore.threedModel.shared.ThreedModel;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.NoExport;
import smartsoft.util.gwt.client.events3.ChangeListener;

public class ThreedSession implements Exportable {

    private final SeriesSession seriesSession;

    private final ImageModeSession imageModeSession;

    @NoExport
    public ThreedSession(final ThreedModel threedModel, final Profile profile) {
        this.imageModeSession = new ImageModeSession();
        this.seriesSession = new SeriesSession(threedModel, profile,imageModeSession);
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

    @Export
    public void addImageChangeListener1(ImageChangeListener listener) {
        seriesSession.addImageChangeListener1(listener);
    }

    @Export
    public void addImageChangeListener2(ImageChangeListener listener) {
        seriesSession.addImageChangeListener2(listener);
    }


    @Export
    public ImageBatch getImageBatch() {
        return seriesSession.getImageBatch();
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

    @Export
    public ViewSession getCurrentViewSession() {
        return seriesSession.getCurrentViewSession();
    }
}
