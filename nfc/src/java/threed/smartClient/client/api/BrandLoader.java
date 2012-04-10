package threed.smartClient.client.api;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import threed.core.threedModel.shared.BrandKey;
import threed.core.threedModel.shared.VtcMap;
import threed.smartClient.client.util.futures.Future;
import threed.smartClient.client.util.futures.Loader;
import threed.smartClient.client.util.futures.OnFailure;
import threed.smartClient.client.util.futures.OnSuccess;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.gwt.client.rpc.UiLog;
import smartsoft.util.lang.shared.Path;

public class BrandLoader extends Loader<Brand> {

    private final ThreedModelClient client;
    private final BrandKey brandKey;

    public BrandLoader(BrandKey brandKey, Path repoBaseUrl) {
        super("BrandLoader");
        Preconditions.checkNotNull(brandKey);
        Preconditions.checkNotNull(repoBaseUrl);
        this.client = new ThreedModelClient(UiLog.DEFAULT, repoBaseUrl);
        this.brandKey = brandKey;
        send();
    }


    protected void send() {
        assert brandKey != null;

        final Future<VtcMap> future = client.getVtcMap(brandKey);

        future.success(new OnSuccess() {
            @Override
            public void call() {
                ImmutableList<Profile> profiles = ImmutableList.of();
                Brand brand = new Brand(brandKey, future.result, profiles);
                try {
                    setResult(brand);
                } catch (Exception e) {
                    Console.log(e.toString());
                    e.printStackTrace();
                }
            }
        });

        future.failure(new OnFailure() {
            @Override
            public void call() {
                setException(future.exception);
            }
        });

    }

//    @Override
//    public BrandFuture createFuture(String name) {
//        return new BrandFuture();
//    }
//
//    @Nonnull
//    @Override
//    public BrandFuture ensureLoaded() {
//        return (BrandFuture) super.ensureLoaded();
//    }

}
