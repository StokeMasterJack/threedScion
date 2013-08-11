package c3i.core.featureModel.shared.picks;

import c3i.core.featureModel.data.SampleFeatureSet;
import c3i.core.featureModel.shared.FeatureModel;
//import com.tms.threed.featureModel.shared.ProposePickResponse;
import c3i.repo.server.Repos;

import c3i.core.common.shared.SeriesKey;
import junit.framework.TestCase;

/**
 *  Venza
 *
 *  modelCode[2810] exteriorColor[1F7] interiorColor[FA12]
 */
public class PicksTest extends TestCase {



     public void testFixupVenza() throws Exception {
         Repos repos = Repos.getToyotaRepoTest();

        FeatureModel fm = repos.getFeatureModel(SeriesKey.VENZA_2011);
        Picks picks = fm.createPicks(new SampleFeatureSet("2810,1F7,FA12"));

        System.out.println("Current Picks:");
        System.out.println("\tRaw: " + picks);
        picks.fixup();
        System.out.println("\tFixed: " + picks);



    }

    public void testPicksTester() throws Exception {

//        FeatureModel fm = repos.getFeatureModel(SeriesKey.AVALON_2011);
//        Picks picks = fm.getInitialVisiblePicksForTestHarness();
//
//        System.out.println("Current Picks:");
//        System.out.println("\tRaw: " + picks);
//        picks.fixup();
//        System.out.println("\tFixed: " + picks);
//
//        System.out.println("---");
//
////        if(true) return;
//        PickTester t = new PickTester();
//
//        Var proposedPick = fm.get("LL02");
//        System.out.println("proposedPick = [" + proposedPick + "=true]");
//
//        ProposePickResponse response = t.proposePick(picks, proposedPick, true);
////
//        System.out.println("\t " + (response.valid ? "Valid" : "Invalid"));
//

    }

}
