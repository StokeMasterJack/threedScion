package c3i.smartClient.client.model;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.imageModel.shared.Profile;
import c3i.core.threedModel.shared.Brand;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.smartClient.client.ThreedConstants;
import c3i.smartClient.client.service.ThreedModelClient;
import c3i.smartClient.client.service.ThreedModelLoader;
import c3i.smartClient.client.settings.Arg;
import c3i.smartClient.client.settings.DefaultFunction;
import c3i.util.shared.futures.Completer;
import c3i.util.shared.futures.CompleterImpl;
import c3i.util.shared.futures.Future;
import c3i.util.shared.futures.OnException;
import c3i.util.shared.futures.OnSuccess;
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
public class ThreedSessionFactory implements Exportable, ThreedConstants {


    private static Logger log = Logger.getLogger(ThreedSessionFactory.class.getName());

    private final SessionArgs args = new SessionArgs();

    @Export
    public ThreedSessionFactory() {
        log.log(Level.INFO, "ThreedSessionFactory.ThreedSessionFactory");
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


    @Export
    public void setProfileKey(String profileKey) {
        args.profileKey.setClientValue(profileKey);
        args.profileKey.checkNotEmpty();
    }


    /**
     * A domain relative path to the 3d repository. The repository must be on the same domain as the consuming application.
     *
     * Currently this value is typically set to <code>/configurator-content-v2</code>
     *
     */
    @Export
    public void setRepoBase(String newValue) {
        setThreedModelBaseUrl(newValue);
    }

    @Export
    public void setThreedModelBaseUrl(String newValue) {
        args.fmBaseUrl.setClientValue(new Path(newValue));
    }

    @Export
    public void setVtcBaseUrl(String newValue) {
        args.vtcBaseUrl.setClientValue(new Path(newValue));
    }

    @Export
    public void setImageBaseUrl(String newValue) {
        args.imgBaseUrl.setClientValue(new Path(newValue));
    }

    @NoExport
    public SeriesKey getSeriesKey() {
        args.seriesKey.checkNotNull();
        return args.seriesKey.getEffectiveValue();
    }

    @NoExport
    public void setSeriesKey(SeriesKey seriesKey) {
        args.seriesKey.setClientValue(seriesKey);
    }

    /**
     * Example: <pre>
     *     threedSessionFactory.setSeries("scion",2012,"iq");
     * </pre>
     */
    @Export
    public void setSeries(String brandKey, int seriesYear, String seriesName) {
        SeriesKey sk = new SeriesKey(BrandKey.fromString(brandKey), seriesYear, seriesName);
        setSeriesKey(sk);
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

        args.log(log);


        try {

            args.check();

            final BrandLoaderFactory brandLoaderFactory = new BrandLoaderFactoryXhr(args.brandKey.get(), args.vtcBaseUrl.get());
            final BrandLoader brandLoader = brandLoaderFactory.createLoader();
            final Future<Brand> brandFuture = brandLoader.ensureLoaded();

            final Completer<ThreedSession> threedSessionCompleter = new CompleterImpl<ThreedSession>();

            brandFuture.success(new OnSuccess<Brand>() {
                @Override
                public void onSuccess(@Nonnull final Brand brand) {

                    final Profile profile = brand.getProfile(args.profileKey.get());
                    SeriesId seriesId = brand.getSeriesId(args.seriesKey.get());

                    ThreedModelClient threedModelClient = new ThreedModelClient(args.fmBaseUrl.get());

                    final ThreedModelLoader seriesLoader = new ThreedModelLoader(threedModelClient, seriesId);
                    final Future<ThreedModel> seriesFuture = seriesLoader.ensureLoaded();

                    seriesFuture.success(new OnSuccess<ThreedModel>() {
                        @Override
                        public void onSuccess(@Nonnull ThreedModel threedModel) {
                            Path imgBaseUrl = computeImageBaseUrl(args, brand);
                            System.err.println("computedImageBaseUrl[" + imgBaseUrl + "]");
                            ThreedSession session = new ThreedSession(imgBaseUrl, threedModel, profile);
                            threedSessionCompleter.setResult(session);
                        }
                    });
                }
            });

            brandFuture.failure(new OnException() {
                @Override
                public boolean onException(Throwable e) {
                    e.printStackTrace();
                    threedSessionCompleter.setException(e);
                    return false;
                }
            });


            Future<ThreedSession> future = threedSessionCompleter.getFuture();
            return new ThreedSessionFuture(future);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Problem in createSession", e);
            throw new RuntimeException(e);
        }

    }


    public static Path computeImageBaseUrl(SessionArgs sessionArgs, Brand brand) {
        log.info("computeImageBaseUrl for [" + brand.getBrandKey() + "]");
        log.info("First check sessionArgs.imgBaseUrl");
        Path p = sessionArgs.imgBaseUrl.get();

        //first check client args
        if (p != null) {
            log.info("Using imgBaseUrl from sessionArgs[" + p + "]");
            return p;
        } else {
            log.info("sessionArgs.imgBaseUrl was null - next we look for a value in vtcMap.config.imgBaseUrl");
            //fallback to vtcMap config params
            p = brand.getImageRepoBaseUrl();
            if (p != null) {
                log.info("Using imgBaseUrl from vtcMap.config.imgBaseUrl[" + p + "]");
                return p;
            } else {
                p = ThreedConstants.BASE_URL_IMG;
                log.info("vtcMap.config.imgBaseUrl was null - next we fallback to [" + p + "]");
                //finally, use the default
                log.info("Using imgBaseUrl from ThreedConstants.BASE_URL_IMG [" + p + "]");
                return p;
            }
        }
    }


    public BrandKey getBrandKey() {
        args.seriesKey.checkNotNull();
        return getSeriesKey().getBrandKey();
    }


    public class SessionArgs implements ThreedConstants {

        public final Arg<String> profileKey = Arg.create("profile", "wStd");
        public final Arg<SeriesKey> seriesKey = Arg.create("seriesKey");

        public final Arg<Boolean> jsonp = Arg.create("jsonp");

        public final Arg<Path> vtcBaseUrl = Arg.create("vtcBaseUrl", BASE_URL_VTC);

        public final Arg<Path> fmBaseUrl = Arg.create("fmBaseUrl", BASE_URL_FM);

        public final Arg<Path> imgBaseUrl = Arg.create("imgBaseUrl");

        public final Arg<BrandKey> brandKey = Arg.create("brandKey", new DefaultFunction<BrandKey>() {
            @Override
            public BrandKey getDefaultValue(Arg<BrandKey> arg) {
                seriesKey.checkNotEmpty();
                return seriesKey.get().getBrandKey();
            }
        });


        public void log(Logger log) {
            log.info("ThreedSessionFactory args:");
            profileKey.log("\tprofileKey", log);
            seriesKey.log("\tseriesKey", log);
            vtcBaseUrl.log("\tvtcBaseUrl", log);
            fmBaseUrl.log("\tfmBaseUrl", log);
            imgBaseUrl.log("\timgBaseUrl", log);
            jsonp.log("\tjsonp", log);
        }

        public void check() {
            seriesKey.checkNotNull("seriesKey must be set before calling createSession");
        }

    }
}
