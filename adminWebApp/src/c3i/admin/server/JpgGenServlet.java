package c3i.admin.server;

import c3i.core.common.shared.BrandKey;
import c3i.jpgGen.server.taskManager.EquivalentJobAlreadyRunningException;
import c3i.jpgGen.server.taskManager.JpgGeneratorService;
import c3i.jpgGen.server.taskManager.Master;
import c3i.jpgGen.shared.ExecutorStatus;
import c3i.jpgGen.shared.JobId;
import c3i.jpgGen.shared.JobSpec;
import c3i.jpgGen.shared.JobStatusItem;
import c3i.jpgGen.shared.JpgGenService;
import c3i.jpgGen.shared.Stats;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.Repos;
import com.google.gwt.rpc.server.RpcServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import java.awt.*;
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

    private BrandRepos brandRepos;
    private JpgGeneratorService jpgGen;


    public JpgGenServlet() {

    }

    @Override
    public void init() throws ServletException {
        super.init();
        log = LogFactory.getLog(JpgGenServlet.class);
        log.info("Initializing " + getClass().getSimpleName());

        try {
            app = ThreedAdminApp.getFromServletContext(getServletContext());
            brandRepos = app.getBrandRepos();
            jpgGen = app.getJpgGeneratorService();
            log.info(getClass().getSimpleName() + " initialization complete!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<JobStatusItem> getQueueStatus(BrandKey brandKey) {
        Collection<Master> masterJobs = jpgGen.getMasterJobs();
        ArrayList<JobStatusItem> jobStatusItems = new ArrayList<JobStatusItem>();
        for (Master job : masterJobs) {
            JobStatusItem item = job.getJobStatusItem();
            Integer jpgCount = item.getStatus().getJpgCount();
            jobStatusItems.add(item);
        }
        return jobStatusItems;

    }

    @Override
    public void cancelJob(BrandKey brandKey, JobId jobId) {
        log.warn("Cancelling jpg job: " + jobId);
        jpgGen.cancelJob(jobId);
    }

    @Override
    public void removeJob(BrandKey brandKey, JobId jobId) {
        jpgGen.removeJob(jobId);
    }

    @Override
    public void removeTerminal(BrandKey brandKey) {
        jpgGen.removeTerminal();
    }

    @Override
    public ArrayList<ExecutorStatus> getQueueDetails(BrandKey brandKey, JobId jobId) {
        Master masterJob = jpgGen.getJob(jobId);
        if (masterJob == null) {
            return new ArrayList<ExecutorStatus>();
        } else {
            return new ArrayList<ExecutorStatus>(masterJob.getExecutorStatuses());
        }
    }


    @Override
    public boolean startJob(JobSpec jobSpec) {

        BrandKey brandKey = jobSpec.getSeriesId().getSeriesKey().getBrandKey();
        boolean headless = GraphicsEnvironment.isHeadless();

        jobSpec.getProfile().getBaseImageType();

        Repos repos = brandRepos.getRepos(brandKey);
        try {
            int threadCount = repos.getSettings().getThreadCount();
            int priority = Thread.NORM_PRIORITY;
            if (isDfLocal()) {
                threadCount = 1;
                priority = Thread.MIN_PRIORITY;
            }

            jpgGen.startNewJpgJob(jobSpec, threadCount, priority);
            return true;
        } catch (EquivalentJobAlreadyRunningException e) {
            return false;
        }
    }

    @Override
    public Stats getJpgGenFinalStats(BrandKey brandKey, JobId jobId) {
        return jpgGen.getJob(jobId).getStats();
    }

    public void destroy() {
        jpgGen.stopAndWait();
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
