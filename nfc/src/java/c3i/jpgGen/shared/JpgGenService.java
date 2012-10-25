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

    ArrayList<ExecutorStatus> getQueueDetails(BrandKey brandKey,JobId jobId);

    Stats getJpgGenFinalStats(BrandKey brandKey,JobId jobId);

    void cancelJob(BrandKey brandKey,JobId jobId);

    void removeJob(BrandKey brandKey,JobId jobId);

    void removeTerminal(BrandKey brandKey);

}
