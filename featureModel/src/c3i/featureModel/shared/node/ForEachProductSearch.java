package c3i.featureModel.shared.node;

import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.search.ProductHandler;
import c3i.featureModel.shared.search.Search;

/**
 * This is a trailing (as opposed to copy-on-branch) search node for
 * searching the bottom-most (or "dontCare only) portion of the search space.
 *
 * The top portion of the path is represented by the parent csp (which is a copy-on-branch csp).
 *
 * So the complete path is (parent,dontCare
 *
 * One instance of LiteNode serves as the Node for the entire duration of traversing the dontCare space
 */
public class ForEachProductSearch extends Search {

    private ProductHandler productHandler;
    private long productCount;

    public void setProductHandler(ProductHandler productHandler) {
        this.productHandler = productHandler;
    }

    @Override
    public void onNode(int level, Csp csp) {

        if (csp.isProduct()) {
            if (productHandler != null) {
                productHandler.onProduct(csp);
            }
            productCount++;
        } else {
            Var var = csp.decide();
            csp.assignTrue(var);
            onNode(level + 1, csp); //pos


            csp.pop(var);
//            csp.assignFalse(var);
            onNode(level + 1, csp); //neg
        }

    }

    public long getProductCount() {
        return productCount;
    }
}
