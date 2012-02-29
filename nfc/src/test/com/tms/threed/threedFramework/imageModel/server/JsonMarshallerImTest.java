package com.tms.threed.threedFramework.imageModel.server;

import com.tms.threed.threedFramework.imageModel.shared.ImSeries;
import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.threedModel.server.TestHelper;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;
import junit.framework.TestCase;
import org.codehaus.jackson.node.ObjectNode;

public class JsonMarshallerImTest extends TestCase {

    Repos repos = TestHelper.getRepos();

    public void test() throws Exception {

        ImSeries imSeries = getTestImageModel(); //slow

        JsonMarshallerIm m2 = new JsonMarshallerIm();

        ObjectNode jsonNode = m2.jsonForSeries(imSeries);


        System.out.println(jsonNode.toString());
//        JsonMarshaller.prettyPrint(s2);


    }


    ImSeries getTestImageModel() {
//        return repos.createModel(SeriesKey.TUNDRA_2011).getImageModel();

        return repos.getThreedModelForHead(SeriesKey.TACOMA_2011).getImageModel();
    }
}
