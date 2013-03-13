package c3i.admin.client.featurePicker.varPanels;

import com.google.gwt.user.client.ui.HTML;
import c3i.admin.client.featurePicker.VarPanel;
import c3i.admin.client.featurePicker.VarPanelModel;
import c3i.featureModel.shared.boolExpr.Var;

import javax.annotation.Nonnull;

public class MandatoryLeaf extends VarPanel {

    public MandatoryLeaf(@Nonnull VarPanelModel context, @Nonnull Var var) {
        super(context, var);
        initWidget(new HTML("X <b>" + var.getCode() + "</b>: " + var.getName()));
    }

}