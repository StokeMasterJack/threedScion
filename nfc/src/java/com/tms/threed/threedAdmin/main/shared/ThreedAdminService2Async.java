package com.tms.threed.threedAdmin.main.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tms.threed.threedFramework.jpgGen.shared.JobId;
import com.tms.threed.threedFramework.jpgGen.shared.Stats;
import com.tms.threed.threedFramework.repo.shared.CommitHistory;
import com.tms.threed.threedFramework.repo.shared.CommitId;
import com.tms.threed.threedFramework.repo.shared.RootTreeId;
import com.tms.threed.threedFramework.repo.shared.RtConfig;
import com.tms.threed.threedFramework.repo.shared.SeriesNamesWithYears;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;

import java.util.ArrayList;

public interface ThreedAdminService2Async {

    void getSeriesNameWithYears(AsyncCallback<ArrayList<SeriesNamesWithYears>> async);

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
