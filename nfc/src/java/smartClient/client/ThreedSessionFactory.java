package smartClient.client;

import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import com.tms.threed.threedCore.threedModel.shared.ThreedModel;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.NoExport;
import smartClient.client.util.futures.Future;
import smartClient.client.util.futures.OnSuccess;

public class ThreedSessionFactory implements Exportable {

    private BrandKey brandKey;
    private String profileKey;
    private SeriesKey seriesKey;

    public BrandKey getBrandKey() {
        return brandKey;
    }

    @Export
    public void setBrandKey(String brandKey) {
        this.brandKey = BrandKey.fromString(brandKey);
    }

    @NoExport
    public void setBrandKey(BrandKey brandKey) {
        this.brandKey = brandKey;
    }

    @NoExport
    public String getProfileKey() {
        return profileKey;
    }

    @Export
    public void setProfileKey(String profileKey) {
        this.profileKey = profileKey;
    }

    @NoExport
    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    @NoExport
    public void setSeriesKey(SeriesKey seriesKey) {
        this.seriesKey = seriesKey;
    }

    @Export
    public void setSeriesKey(int seriesYear, String seriesName) {
        this.setSeriesKey(new SeriesKey(seriesYear, seriesName));
    }

    @Export
    public Future<ThreedSession> createSession() {

        final ThreedSessionFuture threedSessionFuture = new ThreedSessionFuture();

        final BrandLoader brandLoader = new BrandLoader(brandKey);
        final Future<Brand> brandFuture = brandLoader.ensureLoaded();

        brandFuture.success(new OnSuccess() {

            @Override
            public void call() {
                final Brand brand = brandFuture.getResult();
                final Profile profile = brand.getProfile(profileKey);

                final SeriesLoader seriesLoader = new SeriesLoader(brand.getSeriesId(seriesKey));
                final Future<ThreedModel> seriesFuture = seriesLoader.ensureLoaded();

                seriesFuture.success(new OnSuccess() {
                    @Override
                    public void call() {
                        ThreedSession session = new ThreedSession(seriesFuture.getResult(), profile);
                        threedSessionFuture.setResult(session);
                    }
                });

            }
        });


        return threedSessionFuture;

    }

    @Export
    public static class ThreedSessionFuture extends Future<ThreedSession> implements Exportable {

        public ThreedSessionFuture() {
            super("ThreedSessionFuture");
        }

        @Override
        public ThreedSession getResult() {
            return super.getResult();
        }
    }


}
