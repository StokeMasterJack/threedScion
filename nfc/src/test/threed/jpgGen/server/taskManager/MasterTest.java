package threed.jpgGen.server.taskManager;

import threed.jpgGen.shared.JobSpec;
import threed.core.threedModel.shared.JpgWidth;
import threed.repo.server.Repos;

import threed.core.threedModel.shared.SeriesId;
import threed.core.threedModel.shared.SeriesKey;
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
