package threed.jpgGen.server;

import com.google.gwt.rpc.server.RpcServlet;
import threed.jpgGen.server.taskManager.EquivalentJobAlreadyRunningException;
import threed.jpgGen.server.taskManager.JpgGeneratorService;
import threed.jpgGen.server.taskManager.Master;
import threed.jpgGen.shared.*;
import threed.repo.server.Repos;
import threed.repoWebService.ThreedRepoApp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * <repo-base>/threed-admin/jpgGeneratorService
 *
 */
public class JpgGenServlet extends RpcServlet implements JpgGenService {

    private static final ThreedRepoApp app;
    private static final Log log;

    static {
        app = ThreedRepoApp.get();
        log = LogFactory.getLog(JpgGenServlet.class);
    }

    private final Repos repos;
    private final JpgGeneratorService jpgGen;

    public JpgGenServlet() {
        log.info("Initializing " + getClass().getSimpleName());
        File repoBaseDir = app.getRepoBaseDir();
        Repos.setRepoBaseDir(repoBaseDir);

        try {
            this.repos = Repos.get();
            this.jpgGen = new JpgGeneratorService(repos);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<JobStatusItem> getQueueStatus() {
        Collection<Master> masterJobs = jpgGen.getMasterJobs();
        ArrayList<JobStatusItem> jobStatusItems = new ArrayList<JobStatusItem>();
        for (Master job : masterJobs) {
            jobStatusItems.add(job.getJobStatusItem());
        }
        return jobStatusItems;
    }

    @Override
    public void cancelJob(JobId jobId) {
        log.warn("Cancelling jpg job: " + jobId);
        jpgGen.cancelJob(jobId);
    }

    @Override
    public void removeJob(JobId jobId) {
        jpgGen.removeJob(jobId);
    }

    @Override
    public void removeTerminal() {
        jpgGen.removeTerminal();
    }

    @Override
    public ArrayList<ExecutorStatus> getQueueDetails(JobId jobId) {
        Master masterJob = jpgGen.getJob(jobId);
        if (masterJob == null) {
            return new ArrayList<ExecutorStatus>();
        } else {
            return new ArrayList<ExecutorStatus>(masterJob.getExecutorStatuses());
        }
    }


    @Override
    public boolean startJob(JobSpec jobSpec) {
        try {
            jpgGen.startNewJpgJob(jobSpec, repos.getSettings().getJpgGenThreadCount());
            return true;
        } catch (EquivalentJobAlreadyRunningException e) {
            return false;
        }
    }

    @Override
    public Stats getJpgGenFinalStats(JobId jobId) {
        return jpgGen.getJob(jobId).getStats();
    }

    public void destroy() {
        log.info("\t Shutting down JpgGenerator..");
        jpgGen.stopAndWait();
        log.info("\tJpgGenerator shutdown complete");
    }


}
