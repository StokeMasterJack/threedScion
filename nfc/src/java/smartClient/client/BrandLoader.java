package smartClient.client;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.tms.threed.threedCore.threedModel.client.ThreedModelClient;
import com.tms.threed.threedCore.threedModel.shared.VtcMap;
import smartsoft.util.gwt.client.Console;

import javax.annotation.Nonnull;

public class BrandLoader extends Loader<Brand> {

    private final ThreedModelClient client;

    private final BrandKey brandKey;

    public BrandLoader(BrandKey brandKey) {
        super("BrandLoader");
        Preconditions.checkNotNull(brandKey);
        this.client = ThreedModelClient.create();
        this.brandKey = brandKey;
        send();
    }

    protected void send() {
        assert brandKey != null;

        final Future<VtcMap> future = client.getVtcMap();

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

    @Override
    public BrandFuture createFuture(String name) {
        return new BrandFuture();
    }

    @Nonnull
    @Override
    public BrandFuture ensureLoaded() {
        return (BrandFuture) super.ensureLoaded();
    }

}
