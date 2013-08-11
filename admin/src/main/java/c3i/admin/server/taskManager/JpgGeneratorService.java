package c3i.admin.server.taskManager;

import c3i.admin.shared.jpgGen.JobId;
import c3i.admin.shared.jpgGen.JobSpec;
import c3i.admin.shared.jpgGen.JobState;
import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.Repos;
import c3i.repo.server.SeriesRepo;
import c3i.repo.server.SrcRepo;
import com.google.common.util.concurrent.AbstractIdleService;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class JpgGeneratorService extends AbstractIdleService {

    private final ConcurrentHashMap<JobId, Master> jobQueue = new ConcurrentHashMap<JobId, Master>();

    private final BrandRepos brandRepos;

    public JpgGeneratorService(BrandRepos brandRepos) {
        this.brandRepos = brandRepos;
    }

    public Master startNewJpgJob(JobSpec jobSpec, int threadCount, int priority) throws EquivalentJobAlreadyRunningException {
        if (isThereAlreadyAnOpenJobWithThisSpec(jobSpec)) {
            log.severe("AlreadyRunningException");
            throw new EquivalentJobAlreadyRunningException();
        }

        jobSpec.getProfile().getBaseImageType();

        BrandKey brandKey = jobSpec.getBrandKey();
        Repos repos = brandRepos.getRepos(brandKey);

        Master master = new Master(repos, jobSpec, threadCount, priority);
        jobQueue.put(master.getId(), master);
        return master;
    }


    private boolean isThereAlreadyAnOpenJobWithThisSpec(JobSpec jobSpec) {
        for (Master master : jobQueue.values()) {
            boolean jobSpecMatch = master.getJobSpec().equals(jobSpec);

            JobState state = master.getStatus().getState();

            boolean isRunning = state.equals(JobState.InProcess) || state.equals(JobState.JustStarted);

            if (jobSpecMatch && isRunning) {
                return true;
            }

        }
        return false;
    }


    private String commitIdToTag(SeriesId seriesId) {
        Repos repos = brandRepos.getRepos(seriesId.getBrandKey());
        SeriesRepo seriesRepo = repos.getSeriesRepo(seriesId.getSeriesKey());
        SrcRepo srcRepo = seriesRepo.getSrcRepo();
        Map<String, Ref> tags = srcRepo.getTags();

        for (Map.Entry<String, Ref> e : tags.entrySet()) {
            ObjectId objectId = e.getValue().getObjectId();
            if (objectId.getName().equals(seriesId.getRootTreeId())) {
                return e.getKey();
            }
        }
        return null;

    }

    public Master getJob(JobId jobId) {
        return jobQueue.get(jobId);
    }

    public void cancelJob(JobId jobId) {
        log.info("Server canceling job[" + jobId + "]");
        Master job = getJob(jobId);
        job.cancel();
    }


    public void removeJob(JobId jobId) {
        log.info("Server removing job[" + jobId + "]");
        Master job = jobQueue.remove(jobId);
        if (job != null) {
            Master.KickOffTask future = job.getFuture();
            if (future != null && !future.isCancelled()) {
                job.cancel();
            }
        }

    }


    public Collection<Master> getMasterJobs() {
        return jobQueue.values();
    }

    public void removeTerminal() {
        for (Master master : jobQueue.values()) {
            if (master.getFuture().isDone()) {
                jobQueue.remove(master.getId());
            }
        }
    }

    private static Logger log = Logger.getLogger("c3i");

    @Override
    protected void startUp() throws Exception {

    }

    /**
     * Actually does a shutdownNow
     */
    @Override
    protected void shutDown() throws Exception {
        for (Master master : jobQueue.values()) {
            master.shutdownNow();
        }
    }
}
