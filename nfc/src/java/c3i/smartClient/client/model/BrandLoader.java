package c3i.smartClient.client.model;

import c3i.core.common.shared.BrandKey;
import c3i.core.threedModel.shared.Brand;
import c3i.smartClient.client.service.ThreedModelClient;
import c3i.util.shared.futures.AsyncFunction;
import c3i.util.shared.futures.Completer;
import c3i.util.shared.futures.Future;
import c3i.util.shared.futures.Loader;
import c3i.util.shared.futures.OnException;
import c3i.util.shared.futures.OnSuccess;
import java.util.logging.Level;import java.util.logging.Logger;

import javax.annotation.Nonnull;

public class BrandLoader extends Loader<BrandKey,Brand> {


    public BrandLoader(BrandKey input, final ThreedModelClient client) {
        super(input, new AsyncFunction<BrandKey, Brand>() {
            @Override
            public void start(BrandKey input, final Completer<Brand> brandCompleter) throws Exception {
                log.log(Level.INFO, "BrandLoader.start");
                Future<Brand> f = client.getBrandInit(input);

                f.success(new OnSuccess<Brand>() {
                    @Override
                    public void onSuccess(@Nonnull Brand result) {
                        log.log(Level.INFO, "getBrandInit success: " + result.getBrandKey());
                        brandCompleter.setResult(result);
                    }
                });

                f.failure(new OnException() {
                    @Override
                    public boolean onException(Throwable e) {
                        log.log(Level.INFO, "getBrandInit failed: " + e.toString());
                        log.log(Level.SEVERE, "error", e);
                        brandCompleter.setException(e);
                        return false;
                    }
                });
            }
        });


        log.log(Level.INFO, "BrandLoader.BrandLoader");
    }

    private static Logger log = Logger.getLogger(BrandLoader.class.getName());
}
