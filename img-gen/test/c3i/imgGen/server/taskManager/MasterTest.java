package c3i.imgGen.server.taskManager;

import c3i.featureModel.shared.common.BrandKey;
import c3i.featureModel.shared.common.SeriesId;
import c3i.featureModel.shared.common.SeriesKey;
import c3i.featureModel.shared.node.Csp;
import c3i.featureModel.shared.search.CountingProductHandler;
import c3i.iga.Util;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.Profile;
import c3i.imageModel.shared.Slice;
import c3i.imageModel.shared.Slice2;
import c3i.imgGen.ImgGenApp;
import c3i.imgGen.api.ThreedModelService;
import c3i.imgGen.shared.JobSpec;
import c3i.imgGen.shared.JobState;
import c3i.imgGen.shared.JobStatus;
import c3i.ip.SrcPngLoader;
import c3i.repo.server.BrandRepo;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.SeriesRepo;
import c3i.repo.server.TestConstants;
import c3i.iga.JpgSet;
import c3i.iga.JpgSets;
import c3i.threedModel.client.ThreedModel;
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

    ImgGenApp imgGenApp;

    BrandRepos brandRepos;
    BrandRepo brandRepo;
    ThreedModelService threedModelService;
    SrcPngLoader srcPngLoader;

    @Before
    public void setUp() throws Exception {

        imgGenApp = new ImgGenApp();
        this.srcPngLoader = imgGenApp.getSrcPngLoader();
        brandRepos = BrandRepos.createSingleBrand(BrandKey.TOYOTA, TOYOTA_REPO_BASE_DIR);
//        imgGenKit = new ImgGenKitRepo(brandRepos);
        brandRepo = brandRepos.getBrandRepo(BrandKey.TOYOTA);

        threedModelService = imgGenApp.getThreedModelService();
    }

    @Test
    public void testSatCountAvalon2014() throws Exception {
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2014, SeriesKey.AVALON);
        SeriesId seriesId = brandRepo.getHead(seriesKey);
        ThreedModel threedModel = brandRepo.getThreedModel(seriesId);
        Csp csp = threedModel.getFeatureModel().createCsp();
        System.out.println(csp.getProductCount());
    }

    @Test
    public void testSatCountTundra2013() throws Exception {
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2013, SeriesKey.TUNDRA);
        SeriesId seriesId = brandRepo.getHead(seriesKey);
        ThreedModel threedModel = brandRepo.getThreedModel(seriesId);
        Csp csp = threedModel.getFeatureModel().createCsp();
        long satCount = csp.getProductCount();
        System.out.println(satCount);
    }

    @Test
    public void testForEachTundra2013() throws Exception {
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2013, SeriesKey.TUNDRA);
        SeriesId seriesId = brandRepo.getHead(seriesKey);
        ThreedModel threedModel = brandRepo.getThreedModel(seriesId);
        Csp csp = threedModel.getFeatureModel().createCsp();
        CountingProductHandler ph = new CountingProductHandler();
        csp.forEachProduct(ph);
        System.out.println(ph.getCount());
    }

    @Test
    public void testMasterTaskOnTundra2013() throws Exception {
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2013, "tundra");
        SeriesId seriesId = brandRepo.getHead(seriesKey);

        SeriesRepo seriesRepo = brandRepo.getSeriesRepo(seriesKey);

        Profile profile = brandRepo.getProfiles().get("wStd");
        JobSpec jobSpec = new JobSpec(seriesId, profile);

        final Master master = new Master(
                seriesRepo,
                jobSpec,
                threedModelService,
                srcPngLoader,
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
        SeriesId seriesId = brandRepo.getHead(seriesKey);

        ThreedModel threedModel = threedModelService.getThreedModel(seriesId);
        JpgSets jpgSets = Util.createJpgSets(threedModel);
        int jpgCount = jpgSets.getJpgCount();

        assertEquals(2385, jpgCount);

    }

    //1s
    @Test
    public void testAnalOnlyOnTundra2014() throws Exception {
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2014, "tundra");
        SeriesId seriesId = brandRepo.getHead(seriesKey);

        ThreedModel threedModel = threedModelService.getThreedModel(seriesId);
        JpgSets jpgSets = Util.createJpgSets(threedModel);
        int jpgCount = jpgSets.getJpgCount();

        System.out.println("jpgCount = " + jpgCount);

    }

    //1s
    @Test
    public void testAnalOnlyOnTundra2014OneAngle() throws Exception {
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2014, "tundra");
        SeriesId seriesId = brandRepo.getHead(seriesKey);

        ThreedModel threedModel = threedModelService.getThreedModel(seriesId);
        JpgSet jpgSet = Util.createJpgSet(threedModel,new Slice("exterior", 2));

        System.out.println("jpgCount = " + jpgSet.getJpgCount());

    }

    @Test
    public void testAnalOnly2() throws Exception {
        //        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2013, "tundra");
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2014, "tundra");
        SeriesId seriesId = brandRepo.getHead(seriesKey);

        ThreedModel threedModel = brandRepo.getThreedModel(seriesId);

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
        ThreedModel threedModel = threedModelService.getThreedModel(seriesId);
        return Util.createJpgSet(threedModel,slice.getSlice());
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
        File repoBaseDir = brandRepo.getRepoBaseDir();
        File cacheDir = brandRepo.getCacheDir();
        System.out.println("repoBaseDir = " + repoBaseDir);
//          InputStream is = Files.readBytes()
    }


    public void test1() throws Exception {
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2013, "tundra");
        SeriesId seriesId = brandRepo.getHead(seriesKey);
        SeriesRepo seriesRepo = brandRepo.getSeriesRepo(seriesKey);


        final Master master = new Master(
                seriesRepo,
                new JobSpec(seriesId, Profile.STD),
                threedModelService,
                srcPngLoader,
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
