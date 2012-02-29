package com.tms.threed.threedAdmin.featurePicker.client.varPanels;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.tms.threed.threedAdmin.featurePicker.client.VarPanel;
import com.tms.threed.threedAdmin.featurePicker.client.VarPanelModel;
import com.tms.threed.threedFramework.featureModel.shared.FixResult;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;

import javax.annotation.Nonnull;

/**
 * AKA Boolean Field
 */
public class OptionLeaf extends VarPanel {

    private CheckBox checkBox;

    public OptionLeaf(@Nonnull VarPanelModel context, @Nonnull Var var) {
        super(context, var);

        assert !var.isDerived() : ("var[" + var + "] is derived. Derived vars are not allowed in an OptionLeaf.");

        checkBox = initCheckBox();
        initWidget(initCheckboxPanel());
    }

    private Panel initCheckboxPanel() {
        FlowPanel t = new FlowPanel();
        t.add(checkBox);
        return t;
    }

    private CheckBox initCheckBox() {
         String cbLabel = "<b>" + var.getCode() + "</b>";
        if (var.getName() != null) {
            cbLabel += var.getDisplayName();
        }

        final CheckBox cb = new CheckBox(cbLabel, true);





        cb.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {

                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override public void execute() {
                        picks.toggleCheckBox(var);
                        picks.fix();
                        picks.fire();
                    }
                });
            }
        });
        return cb;
    }

    public void refresh() {

        FixResult response = picks.proposeToggleCheckBox(var);

        if (response.isValidBuild()) {
            getElement().getStyle().setColor("black");
            setTitle("");
        } else {
            getElement().getStyle().setColor("#BBBBBB");
            setTitle(response.getErrorMessage());
        }

        checkBox.setValue(picks.isUiPicked(var));
    }


}
