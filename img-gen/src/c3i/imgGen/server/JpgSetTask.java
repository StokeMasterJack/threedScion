package c3i.imgGen.server;

import c3i.core.common.shared.ProductHandler;
import c3i.imageModel.shared.RawBaseImage;
import c3i.imageModel.shared.SimplePicks;
import c3i.imageModel.shared.Slice2;
import c3i.imgGen.external.ImgGenContext;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

public class JpgSetTask {

    public static enum State {NOT_STARTED, IN_PROCESS, COMPLETE}

    //core state
    private final ImgGenContext ctx;
    private final Slice2 slice;

    private final MyProductHandler productHandler;

    //computed
    private final Set<Object> outVars;

    //compute onProduct
    private State state;

    public JpgSetTask(ImgGenContext ctx, Slice2 slice) {
        this.ctx = ctx;
        this.slice = slice;

        this.outVars = slice.getPngVars();
        this.productHandler = new MyProductHandler();

        state = State.NOT_STARTED;
    }

    public void start() {
        checkState(state == State.NOT_STARTED);

        state = State.IN_PROCESS;
        ctx.forEach(outVars, productHandler);
        state = State.COMPLETE;

    }

    public State getState() {
        return state;
    }

    public Slice2 getSlice() {
        return slice;
    }


    public HashSet<RawBaseImage> getJpgSet() {
        return productHandler.jpgSet;
    }

    public int getJpgCount() {
        return getJpgSet().size();
    }

    public ImgGenContext getCtx() {
        return ctx;
    }

    public JpgSetKey getJpgSetKey() {
        return new JpgSetKey(ctx.getSeriesId(), slice.getSlice());
    }


    private class MyProductHandler implements ProductHandler<SimplePicks> {

        private final HashSet<RawBaseImage> jpgSet = new HashSet<RawBaseImage>();

        @Override
        public void onProduct(SimplePicks product) {
            RawBaseImage rawBaseImage = slice.getPngSegments(product);
            jpgSet.add(rawBaseImage);
        }

    }
}
