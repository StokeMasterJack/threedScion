package com.tms.threed.threedModelBuilders;

import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.threedCore.config.ThreedConfig;
import com.tms.threed.threedFramework.threedModel.server.JsonMarshallerTm;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import junit.framework.TestCase;

public class JsonTest extends TestCase {

    public void test() throws Exception {


        Repos repos = ThreedConfig.getRepos();

        ThreedModel threedModel = repos.getThreedModel("avalon",2011);

        JsonMarshallerTm marshaller = new JsonMarshallerTm();

        String json = marshaller.toJsonString(threedModel);

        System.out.println(json);


    }
}
