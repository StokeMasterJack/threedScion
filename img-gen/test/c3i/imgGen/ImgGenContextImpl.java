package c3i.imgGen;

import c3i.core.common.shared.SeriesId;
import c3i.core.featureModel.server.JsonToFmJvm;
import c3i.core.featureModel.shared.CspForTreeSearch;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.featureModel.shared.search.TreeSearch;
import c3i.core.threedModel.shared.ImFeatureModel;
import c3i.imageModel.shared.SeriesKey;
import c3i.imageModel.shared.SimplePicks;
import c3i.imgGen.external.ImgGenContext;
import c3i.imgGen.external.ProductHandlerSimple;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

public class ImgGenContextImpl implements ImgGenContext {

    SeriesId seriesId;
    SeriesKey seriesKey2;

    c3i.core.common.shared.SeriesKey seriesKey1;

    private FeatureModel fm;

    private String imJson;


    public ImgGenContextImpl(SeriesId seriesId) {
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
    public byte[] getPng(String pngShortSha) {
        return new byte[0];
    }

    private class ProductHandlerAdapter implements c3i.core.featureModel.shared.search.ProductHandler {

        private final ProductHandlerSimple delegate;

        public ProductHandlerAdapter(ProductHandlerSimple delegate) {
            this.delegate = delegate;
        }

        @Override
        public void onProduct(CspForTreeSearch csp) {
            SimplePicks simplePicks = ImFeatureModel.toSimplePicks(csp.getAssignments());
            delegate.onProduct(simplePicks);
        }
    }


    @Override
    public long getSatCount(Set<Object> pngVars) {
        Set<Var> vars = codeSetToVarSet(pngVars);
        CspForTreeSearch csp = fm.createCspForTreeSearch(vars);
        csp.propagateSimplify();
        TreeSearch treeSearch = new TreeSearch();
        treeSearch.start(csp);
        return treeSearch.getSolutionCount();
    }

    @Override
    public void forEach(Set<Object> outVars, ProductHandlerSimple productHandler) {
        ProductHandlerAdapter ph = new ProductHandlerAdapter(productHandler);
        forEach2(outVars, ph);
    }

    private Set<Var> codeSetToVarSet(Set<Object> pngVars) {
        HashSet<Var> vars = new HashSet<Var>();
        for (Object pngVar : pngVars) {
            Var var = (Var) pngVar;
            vars.add(var);
        }
        return vars;
    }


    private void forEach2(Set<Object> pngVarCodes, c3i.core.featureModel.shared.search.ProductHandler productHandler) {
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
