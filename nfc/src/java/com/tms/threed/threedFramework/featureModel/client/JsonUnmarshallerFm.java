package com.tms.threed.threedFramework.featureModel.client;

import com.google.gwt.core.client.JsArray;
import com.tms.threed.threedFramework.featureModel.shared.Cardinality;
import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.And;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.BoolExpr;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Type;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;

import java.util.LinkedHashSet;

public final class JsonUnmarshallerFm {

    private FeatureModel fm;

    public FeatureModel createFeatureModelFromJson(JsFeatureModel jsFeatureModel) {
        String displayName = jsFeatureModel.getDisplayName();
        String name = jsFeatureModel.getName();
        int year = jsFeatureModel.getYear();
        fm = new FeatureModel(year, name, displayName);
        mapVars(jsFeatureModel.getRootVar());
        mapConstraints(jsFeatureModel.getConstraints());
        return fm;
    }

    private void mapVars(JsVar jsRootVar) {
        mapVar(null, jsRootVar);
    }

    private void mapConstraints(JsBoolExpr rootConstraint) {
        BoolExpr boolExpr = map(rootConstraint);
        And and = (And) boolExpr;
        for (BoolExpr constraint : and.getExpressions()) {
            fm.addConstraint(constraint);
        }
    }

    private void mapVar(Var parentVar, JsVar jsChildVar) {

        Var childVar;
        if (parentVar == null) {
            childVar = fm.getRootVar();
        } else {
            childVar = parentVar.addChild(jsChildVar.getCode(), jsChildVar.getName());
        }

        String sCardinality = jsChildVar.getCardinality();
        if (sCardinality != null) {
            Cardinality cardinality = Cardinality.valueOf(sCardinality);
            childVar.setCardinality(cardinality);
        }

        childVar.setDerived(jsChildVar.getDerived());
        childVar.setDefaultValue(jsChildVar.getDefaultValue());
        childVar.setMandatory(jsChildVar.getMandatory());

        JsArray<JsVar> jsChildNodes = jsChildVar.getChildNodes();
        if (jsChildNodes != null && jsChildNodes.length() > 0) {
            for (int i = 0; i < jsChildNodes.length(); i++) {
                mapVar(childVar, jsChildNodes.get(i));
            }
        }
    }

    public BoolExpr map(JsBoolExpr jsBoolExpr) {
        Type t = jsBoolExpr.getType();
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

    private BoolExpr mapJunction(JsBoolExpr jsJunction) {
        Type junctionType = jsJunction.getType();

        JsArray<JsBoolExpr> jsExprList = jsJunction.getExpressions();
        LinkedHashSet<BoolExpr> list = new LinkedHashSet<BoolExpr>();
        for (int i = 0; i < jsExprList.length(); i++) {
            list.add(map(jsExprList.get(i)));
        }

        if (junctionType.isAnd()) return fm.and(list);
        else if (junctionType.isOr()) return fm.or(list);
        else if (junctionType.isXor()) return fm.xor(list);
        else {
            throw new IllegalStateException("Invalid junctionType[" + junctionType + "]");
        }

    }

    private BoolExpr mapPair(JsBoolExpr jsPair) {
        Type t = jsPair.getType();

        JsBoolExpr jsExpr1 = jsPair.getExpressions().get(0);
        assert jsExpr1 != null;
        JsBoolExpr jsExpr2 = jsPair.getExpressions().get(1);
        assert jsExpr2 != null;

        BoolExpr expr1 = map(jsExpr1);
        assert expr1 != null : "map(" + jsExpr1.describe() + ") returned null";
        BoolExpr expr2 = map(jsExpr2);
        assert expr2 != null;

        if (t.isConflict()) return fm.conflict(expr1, expr2);
        else if (t.isIff()) return fm.iff(expr1, expr2);
        else if (t.isImp()) return fm.imply(expr1, expr2);
        else throw new IllegalStateException("Invalid pair type[" + t + "]");
    }

    private BoolExpr mapNot(JsBoolExpr jsNot) {

        JsBoolExpr jsExpr = jsNot.getExpressions().get(0);
        assert jsExpr != null;

        BoolExpr expr = map(jsExpr);
        assert expr != null : "map(" + jsExpr.describe() + ") returned null";

        return BoolExpr.not(expr);

    }

    private BoolExpr mapVar(JsBoolExpr jsVar) {
        String varCode = jsVar.getVarCode();
        Var var = fm.getVarOrNull(varCode);
        assert var != null : varCode + " was not in FeatureModel";
        return var;
    }


}