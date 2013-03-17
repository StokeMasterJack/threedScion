package c3i.featureModel.shared.search;

import c3i.featureModel.shared.node.Csp;

public abstract class Search {

    public abstract void onNode(int level, Csp csp) throws StopSearchException;


}
