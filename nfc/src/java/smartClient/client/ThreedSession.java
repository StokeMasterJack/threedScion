package smartClient.client;

import com.google.common.collect.ImmutableSet;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.NoExport;

@Export
public class ThreedSession implements Exportable {

    private final SeriesSession session;

    @NoExport
    public ThreedSession(final SeriesSession session) {
        this.session = session;
    }
//
//
//    @NoExport
//    public void setAngle(final int angle) {
//        session.setAngle(angle);
//    }
//
    @Export
    public int getAngle() {
        return session.getAngle();
    }

    @Export
    public void nextAngle() {
        session.nextAngle();
    }

    @Export
    public void previousAngle() {
        session.previousAngle();
    }

    @NoExport
    public void setPicksRaw(final Iterable<String> picksRaw) {
        session.setPicksRaw(picksRaw);
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
        session.addImageChangeListener1(listener);
    }

    @Export
    public void addImageChangeListener2(ImageChangeListener listener) {
        session.addImageChangeListener2(listener);
    }


    @Export
    public ImageBatch getImageBatch() {
        return session.getImageBatch();
    }

    @Export
    public String getView() {
        return session.getViewSession().getView().getName();
    }

    @Export
    @Override
    public String toString() {
//        return session.toString();
        return "ThreedSession";
    }

    @Export
    public String poop() {
        return "dude";
    }


}
