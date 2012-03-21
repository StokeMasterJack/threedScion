package com.tms.threed.threedCore.imageModel.shared.slice;

import com.tms.threed.threedCore.featureModel.shared.FeatureModel;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedCore.imageModel.shared.ImSeries;

import com.tms.threed.repoService.server.Repos;
import com.tms.threed.threedCore.threedModel.shared.ThreedModel;
import junit.framework.TestCase;

import java.util.Collection;

public class Test extends TestCase {

    public void test() throws Exception {
        Repos repos = Repos.get();
        String seriesName = "tundra";
        Integer seriesYear = 2011;
        ThreedModel threedModel = repos.getThreedModel(seriesName,seriesYear);
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
        ThreedModel threedModel = repos.getThreedModel(seriesName,2011);
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
