package c3i.jpgGen.server.taskManager;

import c3i.core.common.shared.SeriesId;
import c3i.jpgGen.shared.JobId;
import c3i.jpgGen.shared.JobSpec;
import c3i.jpgGen.shared.JobState;
import c3i.repo.server.Repos;
import c3i.repo.server.SeriesRepo;
import c3i.repo.server.SrcRepo;
import com.google.common.util.concurrent.AbstractIdleService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JpgGeneratorService extends AbstractIdleService {

    private final ConcurrentHashMap<JobId, Master> jobQueue = new ConcurrentHashMap<JobId, Master>();

    private final Repos repos;

    public JpgGeneratorService(Repos repos) {
        this.repos = repos;
    }

    public Master startNewJpgJob(JobSpec jobSpec, int threadCount, int priority) throws EquivalentJobAlreadyRunningException {
        if (isThereAlreadyAnOpenJobWithThisSpec(jobSpec)) {
            log.error("AlreadyRunningException");
            throw new EquivalentJobAlreadyRunningException();
        }

        jobSpec.getProfile().getBaseImageType();
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
        Master job = getJob(jobId);
        if (!job.getFuture().isCancelled()) {
            job.cancel();
        }
        jobQueue.remove(jobId);
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

    private static Log log = LogFactory.getLog(JpgGeneratorService.class);

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
