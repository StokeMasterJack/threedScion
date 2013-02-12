package c3i.core.imageModel.test;

import c3i.core.common.shared.SeriesKey;
import c3i.core.imageModel.server.ImToJsonJvm;
import c3i.core.imageModel.shared.ImSeries;
import c3i.repo.server.Repos;
import junit.framework.TestCase;
import org.codehaus.jackson.node.ObjectNode;

public class ImToJsonJvmTest extends TestCase {

    Repos repos = Repos.get();

    public void test() throws Exception {


//        Repos repos1 = new Repos(BrandKey.TOYOTA, );
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
