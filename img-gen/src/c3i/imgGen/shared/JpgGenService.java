package c3i.imgGen.shared;

import c3i.featureModel.shared.common.BrandKey;
import com.google.gwt.user.client.rpc.RemoteService;

import java.util.ArrayList;

public interface JpgGenService extends RemoteService {

    /**
     * @return true if started, false if already running
     */
    boolean startJob(JobSpec jobSpec);

    /**
     * Cancels a job if running but leaves it in the job queue
     */
    void cancelJob(BrandKey brandKey, JobId jobId);

    /**
     * Removes job from queue. If job is running, it is first canceled.
     */
    void removeJob(BrandKey brandKey, JobId jobId);

    /**
     * Removes all terminal (completed or canceled or errored) jobs
     */
    void removeTerminal(BrandKey brandKey);

    ArrayList<JobStatusItem> getQueueStatus(BrandKey brandKey);


    ArrayList<ExecutorStatus> getQueueDetails(BrandKey brandKey, JobId jobId);

    Stats getJpgGenFinalStats(BrandKey brandKey, JobId jobId);


}
