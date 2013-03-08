package c3i.imgGen;

import c3i.core.featureModel.shared.CspForTreeSearch;
import c3i.core.featureModel.shared.search.ProductHandler;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.RawBaseImage;
import c3i.imageModel.shared.SimplePicks;
import c3i.imgGen.external.ImgGenContext;
import c3i.imgGen.external.ProductHandlerSimple;
import c3i.imgGen.server.JpgSet;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

public class JpgSetTask {

    public static enum State {NOT_STARTED, IN_PROCESS, COMPLETE}

    //core state
    private final ImgGenContext ctx;
    private final ImView view;
    private final int angle;

    private final MyProductHandler productHandler;

    //computed
    private final Set<Object> outVars;

    //compute onProduct
    private State state;

    public JpgSetTask(ImgGenContext ctx, ImView view, int angle) {
        this.ctx = ctx;
        this.view = view;
        this.angle = angle;

        this.outVars = view.getPngVars();
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

    public ImView getView() {
        return view;
    }

    public int getAngle() {
        return angle;
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

    public JpgSet.JpgSetKey getJpgSetKey() {
        return new JpgSet.JpgSetKey(ctx.getSeriesId(), view.getName(), angle);
    }

    private class MyProductHandler implements ProductHandlerSimple, ProductHandler {

        private final HashSet<RawBaseImage> jpgSet = new HashSet<RawBaseImage>();

        @Override
        public void onProduct(SimplePicks product) {
            RawBaseImage rawBaseImage = view.getPngSegments(product, angle);
            jpgSet.add(rawBaseImage);
        }

        @Override
        public void onProduct(CspForTreeSearch csp) {
            this.onProduct(csp.getAssignments());
        }

    }
}
