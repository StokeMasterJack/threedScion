package c3i.admin.server.taskManager;

import c3i.admin.server.JpgSet;
import c3i.admin.shared.jpgGen.JobSpec;
import c3i.admin.shared.jpgGen.JobState;
import c3i.admin.shared.jpgGen.JobStatus;
import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.shared.CspForTreeSearch;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.featureModel.shared.search.ProductHandler;
import c3i.imageModel.shared.ImageModel;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.Profile;
import c3i.imageModel.shared.SimplePicks;
import c3i.imageModel.shared.Slice2;
import c3i.core.threedModel.server.TestConstants;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.repo.server.Repos;
import org.junit.Before;
import org.junit.Test;
import sun.misc.VM;

import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class MasterTest implements TestConstants {

    Repos repos;

    static class MySimplePicks implements SimplePicks {

        private final Set<Var> set;

        MySimplePicks(Set<Var> set) {
            this.set = set;
        }

        @Override
        public boolean isPicked(Object var) {
            return set.contains(var);
        }

        @Override
        public boolean isValidBuild() {
            throw new IllegalStateException();
        }
    }

    @Before
    public void setUp() throws Exception {
        repos = new Repos(BrandKey.TOYOTA, TOYOTA_REPO_BASE_DIR);
    }

    @Test
    public void testDave() throws Exception {
        SeriesKey seriesKey = SeriesKey.IQ_2012;
        SeriesId seriesId = repos.getHead(seriesKey);
        ThreedModel threedModel = repos.getThreedModel(seriesId);
        ImageModel imageModel = threedModel.getImageModel();


//        imageModel.getView("exterior").getSrcPngs(simplePicks, 2);
    }


    @Test
    public void test0() throws Exception {
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2013, SeriesKey.TUNDRA);
//        SeriesKey seriesKey = SeriesKey.IQ_2012;
        SeriesId seriesId = repos.getHead(seriesKey);

        ThreedModel threedModel = repos.getThreedModel(seriesId);

        CspForTreeSearch csp = threedModel.getFeatureModel().createCspForTreeSearch();


        final HashSet<String> all = new HashSet<String>();
        csp.findAll(new ProductHandler() {

            @Override
            public void onProduct(SimplePicks product) {
                all.add(product.toString());
            }
        });


    }

    @Test
    public void test2() throws Exception {
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2013, "tundra");
        SeriesId seriesId = repos.getHead(seriesKey);

        Profile profile = repos.getProfiles().get("wStd");
        final Master master = new Master(repos, new JobSpec(seriesId, profile), 5, Thread.NORM_PRIORITY);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
//                master.printStatusBrief();
                printBrief(master.getStatus());
            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 5000);


        System.err.println("awaitDoneDeep..");
        master.awaitDoneDeep();
        log.info("Complete!");


        log.info("Final status:");
        master.printStatus();

        log.info(null);

        master.getStats().printDeltas();


    }

    @Test
    public void testAnalOnly() throws Exception {
//        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2013, "tundra");
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2011, "avalon");
        SeriesId seriesId = repos.getHead(seriesKey);


        ThreedModel threedModel = repos.getThreedModel(seriesId);

        long jpgCount = 0;
        for (ImView view : threedModel.getViews()) {
            for (int angle = 1; angle <= view.getAngleCount(); angle++) {
                Slice2 slice = new Slice2(view, angle);
                JpgSet jpgSet = createJpgSet(seriesId, slice);
                jpgCount += jpgSet.size();
//                writeJpgSet(jpgSet);
            }
        }

        System.out.println("jpgCount = " + jpgCount);


    }

    public JpgSet createJpgSet(SeriesId seriesId, Slice2 slice) throws Exception {
        long t1 = System.currentTimeMillis();

        System.out.print("Start CreateJpgSet " + slice + " .. ");

        JpgSet.JpgSetKey jpgSetKey = new JpgSet.JpgSetKey(seriesId, slice.getViewName(), slice.getAngle());

        JpgSet jpgSet = JpgSet.createJpgSet(repos, jpgSetKey);

        long t2 = System.currentTimeMillis();
        System.out.println("Complete! jpgCount: " + jpgSet.size() + "  Delta: " + (t2 - t1));

        return jpgSet;
    }

    public void writeJpgSet(JpgSet jpgSet) throws Exception {
        File cacheDir = repos.getCacheDir();
        long t1 = System.currentTimeMillis();
        System.out.println("Start writeJpgSet");


//        jpgSet.writeToFile(cacheDir,jpgSet.);


        long t2 = System.currentTimeMillis();
        System.out.println("Complete writeJpgSet Delta: " + (t2 - t1));
    }


    public void printBrief(JobStatus jobStatus) {
        JobState state = jobStatus.getState();
        String time = TIME_FORMAT.format(new Date());

        System.out.print(time + "\t");
        System.out.print(state + "\t");
        boolean displayProgress = state.equals(JobState.InProcess) || state.equals(JobState.Complete);
        if (displayProgress) {
            String progressMsg = getPercentJpgsCompleteFormatted(jobStatus) + "  \t" + jobStatus.getJpgsComplete() + "/" + jobStatus.getJpgCount();
            System.out.println(progressMsg);
        } else {
            System.out.println();
        }
    }

    private static final NumberFormat PERCENT_FORMAT = NumberFormat.getPercentInstance();
    private static final DateFormat TIME_FORMAT = DateFormat.getTimeInstance();

    public String getPercentJpgsCompleteFormatted(JobStatus jobStatus) {
        return PERCENT_FORMAT.format(jobStatus.getPercentJpgsComplete());
    }

    @Test
    public void testTmp() throws Exception {
        File repoBaseDir = repos.getRepoBaseDir();
        File cacheDir = repos.getCacheDir();
        System.out.println("repoBaseDir = " + repoBaseDir);
//          InputStream is = Files.readBytes()
    }


    public void test1() throws Exception {
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2013, "tundra");
        SeriesId seriesId = repos.getHead(seriesKey);
        final Master master = new Master(repos, new JobSpec(seriesId, Profile.STD), 5, Thread.NORM_PRIORITY);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                printBrief(master.getStatus());
            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 5000);


        System.err.println("awaitDoneDeep..");
        master.awaitDoneDeep();
        log.info("Complete!");


        log.info("Final status:");
        master.printStatus();

        log.info(null);

        master.getStats().printDeltas();


    }

    @Test
    public void test4() throws Exception {


        System.out.println(VM.maxDirectMemory());
        //  129,957,888
        //2,117,795,840

    }

    private static Logger log = Logger.getLogger("c3i");


}
