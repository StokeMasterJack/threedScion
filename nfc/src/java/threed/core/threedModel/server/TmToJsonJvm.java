package threed.core.threedModel.server;

import threed.core.featureModel.server.FmToJsonJvm;
import threed.core.imageModel.server.ImToJsonJvm;
import threed.core.threedModel.shared.ThreedModel;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class TmToJsonJvm {

    private FmToJsonJvm fmMarshaller = new FmToJsonJvm();
    private ImToJsonJvm imMarshaller = new ImToJsonJvm();

    private static final JsonNodeFactory f = JsonNodeFactory.instance;

    public static String marshal(ThreedModel threedModel) {
        return new TmToJsonJvm().toJsonString(threedModel);
    }

    public ObjectNode toJsonObject(ThreedModel threedModel) {
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
