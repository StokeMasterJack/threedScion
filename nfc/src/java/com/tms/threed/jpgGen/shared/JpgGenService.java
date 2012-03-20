package com.tms.threed.jpgGen.shared;

import com.google.gwt.rpc.client.RpcService;

import java.util.ArrayList;

public interface JpgGenService extends RpcService {

    /**
     * @return true if started, false if already running
     */
    boolean startJob(JobSpec jobSpec);

    ArrayList<JobStatusItem> getQueueStatus();

    ArrayList<ExecutorStatus> getQueueDetails(JobId jobId);

    Stats getJpgGenFinalStats(JobId jobId);

    void cancelJob(JobId jobId);

    void removeJob(JobId jobId);

    void removeTerminal();

}
