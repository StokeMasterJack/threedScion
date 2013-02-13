package c3i.smartClient.client.model;

import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.threedModel.shared.Brand;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.imageModel.shared.Profile;
import c3i.smartClient.client.service.ThreedModelClient;
import c3i.smartClient.client.service.ThreedModelLoader;
import c3i.util.shared.futures.Completer;
import c3i.util.shared.futures.CompleterImpl;
import c3i.util.shared.futures.Future;
import c3i.util.shared.futures.OnException;
import c3i.util.shared.futures.OnSuccess;
import com.google.common.base.Preconditions;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.NoExport;
import smartsoft.util.shared.Path;

import javax.annotation.Nonnull;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  A <code>ThreedSessionFactory</code> is used to configure and load a <code>ThreedSession </code>. For example:<br/>
 *  <pre>
 *  var factory = new c3i.smartClient.model.ThreedSessionFactory();
 *
 *  factory.setRepoBase("/configurator-content-v2");
 *  factory.setSeries("scion", 2012, "iq");
 *
 *  factory.createSession().success(function (threedSession) {
 *      //use threedSession
 *  });</pre>
 */
@Export
public class ThreedSessionFactory implements Exportable {

    private SeriesKey seriesKey;
    private String profileKey = "wStd";
    private Path repoBaseUrl;

    @Export
    public ThreedSessionFactory() {
        log.log(Level.INFO, "ThreedSessionFactory.ThreedSessionFactory");
    }

    @Export
    public void setProfileKey(String profileKey) {
        log.log(Level.INFO, "profileKey = " + profileKey);
        Preconditions.checkArgument(notEmpty(profileKey));
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

    /**
     * A domain relative path to the 3d repository. The repository must be on the same domain as the consuming application.
     *
     * Currently this value is typically set to <code>/configurator-content-v2</code>
     *
     */
    @Export
    public void setRepoBase(String repoBaseUrlString) {
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

    /**
     * Example: <pre>
     *     threedSessionFactory.setSeries("scion",2012,"iq");
     * </pre>
     */
    @Export
    public void setSeries(String brandKey, int seriesYear, String seriesName) {
        try {
            SeriesKey sk = new SeriesKey(brandKey, seriesYear + "", seriesName);
            this.setSeriesKey(sk);
        } catch (IllegalArgumentException e) {
            log.log(Level.SEVERE, "Problem calling setSeries", e);
            throw e;
        }
    }

    /**
     * Loads currently vtc'd version threed model from repository and creates ThreedSession object.
     * The new threedSession is accessed by passing a callback function to the returned future's success method.
     *
     * Example:<pre>
     *  factory.createSession().success(function (threedSession) {
     *      //use threedSession
     *  });</pre>
     */
    @Export
    public ThreedSessionFuture createSession() {
        log.log(Level.INFO, "ThreedSessionFactory.createSession start");

        log.log(Level.INFO, "seriesKey = " + seriesKey);
        log.log(Level.INFO, "profileKey = " + profileKey);
        log.log(Level.INFO, "repoBaseUrl = " + repoBaseUrl);

        try {
            if (seriesKey == null)
                throw new IllegalStateException("seriesKey must be set before calling createSession");
            if (isEmpty(profileKey))
                throw new IllegalStateException("profileKey must be set before calling createSession");
            if (repoBaseUrl == null)
                throw new IllegalStateException("repoBaseUrl must be set before calling createSession");


            final ThreedModelClient threedModelClient = new ThreedModelClient(repoBaseUrl);

            final Completer<ThreedSession> threedSessionCompleter = new CompleterImpl<ThreedSession>();

            final BrandLoader brandLoader = new BrandLoader(seriesKey.getBrandKey(), threedModelClient);

            final Future<Brand> brandFuture = brandLoader.ensureLoaded();

            brandFuture.success(new OnSuccess<Brand>() {
                @Override
                public void onSuccess(@Nonnull Brand brand) {
                    final Profile profile = brand.getProfile(profileKey);
                    SeriesId seriesId = brand.getSeriesId(seriesKey);
                    final ThreedModelLoader seriesLoader = new ThreedModelLoader(threedModelClient, seriesId);
                    final Future<ThreedModel> seriesFuture = seriesLoader.ensureLoaded();
                    seriesFuture.success(new OnSuccess<ThreedModel>() {
                        @Override
                        public void onSuccess(@Nonnull ThreedModel threedModel) {
                            ThreedSession session = new ThreedSession(repoBaseUrl, threedModel, profile);
                            threedSessionCompleter.setResult(session);
                        }
                    });

                }
            });

            brandFuture.failure(new OnException() {
                @Override
                public boolean onException(Throwable e) {
                    e.printStackTrace();
                    return false;
                }
            });


            Future<ThreedSession> future = threedSessionCompleter.getFuture();

//            return new ForwardingFuture<ThreedSession>(future);
            return new ThreedSessionFuture(future);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Problem in createSession", e);
            throw new RuntimeException(e);
        }

    }


    @NoExport
    public static boolean isEmpty(String s) {
        if (s == null || s.trim().equals("") || s.trim().equalsIgnoreCase("null")) {
            return true;
        } else {
            return false;
        }
    }

    @NoExport
    public static boolean notEmpty(String s) {
        return !isBlank(s);
    }

    @NoExport
    public static boolean isBlank(String s) {
        if (s == null || s.trim().equals("") || s.trim().equalsIgnoreCase("null")) {
            return true;
        } else {
            return false;
        }
    }

    private static Logger log = Logger.getLogger(ThreedSessionFactory.class.getName());
}
