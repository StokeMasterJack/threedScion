package c3i.admin.client.featurePicker.varPanels;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import c3i.admin.client.featurePicker.VarPanel;
import c3i.admin.client.featurePicker.VarPanelModel;
import c3i.featureModel.shared.FixedPicks;
import c3i.featureModel.shared.boolExpr.Var;

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
        t.getElement().getStyle().setPaddingLeft(1, Style.Unit.EM);
        t.add(checkBox);
        return t;
    }

    private CheckBox initCheckBox() {
        String cbLabel = "<span style='font-weight:bold;padding-left:.7em'>" + var.getCode() + "</span>";
//         String cbLabel = "<b>" + var.getCode() + "</b>";
        if (var.getName() != null) {
            cbLabel += " " + var.getDisplayName();
        }

        final CheckBox cb = new CheckBox(cbLabel, true);





        cb.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {

                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override public void execute() {
                        picks.toggleCheckBox(var);
                    }
                });
            }
        });
        return cb;
    }

    public void refresh() {

        FixedPicks response = picks.proposeToggleCheckBox(var);

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
