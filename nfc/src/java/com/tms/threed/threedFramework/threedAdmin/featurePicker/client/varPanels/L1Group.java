package com.tms.threed.threedFramework.threedAdmin.featurePicker.client.varPanels;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.tms.threed.threedFramework.threedAdmin.featurePicker.client.VarPanel;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.threedAdmin.featurePicker.client.VarPanelModel;

import javax.annotation.Nonnull;
import java.util.List;

public class L1Group extends VarPanel {

    public L1Group(@Nonnull VarPanelModel context, @Nonnull Var var) {
        super(context, var);
        List<Var> vars = var.getChildVars();

        FlowPanel flowPanel = new FlowPanel();
        for (Var childVar : vars) {
            boolean derived = childVar.isDerived();
            if(derived) continue;
            VarPanel varPanel = context.getVarPanel(childVar);
            flowPanel.add(varPanel);
        }

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.setWidget(flowPanel);

        initWidget(scrollPanel);

        getElement().getStyle().setPadding(.5, Style.Unit.EM);
    }

}