package c3i.featureModel.shared.search;

import c3i.featureModel.shared.node.Csp;

public class StopSearchException extends RuntimeException {

    private final Csp firstTrueNode;

    public StopSearchException(Csp firstTrueNode) {
        this.firstTrueNode = firstTrueNode;
    }

    public Csp getFirstTrueNode() {
        return firstTrueNode;
    }
}
