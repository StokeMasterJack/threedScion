package threed.core.featureModel.shared.search;

import threed.core.featureModel.shared.CspForTreeSearch;
import threed.core.featureModel.shared.FeatureModel;
import threed.repo.server.Repos;

import threed.core.threedModel.shared.BrandKey;
import threed.core.threedModel.shared.ThreedModel;
import junit.framework.TestCase;

public class FindFirstTreeSearchTest extends TestCase {

    public void test1() throws Exception {
        ThreedModel threedModel = getVenza();
        FeatureModel fm = threedModel.getFeatureModel();

        CspForTreeSearch csp = fm.createCspForTreeSearch();
        assertTrue(FindFirstTreeSearch.hasAtLeastOneSolution(csp.copy()));

        csp.assignTrue("2810");
        csp.assignTrue("2812");

        assertFalse(FindFirstTreeSearch.hasAtLeastOneSolution(csp.copy()));
    }


    public ThreedModel getVenza() {
        Repos repos = Repos.get();
        return repos.getThreedModel(BrandKey.TOYOTA, "venza",2011);
    }
}
