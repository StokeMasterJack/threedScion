package c3i.imgGen.search;

public interface Decisions<
        PRODUCT_TYPE,
        VAR_TYPE,
        SEARCH_TYPE extends Search<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE>,
        NODE_TYPE extends Node<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE>

        > {

    int size();

    Decision<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE> getDecision(int i);
}
