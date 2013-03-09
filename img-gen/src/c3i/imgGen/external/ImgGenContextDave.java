package c3i.imgGen.external;

import c3i.core.common.shared.ProductHandler;
import c3i.core.common.shared.SeriesId;
import c3i.core.featureModel.server.JsonToFmJvm;
import c3i.core.featureModel.shared.CspForTreeSearch;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.featureModel.shared.search.TreeSearch;
import c3i.imageModel.server.JsonToImJvm;
import c3i.imageModel.shared.ImageModel;
import c3i.imageModel.shared.SeriesKey;
import c3i.repo.server.SeriesRepo;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;
import org.eclipse.jgit.lib.ObjectLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

public class ImgGenContextDave implements ImgGenContext<CspForTreeSearch> {

    SeriesId seriesId;
    SeriesKey seriesKey2;

    c3i.core.common.shared.SeriesKey seriesKey1;

    private FeatureModel fm;

    private String imJson;

    private ImageModel imageModel;

    private SeriesRepo seriesRepo;


    public ImgGenContextDave(SeriesId seriesId, SeriesRepo seriesRepo) {

        this.seriesId = seriesId;
        seriesKey1 = seriesId.getSeriesKey();
        String brand = seriesKey1.getBrandKey().toString();
        seriesKey2 = new SeriesKey(brand, seriesKey1.getYear(), seriesKey1.getName());


        JsonToFmJvm fmParser = new JsonToFmJvm();

        URL urlFm = Resources.getResource(this.getClass(), "avalon-fm.json");
        fm = fmParser.parseJson(seriesKey1, urlFm);


        URL urlIm = Resources.getResource(this.getClass(), "avalon-im.json");
        try {
            imJson = Resources.toString(urlIm, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


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


    public void test2() throws Exception {
//        @Override
//        public void onProduct(Assignments product) {
//            RawBaseImage rawBaseImage = view.getPngSegments(toSimplePicks(product), key.getAngle());
//            set.add(rawBaseImage);
//        }
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
