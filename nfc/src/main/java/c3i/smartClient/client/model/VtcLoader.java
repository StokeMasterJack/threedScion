package c3i.smartClient.client.model;

import c3i.core.common.shared.SeriesKey;
import c3i.smartClient.client.service.ThreedModelClient;
import c3i.util.shared.futures.AsyncFunction;
import c3i.util.shared.futures.Completer;
import c3i.util.shared.futures.Future;
import c3i.util.shared.futures.Loader;
import c3i.util.shared.futures.OnException;
import c3i.util.shared.futures.OnSuccess;
import java.util.logging.Level;import java.util.logging.Logger;

import javax.annotation.Nonnull;

public class VtcLoader extends Loader<SeriesKey, String> {


    public VtcLoader(SeriesKey input, final ThreedModelClient client) {
        super(input, new AsyncFunction<SeriesKey, String>() {
            @Override
            public void start(SeriesKey arg, final Completer<String> vtcCompleter) throws Exception {
                Future<String> f = client.getVtc(arg);

                f.success(new OnSuccess<String>() {
                    @Override
                    public void onSuccess(@Nonnull String result) {
                        log.log(Level.INFO, "getVtc success: " + result);
                        vtcCompleter.setResult(result);
                    }
                });

                f.failure(new OnException() {
                    @Override
                    public boolean onException(Throwable e) {
                        log.log(Level.INFO, "getBrandInit failed: " + e.toString());
                        log.log(Level.SEVERE, "error", e);
                        vtcCompleter.setException(e);
                        return false;
                    }
                });
            }
        });


        log.log(Level.INFO, "BrandLoader.BrandLoader");
    }

    private static Logger log = Logger.getLogger(VtcLoader.class.getName());
}
