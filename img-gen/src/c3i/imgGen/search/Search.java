package c3i.imgGen.search;


import c3i.core.common.shared.ProductHandler;
import c3i.imgGen.external.ProductConverter;
import c3i.imgGen.external.SpaceContext;

import static com.google.common.base.Preconditions.checkState;

public abstract class Search<
        PRODUCT_TYPE,
        VAR_TYPE,
        SEARCH_TYPE extends Search<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE>,
        NODE_TYPE extends Node<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE>
        > {

    public static enum RunState {
        SEARCH_NOT_STARTED,
        SEARCH_RUNNING,
        SEARCH_COMPLETE
    }

    private final SpaceContext<VAR_TYPE> spaceContext;

    private Boolean sat;
    private long satCount;

    private Class<PRODUCT_TYPE> productHandlerClass;
    private ProductConverter<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE> productConverter;

    private ProductHandler<PRODUCT_TYPE> productHandler;

    private RunState runState = RunState.SEARCH_NOT_STARTED;

    protected Search(SpaceContext<VAR_TYPE> spaceContext, ProductHandler<PRODUCT_TYPE> productHandler) {
        this.spaceContext = spaceContext;
    }

    public SEARCH_TYPE self() {
        return (SEARCH_TYPE) this;
    }

    public void start(NODE_TYPE node) {
        runState = RunState.SEARCH_RUNNING;

        try {
            node.doSearch();
            if (sat == null) {
                sat = false;
            }
        } catch (StopOnTrueException e) {
            checkState(sat != null);
            sat = true;
        }

        runState = RunState.SEARCH_COMPLETE;
    }


    protected abstract void onProduct(NODE_TYPE node);


    public boolean isSearchComplete() {
        return runState == RunState.SEARCH_COMPLETE;
    }

    public boolean isSearchNotStarted() {
        return runState == RunState.SEARCH_NOT_STARTED;
    }

    public boolean isSearchRunning() {
        return runState == RunState.SEARCH_RUNNING;
    }

    public boolean isSat() {
        checkState(runState == RunState.SEARCH_COMPLETE);
        checkState(sat != null);
        return sat;
    }

    public RunState getRunState() {
        return runState;
    }

    public void incrementProductCount() {
        satCount++;
    }

    public void incrementProductCount(long delta) {
        satCount += delta;
    }

    public static long twoToThePowerOf(int power) {
        return (long) Math.pow(2, power);
    }

    public abstract VarSet<VAR_TYPE> getOutVars();

    public abstract boolean isOutVar(VAR_TYPE var);


    public abstract boolean stopOnTrue();

    public abstract boolean isCountOnly();

    public abstract VAR_TYPE resolveVar(String varCode);

    public abstract ProductHandler<PRODUCT_TYPE> getProductHandler();

    public abstract ProductConverter<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE> getProductConverter();
}
