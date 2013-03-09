package c3i.imgGen.search;

import static com.google.common.base.Preconditions.checkState;

public class Level<
        PRODUCT_TYPE,
        VAR_TYPE,
        SEARCH_TYPE extends Search<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE>,
        NODE_TYPE extends Node<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE>

        > {

    private final NODE_TYPE parent;
    private final VAR_TYPE decisionVar;

    private NODE_TYPE pos;
    private NODE_TYPE neg;

    public Level(NODE_TYPE parent, VAR_TYPE decisionVar) {
        this.parent = parent;
        this.decisionVar = decisionVar;
    }

    private void processPos() {
        checkState(pos == null);
        process(true);
    }

    private void processNeg() {
        checkState(neg == null);
        process(false);
    }

    private void process(boolean phase) {
        //prindent(depth, "Decision: " + var + " " + phase);

        //prindent(depth, "\t before assign \t[" + parent + "]");

        if (phase) {
            pos = parent.assign(decisionVar, phase);
        } else {
            neg = parent.assign(decisionVar, phase);
        }

        //prindent(depth, "\t after assign \t[" + parent + "]");

    }

    public VAR_TYPE getDecisionVar() {
        return decisionVar;
    }

    public void process() {

        //prindent2(depth, "Decisions: " + decisions);
        checkState(pos == null);
        checkState(neg == null);
        processPos();
        processNeg();
    }

}
