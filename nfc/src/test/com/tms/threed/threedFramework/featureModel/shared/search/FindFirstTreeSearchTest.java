package com.tms.threed.threedFramework.featureModel.shared.search;

import com.tms.threed.threedFramework.featureModel.shared.CspForTreeSearch;
import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.threedCore.config.ThreedConfig;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
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
        Repos repos = ThreedConfig.getRepos();
        return repos.getThreedModel("venza",2011);
    }
}
