package com.tms.threed.threedFramework.repoClient;

import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.repo.shared.RootTreeId;
import com.tms.threed.threedFramework.threedCore.config.ThreedConfig;
import com.tms.threed.threedFramework.threedCore.shared.SeriesId;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import com.tms.threed.threedFramework.util.lang.shared.Path;
import org.junit.Test;

import java.net.URL;

import static junit.framework.Assert.assertEquals;

public class RepoClientTest {

     @Test public void test_JavaClient2() throws Exception {

        Path repoUrl = new Path("http://localhost:8080/configurator-content");
        RepoClient repoClient = new RepoClient(repoUrl);

        ThreedModel threedModel = repoClient.getCachedVtcThreedModel(SeriesKey.AVALON_2011);

    }

    @Test public void test_Unmarshaller() throws Exception {


        //read ThreedModel from JSON
//        URL url = Resources.getResource(this.getClass(), "avalon-threed-model.json");
        URL url = new URL("http://localhost:8080/configurator-content/avalon/2011/3d/models/2c05ba6f8d52e4ba85ae650756dc2d1423d9395d.json");
        JsonUnmarshallerTm u = new JsonUnmarshallerTm();
        ThreedModel threedModel1 = u.createModelFromJs(SeriesKey.AVALON_2011, url);

        //read from local repo using XML file - just to have something to compare against
        Repos repos = ThreedConfig.getRepos();
        ThreedModel threedModel2 = repos.getThreedModel("avalon", 2011);

        assertEquals(threedModel1, threedModel2);


    }

    @Test public void test_JavaClient() throws Exception {

        Path repoUrl = new Path("http://localhost:8080/configurator-content");
        RepoClient repoClient = new RepoClient(repoUrl);


        SeriesId seriesId = new SeriesId(SeriesKey.AVALON_2011, new RootTreeId("2c05ba6f8d52e4ba85ae650756dc2d1423d9395d"));

        String actual = repoClient.getThreedModelUrl(seriesId).toString();

        String expected = "http://localhost:8080/configurator-content/avalon/2011/3d/models/2c05ba6f8d52e4ba85ae650756dc2d1423d9395d.json";
        assertEquals(expected, actual);


        ThreedModel threedModel = repoClient.getThreedModel(seriesId);

    }


}