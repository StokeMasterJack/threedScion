package c3i.core.imageModel.server;

import c3i.core.imageModel.shared.ImFeature;
import c3i.core.imageModel.shared.ImFeatureOrPng;
import c3i.core.imageModel.shared.ImLayer;
import c3i.core.imageModel.shared.ImSeries;
import c3i.core.imageModel.shared.ImView;
import c3i.core.imageModel.shared.SrcPng;
import c3i.core.imageModel.shared.ViewLiftSpec;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Stateless
 */
public class ImToJsonJvm {

    private static final JsonNodeFactory f = JsonNodeFactory.instance;

    public ObjectNode jsonForSeries(ImSeries series) {
        ObjectNode jsSeries = f.objectNode();
        ArrayNode jsView = toJson(series.getViews());
        jsSeries.put("views", jsView);
        return jsSeries;
    }

    private ArrayNode toJson(List<ImView> imViews) {
        ArrayNode a = f.arrayNode();
        for (ImView imView : imViews) {
            a.add(toJson(imView));
        }
        return a;
    }

    private JsonNode toJson(ImView imView) {
        ObjectNode n = f.objectNode();

        n.put("name", imView.getName());


        ViewLiftSpec liftSpec = imView.getLiftSpec();
        if (liftSpec != null) {
            n.put("lift", toJson(liftSpec));
        }

        n.put("layers", this.toJson(imView.getLayers()));
        return n;
    }

    private JsonNode toJson(ViewLiftSpec liftSpec) {
        ObjectNode n = f.objectNode();
        n.put("triggerFeature", liftSpec.getTriggerFeature().getCode());
        n.put("deltaY", liftSpec.getDeltaY());
        return n;
    }

    private JsonNode toJson(List<ImLayer> imLayers) {
        ArrayNode n = f.arrayNode();
        for (ImLayer imLayer : imLayers) {
            n.add(toJson(imLayer));
        }
        return n;
    }

    private JsonNode toJson(ImLayer imLayer) {
        ObjectNode n = f.objectNode();
        n.put("name",imLayer.getName());
        n.put("children", jsonForFeaturesOrPngs(imLayer.getChildNodes()));
        n.put("lift",imLayer.isLiftLayer());
        return n;
    }

    private JsonNode jsonForFeaturesOrPngs(List<ImFeatureOrPng> fps) {
        ArrayNode n = f.arrayNode();
        for (ImFeatureOrPng fp : fps) {
            n.add(jsonForFeatureOrPng(fp));
        }
        return n;
    }

    private JsonNode jsonForFeatureOrPng(ImFeatureOrPng fp) {
        if (fp.isFeature()) {
            return jsonForFeature((ImFeature) fp);
        } else if (fp.isPng()) {
            return jsonForPng((SrcPng) fp);
        } else {
            throw new IllegalStateException();
        }
    }

    private JsonNode jsonForFeature(ImFeature imFeature) {
        ObjectNode n = f.objectNode();
        n.put(imFeature.getName(), jsonForFeaturesOrPngs(imFeature.getChildNodes()));
        return n;
    }

    private JsonNode jsonForPng(SrcPng imPng) {

        ArrayNode a = f.arrayNode();

        a.add(imPng.getAngle());
        a.add(imPng.getShortSha());


        if (imPng.isBlink()) {
            a.add("blink");
        }
        return a;
    }

    public static void prettyPrint(JsonNode jsonNode) throws IOException {
        PrintWriter out = new PrintWriter(System.out);
        JsonFactory f = new MappingJsonFactory();
        JsonGenerator g = f.createJsonGenerator(out);
        g.useDefaultPrettyPrinter();
        g.writeObject(jsonNode);
    }


}