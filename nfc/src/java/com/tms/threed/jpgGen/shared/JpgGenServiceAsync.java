package com.tms.threed.jpgGen.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;

public interface JpgGenServiceAsync {
    /**
     * @return true if started, false if already running
     */
    void startJob(JobSpec jobSpec, AsyncCallback<Boolean> async);

    void getQueueStatus(AsyncCallback<ArrayList<JobStatusItem>> async);

    void getQueueDetails(JobId jobId, AsyncCallback<ArrayList<ExecutorStatus>> async);

    void getJpgGenFinalStats(JobId jobId, AsyncCallback<Stats> async);

    void cancelJob(JobId jobId, AsyncCallback<Void> async);

    void removeJob(JobId jobId, AsyncCallback<Void> async);

    void removeTerminal(AsyncCallback<Void> async);
}
