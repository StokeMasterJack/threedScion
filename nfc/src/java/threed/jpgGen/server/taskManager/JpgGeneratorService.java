package threed.jpgGen.server.taskManager;

import com.google.common.util.concurrent.AbstractIdleService;
import threed.jpgGen.shared.JobId;
import threed.jpgGen.shared.JobSpec;
import threed.jpgGen.shared.JobState;
import threed.repo.server.Repos;
import threed.repo.server.SeriesRepo;
import threed.repo.server.SrcRepo;
import threed.core.threedModel.shared.SeriesId;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JpgGeneratorService extends AbstractIdleService {

    private final ConcurrentHashMap<JobId, Master> map = new ConcurrentHashMap<JobId, Master>();

    private final Repos repos;

    public JpgGeneratorService(Repos repos) {
        this.repos = repos;
    }


    public Master startNewJpgJob(JobSpec jobSpec,int threadCount) throws EquivalentJobAlreadyRunningException {
        if (isThereAlreadyAnOpenJobWithThisSpec(jobSpec)) {
            log.error("AlreadyRunningException");
            throw new EquivalentJobAlreadyRunningException();
        }

        Master master = new Master(repos, jobSpec,threadCount);
        map.put(master.getId(), master);
        return master;
    }

    private boolean isThereAlreadyAnOpenJobWithThisSpec(JobSpec jobSpec) {
        for (Master master : map.values()) {
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
        return map.get(jobId);
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
        map.remove(jobId);
    }


    public Collection<Master> getMasterJobs() {
        return map.values();
    }

    public void removeTerminal() {
        for (Master master : map.values()) {
            if (master.getFuture().isDone()) {
                map.remove(master.getId());
            }
        }
    }

    private static Log log = LogFactory.getLog(JpgGeneratorService.class);

    @Override protected void startUp() throws Exception {

    }

    /**
     * Actually does a shutdownNow
     */
    @Override protected void shutDown() throws Exception {
        for (Master master : map.values()) {
            master.shutdownNow();
        }
    }
}
