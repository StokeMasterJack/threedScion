package c3i.featureModel.shared.search;

import c3i.featureModel.shared.node.Csp;

public abstract class OutSearch {

    protected Csp contextCsp;

    public void start(Csp contextNode) {
        this.start(0, contextNode);
    }

    public void start(int level, Csp contextNode) {
        this.contextCsp = contextNode;
        onNode(level, contextNode);
    }

    public abstract void onNode(int level, Csp csp);


}
