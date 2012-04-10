package threed.jpgGen.server.taskManager;

import threed.repo.server.Repos;
import threed.core.threedModel.shared.JpgWidth;
import threed.repo.server.rt.RtRepo;
import threed.repo.server.SeriesRepo;
import threed.repo.server.SrcRepo;
import threed.core.threedModel.shared.RootTreeId;
import threed.core.threedModel.shared.SeriesId;
import threed.core.threedModel.shared.*;
import threed.core.threedModel.shared.SeriesKey;
import threed.core.threedModel.shared.Slice;

public class JpgVersionWidthSliceAction {

    protected final Repos repos;
    protected final SeriesId seriesId;
    protected final Slice slice;
    protected final JpgWidth jpgWidth;

    protected final SeriesKey seriesKey;
    protected final RootTreeId rootTreeId;
    protected final ThreedModel threedModel;

    protected final SeriesRepo seriesRepo;
    protected final SrcRepo srcRepo;
    protected final RtRepo genRepo;

    public JpgVersionWidthSliceAction(Repos repos, SeriesId seriesId, Slice slice, JpgWidth jpgWidth) {
        this.repos = repos;
        this.seriesId = seriesId;
        this.slice = slice;
        this.jpgWidth = jpgWidth;

        this.rootTreeId = seriesId.getRootTreeId();
        this.seriesKey = seriesId.getSeriesKey();


        this.seriesRepo = repos.getSeriesRepo(seriesKey);
        this.srcRepo = seriesRepo.getSrcRepo();
        this.genRepo = seriesRepo.getRtRepo();


        this.threedModel = repos.getThreedModel(seriesId);

    }

    public Slice getSlice() {
        return slice;
    }
}
