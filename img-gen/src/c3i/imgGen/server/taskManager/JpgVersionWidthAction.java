package c3i.imgGen.server.taskManager;

import c3i.threedModel.shared.ThreedModel;
import c3i.featureModel.shared.common.RootTreeId;
import c3i.featureModel.shared.common.SeriesId;
import c3i.featureModel.shared.common.SeriesKey;
import c3i.imageModel.shared.Profile;
import c3i.repo.server.BrandRepo;
import c3i.repo.server.SeriesRepo;
import c3i.repo.server.SrcRepo;
import c3i.repo.server.rt.RtRepo;

public class JpgVersionWidthAction {

    protected final BrandRepo brandRepo;
    protected final SeriesId seriesId;
    protected final Profile profile;

    protected final SeriesKey seriesKey;
    protected final RootTreeId rootTreeId;
    protected final ThreedModel threedModel;

    protected final SeriesRepo seriesRepo;
    protected final SrcRepo srcRepo;
    protected final RtRepo genRepo;

    public JpgVersionWidthAction(BrandRepo brandRepo, SeriesId seriesId, Profile profile) {
        this.brandRepo = brandRepo;
        this.seriesId = seriesId;
        this.profile = profile;

        this.rootTreeId = seriesId.getRootTreeId();
        this.seriesKey = seriesId.getSeriesKey();


        this.seriesRepo = brandRepo.getSeriesRepo(seriesKey);
        this.srcRepo = seriesRepo.getSrcRepo();
        this.genRepo = seriesRepo.getRtRepo();


        this.threedModel = brandRepo.getThreedModel(seriesId);

    }


}
