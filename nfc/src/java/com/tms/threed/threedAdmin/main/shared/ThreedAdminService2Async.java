package com.tms.threed.threedAdmin.main.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tms.threed.threedFramework.jpgGen.shared.JobId;
import com.tms.threed.threedFramework.jpgGen.shared.Stats;
import com.tms.threed.threedFramework.repo.shared.RootTreeId;
import com.tms.threed.threedFramework.repo.shared.RtConfig;
import com.tms.threed.threedFramework.repo.shared.SeriesNamesWithYears;
import com.tms.threed.threedFramework.repo.shared.TagCommit;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;

import java.util.ArrayList;
import java.util.List;

public interface ThreedAdminService2Async {

    void getSeriesNameWithYears(AsyncCallback<ArrayList<SeriesNamesWithYears>> async);

    void getJpgGenFinalStats(JobId jobId, AsyncCallback<Stats> async);

    void getVtcRootTreeId(SeriesKey seriesKey, AsyncCallback<RootTreeId> async);

    void setVtcRootTreeId(SeriesKey seriesKey, RootTreeId rootTreeId, AsyncCallback<Void> async);

    void getRepoConfig(AsyncCallback<RtConfig> async);

    void saveRtConfig(RtConfig repoConfig, AsyncCallback<Void> async);

    void getInitData(AsyncCallback<InitData> async);

    void getTagCommits(final SeriesKey seriesKey, AsyncCallback<List<TagCommit>> async);

    void tagCurrentVersion(SeriesKey seriesKey, String tagName, AsyncCallback<Void> async);
}
