package com.tms.threed.threedFramework.featureModel.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public final class JsVar extends JavaScriptObject {

    protected JsVar() {}

    public native String getCode() /*-{
       return this.c;
    }-*/;

    public native JsArray<JsVar> getChildNodes() /*-{
       return this.childNodes;
    }-*/;

    public native String getName() /*-{
       if(this.n) return this.n;
       return null;
    }-*/;

    public native String getCardinality() /*-{
       if(this.card) return this.card;
        else return null;
    }-*/;

    public native String getDerivedS() /*-{
        if(this.d) return this.d + "";
        else return null;
    }-*/;

    public native String getDefaultValueS() /*-{
       if(this.dv) return this.dv;
       else return null;
    }-*/;

    public native String getMandatoryS() /*-{
       if(this.m) return this.m + "";
       else return null;
    }-*/;

    public Boolean getDerived() {
        String s = getDerivedS();
        if (s!=null) return new Boolean(s);
        else return null;
    }

    public Boolean getDefaultValue() {
        String s = getDefaultValueS();
        if (s!=null) return new Boolean(s);
        else return null;
    }

    public Boolean getMandatory() {
        String s = getMandatoryS();
        if (s!=null) return new Boolean(s);
        else return null;
    }


}
