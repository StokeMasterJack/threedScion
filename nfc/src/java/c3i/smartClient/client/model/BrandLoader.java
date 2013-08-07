package c3i.smartClient.client.model;

import c3i.core.common.shared.BrandKey;
import c3i.core.threedModel.shared.Brand;
import c3i.util.shared.futures.AsyncFunction;
import c3i.util.shared.futures.Loader;

public class BrandLoader extends Loader<BrandKey, Brand> {

    public BrandLoader(BrandKey input, AsyncFunction<BrandKey, Brand> asyncFunction) {
        super(input, asyncFunction);
    }
}
