package c3i.featureModel.shared.search;

import c3i.core.common.shared.ProductHandler;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class SearchRequest<V, P, R> {

    ImmutableSet<V> outVars = null;
    ProductHandler<P, R> productHandler;

    public void setOutVars(Set<V> outVars) {
        if (outVars instanceof ImmutableSet) {
            this.outVars = (ImmutableSet<V>) outVars;
        } else {
            this.outVars = ImmutableSet.copyOf(outVars);
        }
    }

    public void setProductHandler(ProductHandler<P, R> productHandler) {
        this.productHandler = productHandler;
    }

    public ImmutableSet<V> getOutVars() {
        return outVars;
    }

    public ProductHandler<P, R> getProductHandler() {
        return productHandler;
    }

    public R getResult() {
        return productHandler.getResult();
    }

}
