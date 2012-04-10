package threed.core.imageModel.shared.slice;

import threed.core.featureModel.shared.FeatureModel;
import threed.core.featureModel.shared.boolExpr.Var;
import threed.core.imageModel.shared.ImSeries;

import threed.repo.server.Repos;
import threed.core.threedModel.shared.BrandKey;
import threed.core.threedModel.shared.ThreedModel;
import junit.framework.TestCase;

import java.util.Collection;

public class Test extends TestCase {

    public void test() throws Exception {
        Repos repos = Repos.get();
        String seriesName = "tundra";
        Integer seriesYear = 2011;
        ThreedModel threedModel = repos.getThreedModel(BrandKey.TOYOTA,seriesName,seriesYear);
        ImSeries im = threedModel.getImageModel();
        FeatureModel fm = threedModel.getFeatureModel();

        System.out.println("Full: " + im.summary());


        ImageSlice ime2 = im.simplify("exterior", 2);
        System.out.println("e2: " + ime2.summary());


        //"Base","Regular","Standard","V6","2WD","5AT","202"
        Collection<Var> exclusions = fm.getXorExclusions("Base","Regular","Standard","V6","2WD","5AT","202");




    }

     public void test2() throws Exception {
        Repos repos = Repos.get();
        String seriesName = "tundra";
        ThreedModel threedModel = repos.getThreedModel(BrandKey.TOYOTA,seriesName,2011);
        ImSeries im = threedModel.getImageModel();
        FeatureModel fm = threedModel.getFeatureModel();



//        ImageModel ime2 = im.simplify("exterior", 2);
//        System.out.println("e2: " + ime2.summary());
//
//
//        //"Base","Regular","Standard","V6","2WD","5AT","202"
//        Collection<Var> exclusions = fm.getXorExclusions("Base","Regular","Standard","V6","2WD","5AT","202");
//
//
//        ImageModel ime2_3554 = ime2.simplify(exclusions);
//        System.out.println("ime2_3554: " + ime2_3554.summary());
//

         System.out.println(fm.getIffVarsForVar(fm.get("8202")));

    }


}
