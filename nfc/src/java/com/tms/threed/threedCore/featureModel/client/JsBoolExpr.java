package com.tms.threed.threedCore.featureModel.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Type;


public final class JsBoolExpr extends JavaScriptObject {

    protected JsBoolExpr() {
    }

    public native int getTypeId() /*-{
        return this.t;
    }-*/;

    public native JsArray<JsBoolExpr> getExpressions() /*-{
        return this.e;
    }-*/;

    private native String getCode() /*-{
        return this.code;
    }-*/;


    public String describe() {

        Type type = Type.getType(getTypeId());

        String simpleName = type.getSimpleName();
        boolean isVar = type.isVar();

        return simpleName + (isVar ? "[" + getExpressions().get(0) + "]" : "");
    }

    public boolean check() {
        Type type = getType();
        assert !type.isConstant():"Constants not supported for marshaling";
        if (type.isVar()) assert getCode() != null;
        if (type.isNot()) assert getExpressions().length() == 1;
        if (type.isPair()) assert getExpressions().length() == 2;
        if (type.isJunction()) assert getExpressions().length() >= 2;
        return true;
    }

    public String getVarCode() {
        assert getType().isVar();
        return getCode();
    }

    public Type getType() {
        int typeId = getTypeId();
        return Type.getType(typeId);
    }


}

