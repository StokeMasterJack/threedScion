package c3i.admin.client.featurePicker;

import c3i.featureModel.shared.boolExpr.Var;

public interface VarPanelModel {

    boolean showFieldHeadings();

    boolean hideDerived();

    VarPanel getVarPanel(Var var);

    CurrentUiPicks getPicks();

    String getRadioGroupPrefix();

}
