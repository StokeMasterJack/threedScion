package c3i.imgGen.search;


import c3i.imgGen.external.ProductConverter;
import c3i.core.common.shared.ProductHandler;

public abstract class Node<
        PRODUCT_TYPE,
        VAR_TYPE,
        SEARCH_TYPE extends Search<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE>,
        NODE_TYPE extends Node<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE>

        > {

    protected final SEARCH_TYPE search;
    protected final Level<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE> level;

    private Level<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE> nextLevel;

    protected Node(SEARCH_TYPE search, Level level) {
        this.search = search;
        this.level = level;
    }

    public NODE_TYPE self() {
        return (NODE_TYPE) this;
    }

    public SEARCH_TYPE getSearch() {
        return search;
    }

    public abstract boolean isTrue();

    public abstract boolean isFalse();

    public boolean isOpen() {
        return !isTrue() && !isFalse();
    }

    public abstract boolean isTrue(VAR_TYPE var);

    public abstract boolean isFalse(VAR_TYPE var);

    public boolean isOpen(VAR_TYPE var) {
        return !isTrue(var) && !isFalse(var);
    }

    /**
     *
     * Assigned enough
     *
     * if there are no out vars, then this is the same as isComplete
     * else it means, all out vars are assigned.
     */
    public abstract boolean isOutComplete();


    public abstract boolean isDistributable();

    /**
     * Fully assigned
     * Are all vars (out and non-out) are assigned
     */
    public abstract boolean isAllComplete();

    public abstract VarSet<VAR_TYPE> getCareVars();

    public abstract VarSet<VAR_TYPE> getDontCares();

    public abstract VarSet<VAR_TYPE> getTrueVars();

    public abstract VarSet<VAR_TYPE> getFalseVars();

    public VarSet<VAR_TYPE> getAssignedVars() {
        return getTrueVars().union(getFalseVars());
    }

    public abstract Decisions<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE> decide();

    public abstract int getVarScore(VAR_TYPE var);

    public VAR_TYPE getBestOpenOutVar() {

        int bestScore = -1;
        VAR_TYPE bestVar = null;

        Iterable<VAR_TYPE> outVars = getSearch().getOutVars();

        for (VAR_TYPE outVar : outVars) {
            int varScore = getVarScore(outVar);
            if (varScore > bestScore) {
                bestScore = varScore;
                bestVar = outVar;
            }
        }
        return null;
    }


    public Level<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE> getNextLevel() {
        if (nextLevel == null) {
            VAR_TYPE var = getBestOpenOutVar();
            nextLevel = new Level<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE>(self(), var);
        }
        return nextLevel;
    }

    public abstract int getOutDontCareCount();

    public abstract boolean isSat();

    public abstract NODE_TYPE refine(VAR_TYPE var, boolean value);

    public abstract NODE_TYPE assignTrue(VAR_TYPE var);

    public abstract NODE_TYPE assignFalse(VAR_TYPE var);

    public abstract NODE_TYPE assign(VAR_TYPE var, boolean value);

    public void doSearch() throws StopOnTrueException {
        if (isFalse()) {
            return;
        }

        if (isTrue() && search.stopOnTrue()) {
            throw new StopOnTrueException();
        }

        Level<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE> nextLevel = getNextLevel();


        //handle product count
        if (nextLevel == null) {
            //node is out complete

            if (isTrue()) {
                search.incrementProductCount();
                onProduct();
            } else if (isOpen() && isSat()) {
                search.incrementProductCount();
                onProduct();
            }
        } else {
            if (isTrue()) {
                //all open vars are dontCares
                int dcCount = getOutDontCareCount();
                long delta = Search.twoToThePowerOf(dcCount);
                search.incrementProductCount(delta);

                onProduct();

            } else if (isOpen()) {
                nextLevel.process();
            }
        }


        //then handle allSat


    }

    protected void onProduct() {
        ProductHandler<PRODUCT_TYPE> productHandler = getSearch().getProductHandler();
        if (productHandler != null) {
            ProductConverter<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE> productConverter = getSearch().getProductConverter();
            productConverter.onProduct(self(), productHandler);
        }
    }

    public abstract FailureInfo getFailureInfo();
}
