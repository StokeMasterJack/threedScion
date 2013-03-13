package c3i.featureModel.shared.search;

import c3i.core.common.shared.ProductHandler;
import c3i.featureModel.shared.CspForTreeSearch;

public class SatCountProductHandler implements ProductHandler<CspForTreeSearch, Long> {

    private long satCount;

    @Override
    public void onProduct(CspForTreeSearch csp) {
        satCount++;
    }

    public long getSatCount() {
        return satCount;
    }

    public long getCount() {
        return satCount;
    }

    @Override
    public Long getResult() {
        return satCount;
    }
}
