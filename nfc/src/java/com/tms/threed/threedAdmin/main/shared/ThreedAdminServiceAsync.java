package com.tms.threed.threedAdmin.main.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tms.threed.jpgGen.shared.JobId;
import com.tms.threed.jpgGen.shared.Stats;
import com.tms.threed.repo.shared.CommitHistory;
import com.tms.threed.repo.shared.CommitId;
import com.tms.threed.threedCore.threedModel.shared.RootTreeId;
import com.tms.threed.repo.shared.RtConfig;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;

public interface ThreedAdminServiceAsync {

    void getJpgGenFinalStats(JobId jobId, AsyncCallback<Stats> async);

    void getVtcRootTreeId(SeriesKey seriesKey, AsyncCallback<RootTreeId> async);

    void setVtcRootTreeId(SeriesKey seriesKey, RootTreeId rootTreeId, AsyncCallback<Void> async);

    void getRepoConfig(AsyncCallback<RtConfig> async);

    void saveRtConfig(RtConfig repoConfig, AsyncCallback<Void> async);

    void getInitData(AsyncCallback<InitData> async);

    void tagCommit(SeriesKey seriesKey, String newTagName, CommitId commitId, AsyncCallback<CommitHistory> async);

    void addAllAndCommit(SeriesKey seriesKey, String commitMessage, String tag, AsyncCallback<CommitHistory> async);

    void getCommitHistory(SeriesKey seriesKey, AsyncCallback<CommitHistory> async);

    void purgeRepoCache(AsyncCallback<Void> async);
}
