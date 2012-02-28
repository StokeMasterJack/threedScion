package com.tms.threed.threedFramework.threedAdmin.featurePicker.client;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;

public interface VarPanelModel {

    boolean showFieldHeadings();

    boolean hideDerived();

    VarPanel getVarPanel(Var var);

    CurrentUiPicks getPicks();

    String getRadioGroupPrefix();

}
