package c3i.core.imageModel.shared.slice;

import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.imageModel.shared.ImSeries;

import c3i.core.imageModel.shared.ViewSlice;
import c3i.repo.server.Repos;
import c3i.core.common.shared.BrandKey;
import c3i.core.threedModel.shared.ThreedModel;
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


        ViewSlice ime2 = im.getViewSlice("exterior", 2);
//        System.out.println("e2: " + ime2.summary());

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
