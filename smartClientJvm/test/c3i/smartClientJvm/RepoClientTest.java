package c3i.smartClientJvm;

import c3i.core.threedModel.shared.ThreedModel;
import c3i.featureModel.shared.common.BrandKey;
import c3i.featureModel.shared.common.RootTreeId;
import c3i.featureModel.shared.common.SeriesId;
import c3i.featureModel.shared.common.SeriesKey;
import c3i.repo.server.Repos;
import org.junit.Test;
import smartsoft.util.shared.Path;

import java.net.URL;

import static junit.framework.Assert.assertEquals;

public class RepoClientTest {

    @Test
    public void test_JavaClient2() throws Exception {

        Path repoUrl = new Path("http://localhost:8080/configurator-content");
        RepoClient repoClient = new RepoClient(repoUrl);

        ThreedModel threedModel = repoClient.getCachedVtcThreedModel(SeriesKey.AVALON_2011);

    }

    @Test
    public void test_Unmarshaller() throws Exception {


        //read ThreedModel from JSON
//        URL url = Resources.getResource(this.getClass(), "avalon-threed-model.json");
        URL url = new URL("http://localhost:8080/configurator-content/avalon/2011/3d/models/2c05ba6f8d52e4ba85ae650756dc2d1423d9395d.json");
        JsonToTmJvm u = new JsonToTmJvm();
        ThreedModel threedModel1 = u.createModelFromJs(SeriesKey.AVALON_2011, url);

        //read from local repo using XML file - just to have something to compare against
        Repos repos = Repos.get();
        ThreedModel threedModel2 = repos.getThreedModel(BrandKey.TOYOTA, "avalon", 2011);

        assertEquals(threedModel1, threedModel2);


    }

    @Test
    public void test_JavaClient() throws Exception {

        Path repoUrl = new Path("http://localhost:8080/configurator-content");
        RepoClient repoClient = new RepoClient(repoUrl);


        SeriesId seriesId = new SeriesId(SeriesKey.AVALON_2011, new RootTreeId("2c05ba6f8d52e4ba85ae650756dc2d1423d9395d"));

        String actual = repoClient.getThreedModelUrl(seriesId).toString();

        String expected = "http://localhost:8080/configurator-content/avalon/2011/3d/models/2c05ba6f8d52e4ba85ae650756dc2d1423d9395d.json";
        assertEquals(expected, actual);


        ThreedModel threedModel = repoClient.getThreedModel(seriesId);

    }


}