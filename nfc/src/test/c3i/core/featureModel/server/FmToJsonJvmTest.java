package c3i.core.featureModel.server;

import c3i.core.featureModel.data.Camry2011;
import c3i.core.featureModel.data.Trim;
import junit.framework.TestCase;
import org.codehaus.jackson.node.ObjectNode;

public class FmToJsonJvmTest extends TestCase {

    public void test() throws Exception {
        Trim fm = new Camry2011();
        FmToJsonJvm mapper = new FmToJsonJvm();
        ObjectNode jsFeatureModel = mapper.jsonForFm(fm);

        System.out.println(jsFeatureModel.toString());
        System.out.println();
        FmToJsonJvm.prettyPrint(jsFeatureModel);
    }

}

