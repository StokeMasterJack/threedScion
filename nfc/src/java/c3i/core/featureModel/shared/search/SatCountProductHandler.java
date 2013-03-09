package c3i.core.featureModel.shared.search;

import c3i.core.common.shared.ProductHandler;
import c3i.core.featureModel.shared.CspForTreeSearch;

public class SatCountProductHandler implements ProductHandler<CspForTreeSearch> {

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
}
