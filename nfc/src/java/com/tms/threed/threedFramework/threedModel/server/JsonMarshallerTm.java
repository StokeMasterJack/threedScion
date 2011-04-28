package com.tms.threed.threedFramework.threedModel.server;

import com.tms.threed.threedFramework.featureModel.server.JsonMarshallerFm;
import com.tms.threed.threedFramework.imageModel.server.JsonMarshallerIm;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class JsonMarshallerTm {

    private JsonMarshallerFm fmMarshaller = new JsonMarshallerFm();
    private JsonMarshallerIm imMarshaller = new JsonMarshallerIm();

    private static final JsonNodeFactory f = JsonNodeFactory.instance;

    public static String marshal(ThreedModel threedModel) {
        return new JsonMarshallerTm().toJsonString(threedModel);
    }

    public ObjectNode toJsonObject(ThreedModel threedModel) {
        return new JsonMarshallerTm().toJsonNode(threedModel);
    }

    public String toJsonString(ThreedModel model) {
        return toJsonObject(model).toString();
    }

    public ObjectNode toJsonNode(ThreedModel model) {
        ObjectNode jsThreedModel = f.objectNode();
        jsThreedModel.put("featureModel", fmMarshaller.jsonForFm(model.getFeatureModel()));
        jsThreedModel.put("imageModel", imMarshaller.jsonForSeries(model.getImageModel()));
        return jsThreedModel;
    }


}
