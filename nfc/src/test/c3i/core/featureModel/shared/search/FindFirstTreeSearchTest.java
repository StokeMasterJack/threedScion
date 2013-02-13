package c3i.core.featureModel.shared.search;

import c3i.core.common.shared.BrandKey;
import c3i.core.featureModel.shared.CspForTreeSearch;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.repo.server.Repos;
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
        return repos.getThreedModel(BrandKey.TOYOTA, "venza", 2011);
    }
}
