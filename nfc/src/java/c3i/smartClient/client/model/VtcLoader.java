package c3i.smartClient.client.model;

import c3i.core.common.shared.SeriesKey;
import c3i.smartClient.client.service.ThreedModelClient;
import c3i.util.shared.futures.AsyncFunction;
import c3i.util.shared.futures.Completer;
import c3i.util.shared.futures.Future;
import c3i.util.shared.futures.Loader;
import c3i.util.shared.futures.OnException;
import c3i.util.shared.futures.OnSuccess;
import smartsoft.util.gwt.client.Console;

import javax.annotation.Nonnull;

public class VtcLoader extends Loader<SeriesKey, String> {


    public VtcLoader(SeriesKey input, final ThreedModelClient client) {
        super(input, new AsyncFunction<SeriesKey, String>() {
            @Override
            public void start(SeriesKey input, final Completer<String> vtcCompleter) throws Exception {
                Future<String> f = client.getVtc(input);

                f.success(new OnSuccess<String>() {
                    @Override
                    public void onSuccess(@Nonnull String result) {
                        Console.log("getVtc success: " + result);
                        vtcCompleter.setResult(result);
                    }
                });

                f.failure(new OnException() {
                    @Override
                    public boolean onException(Throwable e) {
                        Console.log("getBrandInit failed: " + e.toString());
                        Console.error(e);
                        vtcCompleter.setException(e);
                        return false;
                    }
                });
            }
        });


        Console.log("BrandLoader.BrandLoader");
    }
}
