package c3i.imgGen;

import c3i.imageModel.shared.ImView;
import c3i.imgGen.external.ImgGenContext;
import c3i.imgGen.server.JpgSet;

import static com.google.common.base.Preconditions.checkState;

public class ExternalSatCounter {

    public static enum State {NOT_STARTED, COMPUTING_SAT_COUNT, COMPLETE}

    //core state
    private final ImgGenContext ctx;
    private final ImView view;
    private final int angle;

    private long satCount = -1;

    private State state = State.NOT_STARTED;

    public ExternalSatCounter(ImgGenContext ctx, ImView view, int angle) {
        this.ctx = ctx;
        this.view = view;
        this.angle = angle;

    }

    public void start() {
        checkState(state == State.NOT_STARTED);

        state = State.COMPUTING_SAT_COUNT;
        satCount = ctx.getSatCount(view.getPngVars());

        state = State.COMPLETE;
    }

    public long getSatCount() {
        return satCount;
    }

    public State getState() {
        return state;
    }

    public ImView getView() {
        return view;
    }

    public int getAngle() {
        return angle;
    }

    public ImgGenContext getCtx() {
        return ctx;
    }

    public JpgSet.JpgSetKey getJpgSetKey() {
        return new JpgSet.JpgSetKey(ctx.getSeriesId(), view.getName(), angle);
    }
}
