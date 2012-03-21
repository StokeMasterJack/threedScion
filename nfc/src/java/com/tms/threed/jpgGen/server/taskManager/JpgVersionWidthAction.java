package com.tms.threed.jpgGen.server.taskManager;

import com.tms.threed.repoService.server.rt.RtRepo;
import com.tms.threed.threedCore.threedModel.shared.*;
import com.tms.threed.repoService.server.Repos;
import com.tms.threed.repoService.server.SeriesRepo;
import com.tms.threed.repoService.server.SrcRepo;
import com.tms.threed.threedCore.threedModel.shared.RootTreeId;

public class JpgVersionWidthAction {

    protected final Repos repos;
    protected final SeriesId seriesId;
    protected final JpgWidth jpgWidth;

    protected final SeriesKey seriesKey;
    protected final RootTreeId rootTreeId;
    protected final ThreedModel threedModel;

    protected final SeriesRepo seriesRepo;
    protected final SrcRepo srcRepo;
    protected final RtRepo genRepo;

    public JpgVersionWidthAction(Repos repos, SeriesId seriesId, JpgWidth jpgWidth) {
        this.repos = repos;
        this.seriesId = seriesId;
        this.jpgWidth = jpgWidth;

        this.rootTreeId = seriesId.getRootTreeId();
        this.seriesKey = seriesId.getSeriesKey();


        this.seriesRepo = repos.getSeriesRepo(seriesKey);
        this.srcRepo = seriesRepo.getSrcRepo();
        this.genRepo = seriesRepo.getRtRepo();


        this.threedModel = repos.getThreedModel(seriesId);

    }


}
