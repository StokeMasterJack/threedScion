package c3i.imgGen.server.taskManager;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;

import c3i.imgGen.api.SrcPngLoader;
import c3i.imgGen.generic.ImgGenService;
import c3i.imgGen.repoImpl.FmIm;
import c3i.imgGen.shared.JobId;
import c3i.imgGen.shared.JobSpec;
import c3i.imgGen.shared.JobState;
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
    private final ImgGenService imgGenService;
    private final SrcPngLoader srcPngLoader;

    public JpgGeneratorService(BrandRepos brandRepos, ImgGenService imgGenService, SrcPngLoader srcPngLoader) {
        this.brandRepos = brandRepos;
        this.jpgSetFactory = jpgSetFactory;
        this.imgGenService = imgGenService;
        this.srcPngLoader = srcPngLoader;
    }

    public Master startNewJpgJob(JobSpec jobSpec, int threadCount, int priority) throws EquivalentJobAlreadyRunningException {
        if (isThereAlreadyAnOpenJobWithThisSpec(jobSpec)) {
            log.severe("AlreadyRunningException");
            throw new EquivalentJobAlreadyRunningException();
        }

        jobSpec.getProfile().getBaseImageType();

        BrandKey brandKey = jobSpec.getBrandKey();
        Repos repos = brandRepos.getRepos(brandKey);

        SeriesRepo seriesRepo = repos.getSeriesRepo(jobSpec.getSeriesId().getSeriesKey());


        SeriesId seriesId = jobSpec.getSeriesId();
        FmIm fmIm = imgGenService.getFmIm(seriesId);

        Master master = new Master(
                jpgSetFactory,
                seriesRepo,
                jobSpec,
                fmIm,
                srcPngLoader,
                threadCount,
                priority);
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
