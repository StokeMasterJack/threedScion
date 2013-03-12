package c3i.imgGen.repoImpl;

import c3i.core.common.shared.ProductHandler;
import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.shared.CspForTreeSearch;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.featureModel.shared.search.TreeSearch;
import c3i.imageModel.shared.ImageModel;
import c3i.repo.server.SeriesRepo;

import java.util.Set;

//<CspForTreeSearch, ID, V>
//<SeriesKey,Var>
public class ImgGenContextRepo implements ImgGenContext<CspForTreeSearch, SeriesId, Var> {

    private final FmIm fmIm;

    private SeriesRepo seriesRepo;

    private final SeriesId seriesId;
    private final FeatureModel featureModel;
    private final ImageModel imageModel;

    public ImgGenContextRepo(SeriesId seriesId, FmIm<SeriesId> fmIm) {
        this.seriesId = seriesId;
        this.fmIm = fmIm;
    }


    @Override
    public SeriesId getSeriesId() {
        return seriesId;
    }


    @Override
    public int getSliceCount() {
        return 0;
    }

    @Override
    public long getSatCount(Set<Var> outVars) {
        CspForTreeSearch csp = featureModel.createCspForTreeSearch(outVars);
        csp.propagateSimplify();
        TreeSearch treeSearch = new TreeSearch();
        treeSearch.start(csp);
        return treeSearch.getSolutionCount();
    }

    public ImageModel getImageModel() {
        return imageModel;
    }


    @Override
    public void forEach(Set<Var> outVars, ProductHandler<CspForTreeSearch> productHandler) {
        CspForTreeSearch csp = featureModel.createCspForTreeSearch(outVars);
        csp.propagateSimplify();
        TreeSearch treeSearch = new TreeSearch(productHandler);
        treeSearch.start(csp);
    }


    @Override
    public Var getVar(String varCode) {
        return featureModel.getVarOrNull(varCode);
    }

    @Override
    public SeriesKey getSeriesKey() {
        return seriesId.getSeriesKey();
    }

    @Override
    public boolean containsVarCode(String varCode) {
        return featureModel.containsCode(varCode);
    }


}
