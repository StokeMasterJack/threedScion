package com.tms.threed.threedFramework.threedAdmin.featurePicker.client.varPanels;

import com.google.gwt.user.client.ui.HTML;
import com.tms.threed.threedFramework.threedAdmin.featurePicker.client.VarPanel;
import com.tms.threed.threedFramework.threedAdmin.featurePicker.client.VarPanelModel;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;

import javax.annotation.Nonnull;

public class MandatoryLeaf extends VarPanel {

    public MandatoryLeaf(@Nonnull VarPanelModel context, @Nonnull Var var) {
        super(context, var);
        initWidget(new HTML("X <b>" + var.getCode() + "</b>: " + var.getName()));
    }

}