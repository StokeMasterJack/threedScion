package c3i.imgGen.shared;

import c3i.core.common.shared.BrandKey;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;

public interface JpgGenServiceAsync {
    /**
     * @return true if started, false if already running
     */
    void startJob(JobSpec jobSpec, AsyncCallback<Boolean> async);

    void getQueueStatus(BrandKey brandKey,AsyncCallback<ArrayList<JobStatusItem>> async);

    void getQueueDetails(BrandKey brandKey,JobId jobId, AsyncCallback<ArrayList<ExecutorStatus>> async);

    void getJpgGenFinalStats(BrandKey brandKey,JobId jobId, AsyncCallback<Stats> async);

    void cancelJob(BrandKey brandKey,JobId jobId, AsyncCallback<Void> async);

    void removeJob(BrandKey brandKey,JobId jobId, AsyncCallback<Void> async);

    void removeTerminal(BrandKey brandKey,AsyncCallback<Void> async);
}
