package com.tms.threed.threedFramework.featureModel.server;

import com.tms.threed.threedFramework.featureModel.data.Camry2011;
import com.tms.threed.threedFramework.featureModel.data.Trim;
import junit.framework.TestCase;
import org.codehaus.jackson.node.ObjectNode;

public class JsonMarshallerFmTest extends TestCase {

    public void test() throws Exception {
        Trim fm = new Camry2011();
        JsonMarshallerFm mapper = new JsonMarshallerFm();
        ObjectNode jsFeatureModel = mapper.jsonForFm(fm);

        System.out.println(jsFeatureModel.toString());
        System.out.println();
        JsonMarshallerFm.prettyPrint(jsFeatureModel);
    }

}

