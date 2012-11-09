package c3i.jpgGen.shared;

import c3i.core.common.shared.BrandKey;
import com.google.gwt.rpc.client.RpcService;

import java.util.ArrayList;

public interface JpgGenService extends RpcService {

    /**
     * @return true if started, false if already running
     */
    boolean startJob(JobSpec jobSpec);

    ArrayList<JobStatusItem> getQueueStatus(BrandKey brandKey);

    ArrayList<ExecutorStatus> getQueueDetails(BrandKey brandKey, JobId jobId);

    Stats getJpgGenFinalStats(BrandKey brandKey, JobId jobId);

    /**
     * Cancels a job if running but leaves it in the job queue
     */
    void cancelJob(BrandKey brandKey, JobId jobId);

    /**
     * Removes job from queue. If job is running, it is first canceled.
     */
    void removeJob(BrandKey brandKey, JobId jobId);

    /**
     * Removes all jobs in the terminal state
     */
    void removeTerminal(BrandKey brandKey);

}
