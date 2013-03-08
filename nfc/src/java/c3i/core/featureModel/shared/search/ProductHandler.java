package c3i.core.featureModel.shared.search;

import c3i.core.featureModel.shared.CspForTreeSearch;

public interface ProductHandler {
    /**
     * where csp.isTrue and csp.isOutComplete
     */
    void onProduct(CspForTreeSearch csp);
}
