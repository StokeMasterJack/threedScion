package threed.admin.client.featurePicker;

import threed.core.featureModel.shared.FixResult;
import threed.core.featureModel.shared.boolExpr.Var;

public interface UiPicks {

    boolean isUiPicked(Var var);

    FixResult proposePickRadio(Var var);

    FixResult proposePickRadio(String varCode);

    FixResult proposeToggleCheckBox(Var var);

    FixResult proposeToggleCheckBox(String varCode);

    void pickRadio(Var var);

    void pickRadio(String varCode);

    void toggleCheckBox(Var var);

    void toggleCheckBox(String varCode);

}
