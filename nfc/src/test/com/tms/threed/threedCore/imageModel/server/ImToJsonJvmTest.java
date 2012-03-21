package com.tms.threed.threedCore.imageModel.server;

import com.tms.threed.threedCore.imageModel.shared.ImSeries;
import com.tms.threed.repo.server.Repos;

import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import junit.framework.TestCase;
import org.codehaus.jackson.node.ObjectNode;

public class ImToJsonJvmTest extends TestCase {

    Repos repos = Repos.get();

    public void test() throws Exception {

        ImSeries imSeries = getTestImageModel(); //slow

        ImToJsonJvm m2 = new ImToJsonJvm();

        ObjectNode jsonNode = m2.jsonForSeries(imSeries);


        System.out.println(jsonNode.toString());
//        JsonMarshaller.prettyPrint(s2);


    }


    ImSeries getTestImageModel() {
//        return repos.createModel(SeriesKey.TUNDRA_2011).getImageModel();

        return repos.getThreedModelForHead(SeriesKey.TACOMA_2011).getImageModel();
    }
}
