package c3i.imgGen.external;

import c3i.core.common.shared.ProductHandler;
import c3i.imgGen.search.Node;
import c3i.imgGen.search.Search;

public abstract class ProductConverter<
        PRODUCT_TYPE,
        VAR_TYPE,
        SEARCH_TYPE extends Search<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE>,
        NODE_TYPE extends Node<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE>

        > {

    protected Search<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE> search;

    protected ProductConverter(Search<PRODUCT_TYPE, VAR_TYPE, SEARCH_TYPE, NODE_TYPE> search) {
        this.search = search;
    }

    public abstract void onProduct(NODE_TYPE in, ProductHandler<PRODUCT_TYPE,Void> out);

}
