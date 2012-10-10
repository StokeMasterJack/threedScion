package c3i.jpgGen.server;

import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.imageModel.shared.Profile;
import c3i.core.threedModel.shared.JpgWidth;
import c3i.core.threedModel.shared.RootTreeId;
import c3i.jpgGen.server.taskManager.JpgGeneratorService;
import c3i.jpgGen.server.taskManager.Master;
import c3i.jpgGen.shared.JobSpec;
import c3i.jpgGen.shared.JobState;
import c3i.jpgGen.shared.JobStatus;
import c3i.repo.server.Repos;
import c3i.repo.server.SeriesRepo;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import smartsoft.util.CommandLineArgs;
import smartsoft.util.Sys;

import java.awt.*;
import java.io.File;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class JpgGenCommandLine {

    private static final NumberFormat PERCENT_FORMAT = NumberFormat.getPercentInstance();


    private final Args args;

    public JpgGenCommandLine(String... a) {
        args = new Args(a);

        //ide mode only
        args.putRaw(Args.REPO_BASE, "/Users/dford/temp/jpgGenTundraTest/configurator-content-v2");

        System.out.println("Using options:");
        args.printMerged(1);

        Repos.setRepoBaseDir(args.getRepoBase());
    }

    public static void main(String... args) {
        JpgGenCommandLine jpgGen = new JpgGenCommandLine(args);
        jpgGen.start();
    }

    public void start() {

        Repos repos = Repos.get();

        SeriesRepo seriesRepo = repos.getSeriesRepo(args.getSeriesKey());
        RootTreeId rootTreeId = seriesRepo.getSrcRepo().resolveRootTreeId(args.getRev());
        SeriesId seriesId = new SeriesId(args.getSeriesKey(), rootTreeId);
        JobSpec jobSpec = new JobSpec(seriesId, args.getProfile());

        final JpgGeneratorService jpgGen = new JpgGeneratorService(repos);


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

    private static Log log = LogFactory.getLog(JpgGenCommandLine.class);


    public static class Args extends CommandLineArgs {

        static final String BRAND = "brand";
        static final String YEAR = "year";
        static final String SERIES = "series";
        static final String REV = "rev";
        static final String PROFILE = "profile";

        static final String THREAD_COUNT = "threadCount";
        static final String PRIORITY = "priority";

        static final String REPO_BASE = "repoBase";

        private static Map<String, String> buildDefaults() {
            ImmutableMap.Builder<String, String> b = ImmutableMap.builder();
            b.put(BRAND, "toyota");
            b.put(YEAR, "2011");
            b.put(SERIES, "tundra");
            b.put(PROFILE, "wStd");
            b.put(THREAD_COUNT, "5");
            b.put(PRIORITY, Thread.NORM_PRIORITY + "");
            b.put(REPO_BASE, new File(Sys.getUserDir(), "configurator-content-v2").getAbsolutePath());
            return b.build();
        }

        public Args(String[] args) {
            super(args, buildDefaults());
        }

        public File getRepoBase() {
            String s = get(REPO_BASE);
            if (s == null) {
                return new File(Sys.getUserDir(), "configurator-content-2");
            }
            return new File(s);
        }

        public String getBrand() {
            return get(BRAND);
        }

        public String getSeries() {
            return get(SERIES);
        }

        public int getYear() {
            return getInteger(YEAR);
        }

        public String getRev() {
            return get(REV);
        }

        public Profile getProfile() {
            String p = get(PROFILE);
            JpgWidth jpgWidth = new JpgWidth(p);
            return new Profile(jpgWidth);
        }

        public SeriesKey getSeriesKey() {
            return new SeriesKey(getBrand(), getYear(), getSeries());
        }

        public int getThreadCount() {
            return getInteger(THREAD_COUNT);
        }

        public int getPriority() {
            return getInteger(PRIORITY);
        }


    }


}
