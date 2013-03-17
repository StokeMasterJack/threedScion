package c3i.imgGen.server;

import c3i.featureModel.shared.common.BrandKey;
import c3i.featureModel.shared.common.RootTreeId;
import c3i.featureModel.shared.common.SeriesId;
import c3i.featureModel.shared.common.SeriesKey;
import c3i.imageModel.shared.JpgWidth;
import c3i.imageModel.shared.Profile;
import c3i.imgGen.ImgGenApp;
import c3i.imgGen.server.taskManager.JpgGeneratorService;
import c3i.imgGen.server.taskManager.Master;
import c3i.imgGen.shared.JobSpec;
import c3i.imgGen.shared.JobState;
import c3i.imgGen.shared.JobStatus;
import c3i.repo.ReposConfig;
import c3i.repo.server.BrandRepo;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.SeriesRepo;
import com.google.common.collect.ImmutableMap;
import smartsoft.util.Date;
import smartsoft.util.Sys;
import smartsoft.util.args.CommandLineArgs;
import smartsoft.util.args.FieldDefaulter;
import smartsoft.util.args.Schema;

import javax.annotation.Nonnull;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.ImageCapabilities;
import java.io.File;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class JpgGenCommandLine {

    private static final NumberFormat PERCENT_FORMAT = NumberFormat.getPercentInstance();

    private final Args args;

    public JpgGenCommandLine(String... a) {
        this(new Args(a));
    }

    public JpgGenCommandLine(Args args) {
        this.args = args;

        System.out.println("Using options:");
        args.printMerged(1);
    }

    public static void main(String... args) {
        JpgGenCommandLine jpgGen = new JpgGenCommandLine(args);
        jpgGen.start();
    }

    public void start() {

        BrandRepo brandRepo = new BrandRepo(args.getBrand(), args.getRepoBase());

        BrandRepos brandRepos = BrandRepos.createSingleBrand(args.getBrand(), args.getRepoBase());

        SeriesRepo seriesRepo = brandRepo.getSeriesRepo(args.getSeriesKey());
        RootTreeId rootTreeId = seriesRepo.getSrcRepo().resolveRootTreeId(args.getRev());
        SeriesId seriesId = new SeriesId(args.getSeriesKey(), rootTreeId);
        JobSpec jobSpec = new JobSpec(seriesId, args.getProfile());

        ImgGenApp imgGenApp = new ImgGenApp();

        final JpgGeneratorService jpgGen = imgGenApp.getJpgGeneratorService();

        final Master job = jpgGen.startNewJpgJob(jobSpec, args.getThreadCount(), Thread.NORM_PRIORITY);
        final Timer timer = new Timer();

        final long t1 = System.currentTimeMillis();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                final JobStatus status = job.getStatus();
                JobState state = status.getState();

                final long t2 = System.currentTimeMillis();
                final long elapsed = (t2 - t1) / 1000L;

                switch (state) {
                    case Canceled:
                        System.out.println("Job canceled");
                        timer.cancel();
                        break;
                    case Error:
                        System.err.println("Job stopped due to error. See stack trace");
//                        System.out.println(status.getSerializedStackTrace());
                        timer.cancel();
                        break;
                    case Complete:
                        System.out.println("Job complete!!! Total time: " + elapsed);
                        timer.cancel();
                        break;
                    case InProcess:
                        Integer jpgCount = status.getJpgCount();
                        Integer jpgsComplete = status.getJpgsComplete();
                        final String sPercentComplete = PERCENT_FORMAT.format(status.getPercentJpgsComplete());
                        System.out.println(jpgsComplete + " of " + jpgCount + " complete.  (" + sPercentComplete + ")   Elapsed time: " + elapsed + "s");
                        break;
                    case JustStarted:
                        System.out.println("Initiating..");
                        break;
                    default:
                        throw new IllegalStateException();
                }

            }
        };


        timer.scheduleAtFixedRate(timerTask, 0L, 1000L);

    }


    public static void detect() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = ge.getScreenDevices();
        System.out.println("devices.length = " + devices.length);
        for (GraphicsDevice device : devices) {
            GraphicsConfiguration configuration = device.getDefaultConfiguration();
            ImageCapabilities imageCapabilities = configuration.getImageCapabilities();
            System.out.println("isAccelerated = " + imageCapabilities.isAccelerated());
        }

    }


    public static class Args {

        CommandLineArgs args;

        static final String BRAND = "brand";
        static final String YEAR = "year";
        static final String SERIES = "series";
        static final String REV = "rev";
        static final String PROFILE = "profile";

        static final String THREAD_COUNT = "threadCount";
        static final String PRIORITY = "priority";

        static final String REPO_BASE_PREFIX = "repoBasePrefix";
        static final String REPO_BASE = "repoBase";

        private final Schema schema;

        private static Map<String, String> buildDefaults() {
            ImmutableMap.Builder<String, String> b = ImmutableMap.builder();
            b.put(BRAND, "toyota");
            b.put(YEAR, new Date().getYear() + "");
            b.put(SERIES, "avalon");
            b.put(PROFILE, "wStd");
            b.put(THREAD_COUNT, "5");
            b.put(PRIORITY, Thread.NORM_PRIORITY + "");
            b.put(REPO_BASE_PREFIX, new File(Sys.getUserDir(), "configurator-content-").getAbsolutePath());
            return b.build();
        }

        public Args(String[] args) {
            this.schema = buildSchema();
            this.args = new CommandLineArgs(schema, args);
        }

        private Schema buildSchema() {

            String defaultRepoBasePrefix = new File(Sys.getUserDir(), "configurator-content-").getAbsolutePath();

            Schema schema = new Schema();

            schema
                    .addField(BRAND, BrandKey.TOYOTA)
                    .addField(YEAR, new Date().getYear())
                    .addField(SERIES, "avalon")
                    .addField(PROFILE, "wStd")
                    .addField(THREAD_COUNT, 5)
                    .addField(PRIORITY, Thread.NORM_PRIORITY)
                    .addField(REPO_BASE_PREFIX, defaultRepoBasePrefix)
                    .addField(File.class, REPO_BASE, new FieldDefaulter<File>() {
                        @Nonnull
                        @Override
                        public File getDefaultValue(File defaultValue, CommandLineArgs context) {
                            BrandKey brand = context.get(BRAND);
                            String repoBasePrefix = context.get(REPO_BASE_PREFIX);
                            return new File(repoBasePrefix + brand.toString());
                        }
                    });

            return schema;
        }

        public File getRepoBase() {
            return args.get(REPO_BASE);
        }

        public ReposConfig getReposConfig() {
            return new ReposConfig(getBrand(), getRepoBase());
        }

        public BrandKey getBrand() {
            return args.get(BRAND);
        }

        public String getSeries() {
            return args.get(SERIES);
        }

        public Integer getYear() {
            return args.get(YEAR);
        }

        public String getRev() {
            return args.get(REV);
        }

        public Profile getProfile() {
            String p = args.get(PROFILE);
            JpgWidth jpgWidth = new JpgWidth(p);
            return new Profile(jpgWidth);
        }

        public SeriesKey getSeriesKey() {
            return new SeriesKey(getBrand(), getYear() + "", getSeries());
        }

        public Integer getThreadCount() {
            return args.get(THREAD_COUNT);
        }

        public Integer getPriority() {
            return args.get(PRIORITY);
        }

        public void printMerged(int i) {
            args.printMerged(i);

        }
    }

    private static Logger log = Logger.getLogger("c3i");


}
