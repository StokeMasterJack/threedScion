package c3i.smartClient.client.model;

import c3i.core.common.shared.BrandKey;
import c3i.core.threedModel.shared.Brand;
import c3i.smartClient.client.ThreedConstants;
import c3i.util.shared.futures.AsyncFunction;

public interface BrandLoaderFunction extends AsyncFunction<BrandKey, Brand>, ThreedConstants {


}
