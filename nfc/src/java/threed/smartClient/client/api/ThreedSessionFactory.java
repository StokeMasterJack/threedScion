package threed.smartClient.client.api;

import threed.core.threedModel.shared.SeriesId;
import threed.core.threedModel.shared.SeriesKey;
import threed.core.threedModel.shared.ThreedModel;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.NoExport;
import threed.smartClient.client.util.futures.Future;
import threed.smartClient.client.util.futures.OnSuccess;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.lang.shared.Path;

public class ThreedSessionFactory implements Exportable {

    private SeriesKey seriesKey;
    private String profileKey;
    private Path repoBaseUrl;

    @NoExport
    public String getProfileKey() {
        return profileKey;
    }

    @Export
    public void setProfileKey(String profileKey) {
        this.profileKey = profileKey;
    }

    @NoExport
    public Path getRepoBaseUrl() {
        return repoBaseUrl;
    }

    @NoExport
    public void setRepoBaseUrl(Path repoBaseUrl) {
        this.repoBaseUrl = repoBaseUrl;
    }

    @Export
    public void setRepoBaseUrlString(String repoBaseUrlString) {
        this.setRepoBaseUrl(new Path(repoBaseUrlString));
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
    public void setSeries(String brandKey, int seriesYear, String seriesName) {
        try {
            SeriesKey sk = new SeriesKey(brandKey, seriesYear + "", seriesName);
            this.setSeriesKey(sk);
        } catch (IllegalArgumentException e) {
            Console.error("Problem calling setSeries", e);
            throw e;
        }
    }

    @Export
    public ThreedSessionFuture createSession() {

        try {
            if (seriesKey == null)
                throw new IllegalStateException("seriesKey must be set before calling createSession");
            if (profileKey == null)
                throw new IllegalStateException("profileKey must be set before calling createSession");
            if (repoBaseUrl == null)
                throw new IllegalStateException("repoBaseUrl must be set before calling createSession");


            final ThreedSessionFuture threedSessionFuture = new ThreedSessionFuture();

            final BrandLoader brandLoader = new BrandLoader(seriesKey.getBrandKey(), repoBaseUrl);
            final Future<Brand> brandFuture = brandLoader.ensureLoaded();

            brandFuture.success(new OnSuccess() {

                @Override
                public void call() {
                    final Brand brand = brandFuture.getResult();
                    final Profile profile = brand.getProfile(profileKey);

                    SeriesId seriesId = brand.getSeriesId(seriesKey);
                    final SeriesLoader seriesLoader = new SeriesLoader( seriesId, repoBaseUrl);
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
        } catch (Exception e) {
            Console.error("Problem in createSession", e);
            throw new RuntimeException(e);
        }

    }


}
