package com.tms.threed.jpgGen.server.taskManager;

import com.tms.threed.jpgGen.shared.JobSpec;
import com.tms.threed.threedCore.threedModel.shared.JpgWidth;
import com.tms.threed.repoService.server.Repos;

import com.tms.threed.threedCore.threedModel.shared.SeriesId;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Timer;
import java.util.TimerTask;

public class MasterTest extends TestCase {

    public void test1() throws Exception {
        Repos repos = Repos.get();
        SeriesKey seriesKey = SeriesKey.CAMRY_2011;
        SeriesId seriesId = repos.getHead(seriesKey);
        final Master master = new Master(repos, new JobSpec(seriesId, JpgWidth.W_STD),5);
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
