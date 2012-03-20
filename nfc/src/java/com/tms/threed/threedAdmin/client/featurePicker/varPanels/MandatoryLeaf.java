package com.tms.threed.threedAdmin.client.featurePicker.varPanels;

import com.google.gwt.user.client.ui.HTML;
import com.tms.threed.threedAdmin.client.featurePicker.VarPanel;
import com.tms.threed.threedAdmin.client.featurePicker.VarPanelModel;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

import javax.annotation.Nonnull;

public class MandatoryLeaf extends VarPanel {

    public MandatoryLeaf(@Nonnull VarPanelModel context, @Nonnull Var var) {
        super(context, var);
        initWidget(new HTML("X <b>" + var.getCode() + "</b>: " + var.getName()));
    }

}