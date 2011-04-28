package com.tms.threed.threedAdmin.main.shared;

import com.google.gwt.rpc.client.RpcService;
import com.tms.threed.threedFramework.jpgGen.shared.JobId;
import com.tms.threed.threedFramework.jpgGen.shared.Stats;
import com.tms.threed.threedFramework.repo.shared.RootTreeId;
import com.tms.threed.threedFramework.repo.shared.RtConfig;
import com.tms.threed.threedFramework.repo.shared.SeriesNamesWithYears;
import com.tms.threed.threedFramework.repo.shared.TagCommit;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;

import java.util.ArrayList;
import java.util.List;

public interface ThreedAdminService2 extends RpcService {

    InitData getInitData();

    ArrayList<SeriesNamesWithYears> getSeriesNameWithYears();

    Stats getJpgGenFinalStats(JobId jobId);

    RootTreeId getVtcRootTreeId(SeriesKey seriesKey);

    void setVtcRootTreeId(SeriesKey seriesKey, RootTreeId rootTreeId);

    RtConfig getRepoConfig();
    void saveRtConfig(RtConfig config);


    List<TagCommit> getTagCommits(SeriesKey seriesKey);



    void tagCurrentVersion(SeriesKey seriesKey,String tagName);
}
