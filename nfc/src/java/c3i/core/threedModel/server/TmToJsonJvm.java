package c3i.core.threedModel.server;

import c3i.core.featureModel.server.FmToJsonJvm;
import c3i.imageModel.server.ImToJsonJvm;
import c3i.core.threedModel.shared.ThreedModel;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class TmToJsonJvm {

    private FmToJsonJvm fmMarshaller = new FmToJsonJvm();
    private ImToJsonJvm imMarshaller = new ImToJsonJvm();

    private static final JsonNodeFactory f = JsonNodeFactory.instance;

    public static String toJson(ThreedModel threedModel) {
        return toJson(threedModel, null);

    }

    public static String toJson(ThreedModel threedModel, String jsonpCallback) {
        String json = new TmToJsonJvm().toJsonString(threedModel);
        if (jsonpCallback!=null) {
            return jsonpDecorate(jsonpCallback,json);
        } else {
            return json;
        }

    }



    private static String jsonpDecorate(String callback,String json) {
        StringBuilder sb = new StringBuilder();
        sb.append(callback).append("(").append(json).append(");");
        return sb.toString();
    }

    public static ObjectNode toJsonObject(ThreedModel threedModel) {
        return new TmToJsonJvm().toJsonNode(threedModel);
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
