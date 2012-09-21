package c3i.core.threedModel.server;

import c3i.core.common.shared.SeriesKey;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.repo.server.Repos;
import org.junit.Test;

public class TmToJsonJvmTest {

    @Test
    public void test_TmToJsonAvalon() throws Exception {
        Repos.setRepoBaseDir(TestConstants.REPO_BASE_DIR);
        Repos repos = Repos.get();
        SeriesKey sk = SeriesKey.AVALON_2011;
        ThreedModel threedModel = repos.getThreedModelForHead(sk);
        String threedModelJsonText = TmToJsonJvm.toJson(threedModel);
        System.err.println(threedModelJsonText);
    }

    @Test
       public void test_TmToJsonIq() throws Exception {
           Repos.setRepoBaseDir(TestConstants.REPO_BASE_DIR);
           Repos repos = Repos.get();
           SeriesKey sk = SeriesKey.IQ_2012;
           ThreedModel threedModel = repos.getThreedModelForHead(sk);
           String threedModelJsonText = TmToJsonJvm.toJson(threedModel);
           System.err.println(threedModelJsonText);
       }

//    public void test_generateJsonForAllSeriesKeys() throws Exception {
//        generateJsonForAllSeriesKeys();
//    }
//
//    public void generateJsonForAllSeriesKeys() throws Exception {
//        List<SeriesKey> seriesKeys = SeriesKey.getSeriesKeys();
//        for (SeriesKey seriesKey : seriesKeys) {
//            System.out.println("Generating JSON ThreedModel for [" + seriesKey + "]");
//            ThreedModel threedModel = repos.getThreedModelForHead(seriesKey);
//
//
////            createFile(new JsonMarshallerFm(), threedModel.getFeatureModel());
////            createFile(new JsonMarshallerIm(), threedModel.getImageModel());
////            createFile(new JsonMarshallerTm(), threedModel);
//
//
//        }
//
//    }
//
////    public <MT extends SeriesModel> void createFile(JsonMarshaller<MT> jsonMarshaller, MT model) throws Exception {
////        String jsonString = jsonMarshaller.toJsonString(model);
////        File dir = new File(getThreedModelsDir(), model.getModelType().getShortName());
////        File jsonFile = new File(dir, model.getSeriesKey().getShortName() + ".json");
////        Files.createParentDirs(jsonFile);
////        Files.write(jsonString, jsonFile, Charset.defaultCharset());
////    }
//
//
//    public static File getTempDir() {
//        return new File("/temp");
//    }
//
//    public static File getThreedModelsDir() {
//        return new File(getTempDir(), "threedModels");
//    }

}
