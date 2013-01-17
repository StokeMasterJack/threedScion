package c3i.admin.client;

import c3i.admin.shared.BrandInit;
import c3i.core.common.shared.BrandKey;
import c3i.util.shared.futures.AsyncFunction;
import c3i.util.shared.futures.Completer;
import c3i.util.shared.futures.Loader;
import smartsoft.util.gwt.client.rpc.Req;
import smartsoft.util.gwt.client.rpc.SuccessCallback;

public class BrandLoader extends Loader<BrandKey, BrandInit> {

    public BrandLoader(final BrandKey brandKey, final ThreedAdminClient threedAdminClient) {

        super(brandKey, new AsyncFunction<BrandKey, BrandInit>() {
            @Override
            public void start(final BrandKey brandKey, final Completer<BrandInit> completer) throws Exception {
                threedAdminClient.log("Loading " + brandKey + "...");
                Req<BrandInit> request = threedAdminClient.getInitData(brandKey);
                request.onSuccess = new SuccessCallback<BrandInit>() {

                    @Override
                    public void call(Req<BrandInit> r) {
                        threedAdminClient.log("\t Loading " + brandKey + " complete!");
                        completer.setResult(r.result);
                    }

                };
            }
        });
    }


}
