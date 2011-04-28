package com.tms.threed.threedAdmin.featurePicker.client.varPanels;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.tms.threed.threedAdmin.featurePicker.client.VarPanelModel;
import com.tms.threed.threedAdmin.featurePicker.client.VarPanel;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;

import javax.annotation.Nonnull;
import java.util.List;

public class RootVarPanel extends VarPanel {

    private StackLayoutPanel stackLayoutPanel = new StackLayoutPanel(Style.Unit.EM);

    public RootVarPanel(@Nonnull VarPanelModel context, @Nonnull Var var) {
        super(context, var);
        initWidget(stackLayoutPanel);


        List<Var> topVars = var.getChildVars();
        for (Var childVar : topVars) {
            VarPanel childPanel = context.getVarPanel(childVar);
            if (childPanel == null) continue;
            String tabName = childVar.getDisplayName();
            stackLayoutPanel.add(childPanel, tabName, 2.2);
        }

    }




}
