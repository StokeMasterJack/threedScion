package c3i.admin.client.featurePicker.varPanels;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import c3i.admin.client.featurePicker.VarPanel;
import c3i.admin.client.featurePicker.VarPanelModel;
import c3i.featureModel.shared.boolExpr.Var;

import javax.annotation.Nonnull;
import java.util.List;

public class L2Group extends VarPanel {

    private final FlowPanel flowPanel = new FlowPanel();

    public L2Group(@Nonnull VarPanelModel context, @Nonnull Var var) {
        super(context, var);
        List<Var> vars = var.getChildVars();

        flowPanel.add(buildLabel(var));
        for (Var childVar : vars) {
            boolean derived = childVar.isDerived();

            if(derived) continue;
            VarPanel varPanel = context.getVarPanel(childVar);
            flowPanel.add(varPanel);
        }

        initWidget(flowPanel);
        getElement().getStyle().setMarginBottom(1, Style.Unit.EM);
    }

    protected Widget buildLabel(Var var) {
        Label w = new Label(var.getDisplayName() + ": ");
        w.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        w.getElement().getStyle().setMarginBottom(.2, Style.Unit.EM);

        return w;
    }


}