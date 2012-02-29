package com.tms.threed.threedAdmin.main.shared;

import com.google.gwt.rpc.client.RpcService;
import com.tms.threed.jpgGen.shared.JobId;
import com.tms.threed.jpgGen.shared.Stats;
import com.tms.threed.repo.shared.*;
import com.tms.threed.threedCore.threedModel.shared.RootTreeId;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;

/**
 * These are the ThreedAdminService remote calls that use gwt-rpc
 */
public interface ThreedAdminService extends RpcService {

    InitData getInitData();

    Stats getJpgGenFinalStats(JobId jobId);

    /**
     * Redundant with rest-json call: /configurator-content/avalon/2011/vtc.txt
     */
    RootTreeId getVtcRootTreeId(SeriesKey seriesKey);

    void setVtcRootTreeId(SeriesKey seriesKey, RootTreeId rootTreeId);

    RtConfig getRepoConfig();

    void saveRtConfig(RtConfig config);

    CommitHistory getCommitHistory(SeriesKey seriesKey) throws RepoHasNoHeadException;

    CommitHistory tagCommit(SeriesKey seriesKey, String newTagName, CommitId commitId);

    CommitHistory addAllAndCommit(SeriesKey seriesKey, String commitMessage, String tag) throws Exception;

    void purgeRepoCache();
}
