package com.tms.threed.threedAdmin.main.shared;

import com.google.gwt.rpc.client.RpcService;
import com.tms.threed.threedFramework.jpgGen.shared.JobId;
import com.tms.threed.threedFramework.jpgGen.shared.Stats;
import com.tms.threed.threedFramework.repo.shared.RepoHasNoHeadException;
import com.tms.threed.threedFramework.repo.shared.CommitHistory;
import com.tms.threed.threedFramework.repo.shared.CommitId;
import com.tms.threed.threedFramework.repo.shared.RootTreeId;
import com.tms.threed.threedFramework.repo.shared.RtConfig;
import com.tms.threed.threedFramework.repo.shared.SeriesNamesWithYears;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;

import java.util.ArrayList;

public interface ThreedAdminService2 extends RpcService {

    InitData getInitData();

    ArrayList<SeriesNamesWithYears> getSeriesNameWithYears();

    Stats getJpgGenFinalStats(JobId jobId);

    RootTreeId getVtcRootTreeId(SeriesKey seriesKey);

    void setVtcRootTreeId(SeriesKey seriesKey, RootTreeId rootTreeId);

    RtConfig getRepoConfig();
    void saveRtConfig(RtConfig config);

    CommitHistory getCommitHistory(SeriesKey seriesKey) throws RepoHasNoHeadException;

    CommitHistory tagCommit(SeriesKey seriesKey,String  newTagName,CommitId commitId);

    CommitHistory addAllAndCommit(SeriesKey seriesKey,String commitMessage,String tag) throws Exception;

    void purgeRepoCache();
}
