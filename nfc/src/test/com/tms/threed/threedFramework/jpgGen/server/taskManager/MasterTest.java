package com.tms.threed.threedFramework.jpgGen.server.taskManager;

import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.threedModel.server.TestHelper;
import com.tms.threed.threedFramework.threedModel.shared.SeriesId;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Timer;
import java.util.TimerTask;

public class MasterTest extends TestCase {

    public void test1() throws Exception {
        Repos repos = TestHelper.getRepos();
        SeriesKey seriesKey = SeriesKey.CAMRY_2011;
        SeriesId seriesId = repos.getHead(seriesKey);
        final Master master = new Master(repos, seriesId, JpgWidth.W_STD,5);
        TimerTask timerTask = new TimerTask() {
            @Override public void run() {
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

    private static Log log = LogFactory.getLog(MasterTest.class);


}
