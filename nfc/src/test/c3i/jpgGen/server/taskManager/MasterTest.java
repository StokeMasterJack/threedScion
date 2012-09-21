package c3i.jpgGen.server.taskManager;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.shared.AssignmentsForTreeSearch;
import c3i.core.featureModel.shared.CspForTreeSearch;
import c3i.core.featureModel.shared.search.ProductHandler;
import c3i.core.imageModel.shared.Profile;
import c3i.core.threedModel.server.TestConstants;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.jpgGen.shared.JobSpec;
import c3i.repo.server.Repos;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import sun.misc.VM;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class MasterTest {

    @Test
    public void test0() throws Exception {
        Repos.setRepoBaseDir(TestConstants.REPO_BASE_DIR);
        Repos repos = Repos.get();
        SeriesKey seriesKey = SeriesKey.IQ_2012;
        SeriesId seriesId = repos.getHead(seriesKey);


        ThreedModel threedModel = repos.getThreedModel(seriesId);

        CspForTreeSearch csp = threedModel.getFeatureModel().createCspForTreeSearch();

        final HashSet<String> all = new HashSet<String>();
        csp.findAll(new ProductHandler() {
            @Override
            public void onProduct(AssignmentsForTreeSearch product) {
                all.add(product.getTrueOutputVars().toString());
            }
        });


    }

    @Test
    public void test2() throws Exception {
        Repos.setRepoBaseDir(TestConstants.REPO_BASE_DIR);
        Repos repos = Repos.get();
        SeriesKey seriesKey = SeriesKey.IQ_2012;
        SeriesId seriesId = repos.getHead(seriesKey);

        Profile profile = repos.getProfiles(BrandKey.SCION).get("wStdP");
        final Master master = new Master(repos, new JobSpec(seriesId, profile), 5,Thread.NORM_PRIORITY);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                master.printStatusBrief();
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


    public void test1() throws Exception {
        Repos repos = Repos.get();
        SeriesKey seriesKey = SeriesKey.CAMRY_2011;
        SeriesId seriesId = repos.getHead(seriesKey);
        final Master master = new Master(repos, new JobSpec(seriesId, Profile.STD), 5,Thread.NORM_PRIORITY);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                master.printStatusBrief();
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

    private static Log log = LogFactory.getLog(MasterTest.class);


}
