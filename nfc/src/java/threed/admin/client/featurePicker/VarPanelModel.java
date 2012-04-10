package threed.admin.client.featurePicker;

import threed.core.featureModel.shared.boolExpr.Var;

public interface VarPanelModel {

    boolean showFieldHeadings();

    boolean hideDerived();

    VarPanel getVarPanel(Var var);

    CurrentUiPicks getPicks();

    String getRadioGroupPrefix();

}
