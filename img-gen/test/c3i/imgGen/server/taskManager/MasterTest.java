package c3i.imgGen.server.taskManager;

import c3i.featureModel.shared.common.BrandKey;
import c3i.featureModel.shared.common.SeriesId;
import c3i.featureModel.shared.common.SeriesKey;
import c3i.iga.JpgSets;
import c3i.iga.Util;
import c3i.imageModel.shared.Profile;
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
import c3i.threedModel.client.ThreedModel;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class MasterTest implements TestConstants {

    private static final NumberFormat PERCENT_FORMAT = NumberFormat.getPercentInstance();
    private static final DateFormat TIME_FORMAT = DateFormat.getTimeInstance();

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
        brandRepo = brandRepos.getBrandRepo(BrandKey.TOYOTA);
        threedModelService = imgGenApp.getThreedModelService();
    }

    @Test
    public void testAnalOnlyOnAvalon2014() throws Exception {
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2014, "avalon");
        SeriesId seriesId = brandRepo.getHead(seriesKey);

        ThreedModel threedModel = threedModelService.getThreedModel(seriesId);
        JpgSets jpgSets = Util.createJpgSets(threedModel);
        int jpgCount = jpgSets.getJpgCount();

        assertEquals(2385, jpgCount);

    }

    @Test
    public void testMasterTaskOnAvalon2014() throws Exception {
        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2014, "avalon");
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


    public String getPercentJpgsCompleteFormatted(JobStatus jobStatus) {
        return PERCENT_FORMAT.format(jobStatus.getPercentJpgsComplete());
    }

    private static Logger log = Logger.getLogger("c3i");


}
