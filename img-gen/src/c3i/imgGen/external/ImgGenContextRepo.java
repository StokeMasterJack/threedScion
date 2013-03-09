package c3i.imgGen.external;

import c3i.core.common.shared.ProductHandler;
import c3i.core.common.shared.SeriesId;
import c3i.core.featureModel.shared.CspForTreeSearch;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.featureModel.shared.search.TreeSearch;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.imageModel.server.JsonToImJvm;
import c3i.imageModel.shared.ImageModel;
import c3i.imageModel.shared.SeriesKey;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.Repos;
import c3i.repo.server.SeriesRepo;
import com.google.common.io.InputSupplier;
import org.eclipse.jgit.lib.ObjectLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class ImgGenContextRepo implements ImgGenContext<CspForTreeSearch> {

    SeriesId seriesId;
    SeriesKey seriesKey2;

    c3i.core.common.shared.SeriesKey seriesKey1;

    private FeatureModel fm;

    private String imJson;

    private ImageModel imageModel;

    private BrandRepos brandRepos;
    private Repos repos;
    private SeriesRepo seriesRepo;


    public ImgGenContextRepo(BrandRepos brandRepos, SeriesId seriesId) {
        this.seriesId = seriesId;
        this.brandRepos = brandRepos;
        repos = brandRepos.getRepos(seriesKey1.getBrandKey());
        seriesRepo = repos.getSeriesRepo(seriesId.getSeriesKey());

        ThreedModel threedModel = seriesRepo.getThreedModel(seriesId.getRootTreeId());

        fm = threedModel.getFeatureModel();
        imageModel = threedModel.getImageModel();

    }

    @Override
    public SeriesId getSeriesId() {
        return seriesId;
    }

    @Override
    public String getKey() {
        return seriesId.serialize();
    }


    @Override
    public String getImageModelJson() {
        return imJson;
    }

    @Override
    public InputSupplier<? extends InputStream> getPng(final String pngShortSha) {
        return new InputSupplier<InputStream>() {
            @Override
            public InputStream getInput() throws IOException {
                ObjectLoader objectLoader = seriesRepo.getSrcPngByShortSha(pngShortSha);
                return objectLoader.openStream();
            }
        };
    }


    @Override
    public int getSliceCount() {
        return 0;
    }

    //    @Override
//    public byte[] getPng(String pngShortSha) {
//        return new byte[0];
//    }


    @Override
    public long getSatCount(Set<Object> pngVars) {
        Set<Var> vars = codeSetToVarSet(pngVars);
        CspForTreeSearch csp = fm.createCspForTreeSearch(vars);
        csp.propagateSimplify();
        TreeSearch treeSearch = new TreeSearch();
        treeSearch.start(csp);
        return treeSearch.getSolutionCount();
    }

    public ImageModel getImageModel() {
        if (this.imageModel == null) {
            this.imageModel = JsonToImJvm.parse(this, imJson);
        }
        return imageModel;
    }

    private Set<Var> codeSetToVarSet(Set<Object> pngVars) {
        HashSet<Var> vars = new HashSet<Var>();
        for (Object pngVar : pngVars) {
            Var var = (Var) pngVar;
            vars.add(var);
        }
        return vars;
    }

    @Override
    public void forEach(Set<Object> pngVarCodes, ProductHandler<CspForTreeSearch> productHandler) {
        Set<Var> pngVars = codeSetToVarSet(pngVarCodes);
        CspForTreeSearch csp = fm.createCspForTreeSearch(pngVars);
        csp.propagateSimplify();
        TreeSearch treeSearch = new TreeSearch(productHandler);
        treeSearch.start(csp);
    }

    @Override
    public Object getVar(String varCode) {
        return fm.getVarOrNull(varCode);
    }

    @Override
    public SeriesKey getSeriesKey() {
        return seriesKey2;
    }

    @Override
    public boolean containsVarCode(String varCode) {
        return fm.containsCode(varCode);
    }


}
