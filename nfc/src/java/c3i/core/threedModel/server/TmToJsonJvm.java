package c3i.core.threedModel.server;

import c3i.core.featureModel.server.FmToJsonJvm;
import c3i.core.imageModel.server.ImToJsonJvm;
import c3i.core.threedModel.shared.ThreedModel;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class TmToJsonJvm {

    private FmToJsonJvm fmMarshaller = new FmToJsonJvm();
    private ImToJsonJvm imMarshaller = new ImToJsonJvm();

    private static final JsonNodeFactory f = JsonNodeFactory.instance;

    public static String toJson(ThreedModel threedModel) {
        return toJson(threedModel, false);

    }

    public static String toJson(ThreedModel threedModel, boolean jsonp) {
        String json = new TmToJsonJvm().toJsonString(threedModel);
        if (jsonp) {
            return jsonpDecorate(json);
        } else {
            return json;
        }

    }

    private static String jsonpDecorate(String json) {
        StringBuilder sb = new StringBuilder();
        sb.append("var jsonpThreedModel = " + json + ";");
        sb.append("if(window.onThreedModel) window.onThreedModel(jsonpThreedModel);");
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
