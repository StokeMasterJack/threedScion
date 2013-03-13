package c3i.core.featureModel.server;

import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.shared.Cardinality;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.boolExpr.And;
import c3i.core.featureModel.shared.boolExpr.BoolExpr;
import c3i.core.featureModel.shared.boolExpr.Type;
import c3i.core.featureModel.shared.boolExpr.Var;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashSet;

public final class JsonToFmJvm {

    private FeatureModel fm;

    public FeatureModel parseJson(SeriesKey seriesKey, JsonNode rootNode) throws IOException {

        String displayName = rootNode.get("displayName").getTextValue();
        String name = rootNode.get("name").getTextValue();

        assert name.equals(seriesKey.getSeries());

        fm = new FeatureModel(seriesKey, displayName);

        ObjectNode rootVarNode = (ObjectNode) rootNode.get("rootVar");
        ObjectNode rootConstraintNode = (ObjectNode) rootNode.get("constraints");

        mapVars(rootVarNode);
        mapConstraints(rootConstraintNode);

        return fm;

    }

    public FeatureModel parseJson(SeriesKey seriesKey, URL url) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readValue(url, JsonNode.class);
            return parseJson(seriesKey, rootNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void mapVars(ObjectNode jsRootVar) {
        mapVar(null, jsRootVar);
    }

    private void mapVar(Var parentVar, ObjectNode jsChildVar) {

        Var childVar;
        if (parentVar == null) {
            childVar = fm.getRootVar();
        } else {
            JsonNode jsName = jsChildVar.get("n");
            String varName;
            if (jsName != null) {
                varName = jsName.getTextValue();
            } else {
                varName = null;
            }
            childVar = parentVar.addChild(jsChildVar.get("c").getTextValue(), varName);
        }

        JsonNode jsCardinality = jsChildVar.get("card");
        if (jsCardinality != null) {
            Cardinality cardinality = Cardinality.valueOf(jsCardinality.getTextValue());
            childVar.setCardinality(cardinality);
        }


        JsonNode derived = jsChildVar.get("d");
        if (derived != null && derived.getBooleanValue()) {
            childVar.setDerived(true);
        }

        JsonNode defaultValue = jsChildVar.get("dv");
        if (defaultValue != null && defaultValue.getBooleanValue()) {
            childVar.setDefaultValue(true);
        }


        JsonNode mandatory = jsChildVar.get("m");
        if (mandatory != null && mandatory.getBooleanValue()) {
            childVar.setMandatory(true);
        }

        JsonNode childNodes = jsChildVar.get("childNodes");
        if (childNodes != null && childNodes.isArray()) {
            ArrayNode a = (ArrayNode) childNodes;
            for (int i = 0; i < a.size(); i++) {
                JsonNode jsonNode = a.get(i);
                mapVar(childVar, (ObjectNode) jsonNode);

            }
        }


    }

    private void mapConstraints(ObjectNode rootConstraint) {
        BoolExpr boolExpr = map(rootConstraint);
        And and = (And) boolExpr;
        for (BoolExpr constraint : and.getExpressions()) {
            fm.addConstraint(constraint);
        }
    }

    public BoolExpr map(ObjectNode jsBoolExpr) {


        int typeId = jsBoolExpr.get("t").getIntValue();
        Type t = Type.getType(typeId);


        if (t.isJunction()) {
            return mapJunction(jsBoolExpr);
        } else if (t.isPair()) {
            return mapPair(jsBoolExpr);
        } else if (t.isVar()) {
            return mapVar(jsBoolExpr);
        } else if (t.isNot()) {
            return mapNot(jsBoolExpr);
        } else {
            throw new IllegalStateException("Unknown type: [" + t + "]");
        }
    }

    private BoolExpr mapJunction(ObjectNode jsJunction) {
        int typeId = jsJunction.get("t").getIntValue();
        Type t = Type.getType(typeId);

        LinkedHashSet<BoolExpr> list = new LinkedHashSet<BoolExpr>();
        for (JsonNode e : jsJunction.get("e")) {
            list.add(map((ObjectNode) e));
        }


        if (t.isAnd()) return fm.and(list);
        else if (t.isOr()) return fm.or(list);
        else if (t.isXor()) return fm.xor(list);
        else throw new IllegalStateException();

    }

    private BoolExpr mapPair(ObjectNode jsPair) {

        int typeId = jsPair.get("t").getIntValue();
        Type t = Type.getType(typeId);


        ObjectNode jsExpr1 = (ObjectNode) jsPair.get("e").get(0);
        ObjectNode jsExpr2 = (ObjectNode) jsPair.get("e").get(1);

        assert jsExpr1 != null : jsPair.toString();
        assert jsExpr2 != null : jsPair.toString();

        BoolExpr expr1 = map(jsExpr1);
        assert expr1 != null;
        BoolExpr expr2 = map(jsExpr2);
        assert expr2 != null;

        if (t.isConflict()) return fm.conflict(expr1, expr2);
        else if (t.isIff()) return fm.iff(expr1, expr2);
        else if (t.isImp()) return fm.imply(expr1, expr2);
        else throw new IllegalStateException();
    }

    private BoolExpr mapNot(ObjectNode jsNot) {

        ObjectNode jsExpr = (ObjectNode) jsNot.get("e").get(0);
        assert jsExpr != null;

        BoolExpr expr = map(jsExpr);
        assert expr != null;

        return BoolExpr.not(expr);

    }

    private BoolExpr mapVar(ObjectNode jsVar) {
        String varCode = jsVar.get("code").getTextValue();
        Var var = fm.resolveVar(varCode);
        assert var != null : varCode + " was not in FeatureModel";
        return var;
    }


}