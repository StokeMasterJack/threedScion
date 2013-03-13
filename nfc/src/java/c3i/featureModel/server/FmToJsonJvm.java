package c3i.featureModel.server;

import c3i.featureModel.shared.Cardinality;
import c3i.featureModel.shared.FeatureModel;
import c3i.featureModel.shared.boolExpr.BoolExpr;
import c3i.featureModel.shared.boolExpr.Var;
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

import static smartsoft.util.shared.Strings.isEmpty;

/**
 * stateless
 */
public class FmToJsonJvm {

    private JsonNodeFactory f = JsonNodeFactory.instance;

    private boolean slimMode = false;


    public ObjectNode jsonForFm(FeatureModel fm) {
        ObjectNode jsRootVar = mapVar(fm.getRootVar());
        BoolExpr ec = fm.getExtraConstraint();
        ObjectNode jsExtraConstraints = mapConstraint(ec);

        ObjectNode jsFeatureModel = f.objectNode();
        jsFeatureModel.put("displayName", fm.getDisplayName());
        jsFeatureModel.put("brand", fm.getSeriesKey().getBrandKey().getKey());
        jsFeatureModel.put("name", fm.getSeriesKey().getSeriesName());
        jsFeatureModel.put("year", fm.getSeriesKey().getYear());
        jsFeatureModel.put("rootVar", jsRootVar);
        jsFeatureModel.put("constraints", jsExtraConstraints);

        return jsFeatureModel;
    }

    private ObjectNode mapConstraint(BoolExpr expr) {
        ObjectNode jsExpr = f.objectNode();
        jsExpr.put("t", expr.getType().id);

        if (expr.isVar()) {
            jsExpr.put("code", expr.asVar().getCode());
        } else {
            ArrayNode jsExpressions = f.arrayNode();
            for (BoolExpr childExpr : expr.getExpressions()) {
                jsExpressions.add(mapConstraint(childExpr));
            }
            jsExpr.put("e", jsExpressions);
        }

        return jsExpr;
    }

    private ObjectNode mapVar(Var var) {
        ObjectNode jsVar = f.objectNode();

        String varCode = var.getCode();
        jsVar.put("c", varCode);


        String varName = var.getName();
        if (!isEmpty(varName) && !varName.equals(varCode) && !slimMode) {
            jsVar.put("n", varName);
        }

        Cardinality cardinality = var.getCardinality();
        if (cardinality != null) {
            jsVar.put("card", var.getCardinality().name());
        }

        Boolean derived = var.getDerived();
        if (derived != null) {
            jsVar.put("d", derived);
        }

        Boolean defaultValue = var.getDefaultValue();
        if (defaultValue != null) {
            jsVar.put("dv", defaultValue);
        }

        Boolean mandatory = var.getMandatory();
        if (mandatory != null && mandatory) {
            jsVar.put("m", true);
        }

        List<Var> childVars = var.getChildVars();
        if (childVars != null) {
            ArrayNode jsChildVars = f.arrayNode();
            for (Var childVar : childVars) {
                ObjectNode jsChildVar = mapVar(childVar);
                jsChildVars.add(jsChildVar);
            }
            jsVar.put("childNodes", jsChildVars);
        }
        return jsVar;
    }

    public static void prettyPrint(JsonNode jsonNode) throws IOException {
        PrintWriter out = new PrintWriter(System.out);
        JsonFactory f = new MappingJsonFactory();
        JsonGenerator g = f.createJsonGenerator(out);
        g.useDefaultPrettyPrinter();
        g.writeObject(jsonNode);
    }

}