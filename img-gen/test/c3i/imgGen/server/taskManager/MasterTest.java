package c3i.imgGen.server.taskManager;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.shared.CspForTreeSearch;
import c3i.core.featureModel.shared.search.SatCountProductHandler;
import c3i.core.threedModel.server.TestConstants;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.Profile;
import c3i.imageModel.shared.Slice2;
import c3i.imgGen.api.Kit;
import c3i.imgGen.generic.ImgGenService;
import c3i.imgGen.repoImpl.KitRepo;
import c3i.imgGen.server.JpgSet;
import c3i.imgGen.server.JpgSetKey;
import c3i.imgGen.server.JpgSetsTask;
import c3i.imgGen.shared.JobSpec;
import c3i.imgGen.shared.JobState;
import c3i.imgGen.shared.JobStatus;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.Repos;
import c3i.repo.server.SeriesRepo;
import org.junit.Before;
import org.junit.Test;
import sun.misc.VM;

import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class MasterTest implements TestConstants {

    BrandRepos brandRepos;
    Repos repos;
    ImgGenService<SeriesId> imgGenService;
    Kit kit;

    @Before
    public void setUp() throws Exception {
        brandRepos = BrandRepos.createSingleBrand(BrandKey.TOYOTA, TOYOTA_REPO_BASE_DIR);
        kit = new KitRepo(brandRepos);
        repos = brandRepos.getRepos(BrandKey.TOYOTA);
        imgGenService = new ImgGenService<SeriesId>(kit);
    }

    @Test
    public void testSatCountTundra2013() throws Exception {
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2013, SeriesKey.TUNDRA);
        SeriesId seriesId = repos.getHead(seriesKey);
        ThreedModel threedModel = repos.getThreedModel(seriesId);
        CspForTreeSearch csp = threedModel.getFeatureModel().createCspForTreeSearch();
        long satCount = csp.getSatCount();
        System.out.println(satCount);
    }

    @Test
    public void testForEachTundra2013() throws Exception {
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2013, SeriesKey.TUNDRA);
        SeriesId seriesId = repos.getHead(seriesKey);
        ThreedModel threedModel = repos.getThreedModel(seriesId);
        CspForTreeSearch csp = threedModel.getFeatureModel().createCspForTreeSearch();
        SatCountProductHandler ph = new SatCountProductHandler();
        csp.forEach(ph);
        System.out.println(ph.getSatCount());
    }

    @Test
    public void testMasterTaskOnTundra2013() throws Exception {
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2013, "tundra");
        SeriesId seriesId = repos.getHead(seriesKey);

        SeriesRepo seriesRepo = repos.getSeriesRepo(seriesKey);

        Profile profile = repos.getProfiles().get("wStd");
        JobSpec jobSpec = new JobSpec(seriesId, profile);

        final Master master = new Master(
                seriesRepo,
                jobSpec,
                imgGenService,
                kit.createSrcPngLoader(),
                5,
                Thread.NORM_PRIORITY);


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

    //1s
    @Test
    public void testAnalOnlyOnAvalon2014() throws Exception {
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2014, "avalon");
        SeriesId seriesId = repos.getHead(seriesKey);


        JpgSetsTask task = new JpgSetsTask(imgGenService.getFmIm(seriesId));
        task.start();
        int jpgCount = task.getJpgCount();
        assertEquals(2385, jpgCount);

    }

    //1s
    @Test
    public void testAnalOnlyOnTundra2014() throws Exception {
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2014, "tundra");
        SeriesId seriesId = repos.getHead(seriesKey);


        JpgSetsTask task = new JpgSetsTask(imgGenService.getFmIm(seriesId));
        task.start();
        int jpgCount = task.getJpgCount();
//        assertEquals(2385, jpgCount);
        System.out.println("jpgCount = " + jpgCount);

    }

    @Test
    public void testAnalOnly2() throws Exception {
        //        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2013, "tundra");
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2014, "tundra");
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
        JpgSet jpgSet = imgGenService.getJpgSet(seriesId, slice.getSlice());
        long t2 = System.currentTimeMillis();
        return jpgSet;
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
        SeriesRepo seriesRepo = repos.getSeriesRepo(seriesKey);


        final Master master = new Master(
                seriesRepo,
                new JobSpec(seriesId, Profile.STD),
                imgGenService,
                kit.createSrcPngLoader(),
                5,
                Thread.NORM_PRIORITY);
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
