package c3i.imageModel.test;

import c3i.featureModel.shared.common.SeriesKey;
import c3i.imageModel.server.ImToJsonJvm;
import c3i.imageModel.shared.ImageModel;
import c3i.repo.server.Repos;
import junit.framework.TestCase;
import org.codehaus.jackson.node.ObjectNode;

public class ImToJsonJvmTest extends TestCase {

    Repos repos = Repos.get();

    public void test() throws Exception {


//        Repos repos1 = new Repos(BrandKey.TOYOTA, );
        ImageModel imageModel = getTestImageModel(); //slow

        ImToJsonJvm m2 = new ImToJsonJvm();

        ObjectNode jsonNode = m2.jsonForSeries(imageModel);


        System.out.println(jsonNode.toString());
//        JsonMarshaller.prettyPrint(s2);


    }


    ImageModel getTestImageModel() {
//        return repos.createModel(SeriesKey.TUNDRA_2011).getImageModel();

        return repos.getThreedModelForHead(SeriesKey.TACOMA_2011).getImageModel();
    }
}
