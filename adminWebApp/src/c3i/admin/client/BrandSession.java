package c3i.admin.client;

import c3i.admin.shared.BrandInit;
import c3i.core.common.shared.BrandKey;
import c3i.util.shared.futures.Future;
import c3i.util.shared.futures.Loader;

public class BrandSession {

    private final App app;
    private final BrandKey brandKey;

    private final Loader<BrandKey, BrandInit> loader;

    public BrandSession(final App app, final BrandKey brandKey) {
        this.app = app;
        this.brandKey = brandKey;

        loader = new BrandLoader(brandKey, app.getThreedAdminClient());


    }

    public App getApp() {
        return app;
    }

    public BrandKey getBrandKey() {
        return brandKey;
    }

    public Loader<BrandKey, BrandInit> getLoader() {
        return loader;
    }

    public Future<BrandInit> ensureLoaded() {
        return getLoader().ensureLoaded();
    }


}
