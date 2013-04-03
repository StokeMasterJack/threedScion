package c3i.featureModel.shared.search;

import c3i.featureModel.shared.common.SimplePicks;

public class CountingProductHandler implements ProductHandler {

    private long count;

    public CountingProductHandler() {
    }

    @Override
    public void onProduct(SimplePicks product) {
//        System.out.println(product);
        count++;
    }

    public long getCount() {
        return count;
    }

}
