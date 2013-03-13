package c3i.imgGen.server.taskManager;

import c3i.core.threedModel.shared.ThreedModel;
import c3i.featureModel.shared.common.RootTreeId;
import c3i.featureModel.shared.common.SeriesId;
import c3i.featureModel.shared.common.SeriesKey;
import c3i.imageModel.shared.Profile;
import c3i.repo.server.Repos;
import c3i.repo.server.SeriesRepo;
import c3i.repo.server.SrcRepo;
import c3i.repo.server.rt.RtRepo;

public class JpgVersionWidthAction {

    protected final Repos repos;
    protected final SeriesId seriesId;
    protected final Profile profile;

    protected final SeriesKey seriesKey;
    protected final RootTreeId rootTreeId;
    protected final ThreedModel threedModel;

    protected final SeriesRepo seriesRepo;
    protected final SrcRepo srcRepo;
    protected final RtRepo genRepo;

    public JpgVersionWidthAction(Repos repos, SeriesId seriesId, Profile profile) {
        this.repos = repos;
        this.seriesId = seriesId;
        this.profile = profile;

        this.rootTreeId = seriesId.getRootTreeId();
        this.seriesKey = seriesId.getSeriesKey();


        this.seriesRepo = repos.getSeriesRepo(seriesKey);
        this.srcRepo = seriesRepo.getSrcRepo();
        this.genRepo = seriesRepo.getRtRepo();


        this.threedModel = repos.getThreedModel(seriesId);

    }


}
