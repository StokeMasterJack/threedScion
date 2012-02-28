package com.tms.threed.threedFramework.threedModel.server;

import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import junit.framework.TestCase;

import java.io.File;
import java.util.List;

public class JsonMarshallerTest extends TestCase {

    Repos repos = ThreedConfig.getRepos();

    public void test_generateJsonForAllSeriesKeys() throws Exception {
        generateJsonForAllSeriesKeys();
    }

    public void generateJsonForAllSeriesKeys() throws Exception {
        List<SeriesKey> seriesKeys = SeriesKey.getSeriesKeys();
        for (SeriesKey seriesKey : seriesKeys) {
            System.out.println("Generating JSON ThreedModel for [" + seriesKey + "]");
            ThreedModel threedModel = repos.getThreedModelForHead(seriesKey);


//            createFile(new JsonMarshallerFm(), threedModel.getFeatureModel());
//            createFile(new JsonMarshallerIm(), threedModel.getImageModel());
//            createFile(new JsonMarshallerTm(), threedModel);


        }

    }

//    public <MT extends SeriesModel> void createFile(JsonMarshaller<MT> jsonMarshaller, MT model) throws Exception {
//        String jsonString = jsonMarshaller.toJsonString(model);
//        File dir = new File(getThreedModelsDir(), model.getModelType().getShortName());
//        File jsonFile = new File(dir, model.getSeriesKey().getShortName() + ".json");
//        Files.createParentDirs(jsonFile);
//        Files.write(jsonString, jsonFile, Charset.defaultCharset());
//    }


    public static File getTempDir() {
        return new File("/temp");
    }

    public static File getThreedModelsDir() {
        return new File(getTempDir(), "threedModels");
    }

}
