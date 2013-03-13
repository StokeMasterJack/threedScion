package c3i.admin.client.featurePicker;

import c3i.featureModel.shared.FixedPicks;
import c3i.featureModel.shared.boolExpr.Var;

public interface UiPicks {

    boolean isUiPicked(Var var);

    FixedPicks proposePickRadio(Var var);

    FixedPicks proposePickRadio(String varCode);

    FixedPicks proposeToggleCheckBox(Var var);

    FixedPicks proposeToggleCheckBox(String varCode);

    void pickRadio(Var var);

    void pickRadio(String varCode);

    void toggleCheckBox(Var var);

    void toggleCheckBox(String varCode);

}
