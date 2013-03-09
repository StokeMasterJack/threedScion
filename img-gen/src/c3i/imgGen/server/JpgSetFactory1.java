package c3i.imgGen.server;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.ProductHandler;
import c3i.core.featureModel.shared.AssignmentsForTreeSearch;
import c3i.core.featureModel.shared.CspForTreeSearch;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.featureModel.shared.search.TreeSearch;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.RawBaseImage;
import c3i.imageModel.shared.SimplePicks;
import c3i.imgGen.external.ImgGenContextFactory;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.Repos;

import java.util.HashSet;
import java.util.Set;

import static c3i.core.threedModel.shared.ImFeatureModel.toSimplePicks;

public class JpgSetFactory1 extends JpgSetFactory {

    public JpgSetFactory1(BrandRepos brandRepos, ImgGenContextFactory imgGenContextFactory) {
        super(brandRepos, imgGenContextFactory);
    }

    @Override
    protected JpgSet createJpgSet(JpgSetKey key) {
        BrandKey brandKey = key.getSeriesId().getBrandKey();
        Repos repos = brandRepos.getRepos(brandKey);
        ThreedModel threedModel = repos.getThreedModel(key.getSeriesId());
        return createJpgSet2(threedModel, key);
    }

    private static JpgSet createJpgSet2(ThreedModel threedModel, final JpgSetKey key) {

        FeatureModel fm = threedModel.getFeatureModel();
        final ImView view = threedModel.getView(key.getView());
        Set<Object> pngVars = view.getPngVars(key.getAngle());

        Set<Var> pngVarsAsVar = ThreedModel.objectSetToVarSet(pngVars);

        final CspForTreeSearch csp = fm.createCspForTreeSearch(pngVarsAsVar);
        csp.propagateSimplify();
        final TreeSearch treeSearchAllSat = new TreeSearch();

        final HashSet<RawBaseImage> set = new HashSet<RawBaseImage>();

        csp.forEach(new ProductHandler<CspForTreeSearch>() {

            @Override
            public void onProduct(CspForTreeSearch csp) {
                AssignmentsForTreeSearch assignments = csp.getAssignments();
                SimplePicks product = toSimplePicks(assignments);
                RawBaseImage rawBaseImage = view.getPngSegments(product, key.getAngle());
                set.add(rawBaseImage);
            }
        });

        treeSearchAllSat.start(csp);

        return new JpgSet(set);
    }

}
