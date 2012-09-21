package c3i.jpgGen.server;

import c3i.admin.server.ThreedAdminApp;
import c3i.jpgGen.server.taskManager.EquivalentJobAlreadyRunningException;
import c3i.jpgGen.server.taskManager.JpgGeneratorService;
import c3i.jpgGen.server.taskManager.Master;
import c3i.jpgGen.shared.ExecutorStatus;
import c3i.jpgGen.shared.JobId;
import c3i.jpgGen.shared.JobSpec;
import c3i.jpgGen.shared.JobStatusItem;
import c3i.jpgGen.shared.JpgGenService;
import c3i.jpgGen.shared.Stats;
import c3i.repo.server.Repos;
import com.google.gwt.rpc.server.RpcServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.misc.VM;

import javax.servlet.ServletException;
import java.awt.*;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * <repo-base>/threed-admin/jpgGeneratorService
 *
 */
public class JpgGenServlet extends RpcServlet implements JpgGenService {

    private ThreedAdminApp app;
    private Log log;


    private Repos repos;
    private JpgGeneratorService jpgGen;

    public JpgGenServlet() {

    }

    @Override
    public void init() throws ServletException {
        super.init();
        app = ThreedAdminApp.get();
        log = LogFactory.getLog(JpgGenServlet.class);
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

        boolean headless = GraphicsEnvironment.isHeadless();
        System.out.println("headless = " + headless);
        System.out.println("VM.maxDirectMemory = " + VM.maxDirectMemory());

        jobSpec.getProfile().getBaseImageType();
        try {
            int threadCount = repos.getSettings().getThreadCount();
            int priority = Thread.NORM_PRIORITY;
            if (isDfLocal()) {
                threadCount = 1;
                priority = Thread.MIN_PRIORITY;
            }

            System.out.println("threadCount = " + threadCount);
            System.out.println("priority = " + priority);


            jpgGen.startNewJpgJob(jobSpec, threadCount, priority);
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

    public static boolean isDfLocal() {
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            return hostName != null && hostName.equals("df.local");
        } catch (UnknownHostException e) {
            return false;
        }
    }


}
