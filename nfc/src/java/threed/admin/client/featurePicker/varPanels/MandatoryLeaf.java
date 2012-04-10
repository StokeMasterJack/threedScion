package threed.admin.client.featurePicker.varPanels;

import com.google.gwt.user.client.ui.HTML;
import threed.admin.client.featurePicker.VarPanel;
import threed.admin.client.featurePicker.VarPanelModel;
import threed.core.featureModel.shared.boolExpr.Var;

import javax.annotation.Nonnull;

public class MandatoryLeaf extends VarPanel {

    public MandatoryLeaf(@Nonnull VarPanelModel context, @Nonnull Var var) {
        super(context, var);
        initWidget(new HTML("X <b>" + var.getCode() + "</b>: " + var.getName()));
    }

}