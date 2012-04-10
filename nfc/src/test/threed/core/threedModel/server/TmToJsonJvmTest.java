package threed.core.threedModel.server;

import threed.repo.server.Repos;
import threed.core.threedModel.shared.SeriesKey;
import threed.core.threedModel.shared.ThreedModel;
import junit.framework.TestCase;

import java.io.File;
import java.util.List;

public class TmToJsonJvmTest extends TestCase {

    Repos repos = Repos.get();

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
