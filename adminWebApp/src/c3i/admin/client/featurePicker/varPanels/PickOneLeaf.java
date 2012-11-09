package c3i.admin.client.featurePicker.varPanels;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RadioButton;
import c3i.admin.client.featurePicker.VarPanel;
import c3i.admin.client.featurePicker.VarPanelModel;
import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.featureModel.shared.boolExpr.Var;

import javax.annotation.Nonnull;

public class PickOneLeaf extends VarPanel {

    private final RadioButton radioButton;

    public PickOneLeaf(@Nonnull final VarPanelModel context, @Nonnull final Var var) {
        super(context, var);
        assert var.isXorChild();

        assert !var.isDerived() : ("var[" + var + "] is derived. Derived vars are not allowed in a pickOneLeaf.");

        String radioGroupPrefix = context.getRadioGroupPrefix();
        Var pickOneParent = var.getParent();


        String radioGroupId = radioGroupPrefix + "-" + pickOneParent.getCode();

        String radioLabel = "<span style='font-weight:bold;padding-left:.7em'>" + var.getCode() + "</span>";
        if (var.getName() != null) {
            radioLabel += " " + var.getDisplayName();
        }

        radioButton = new RadioButton(radioGroupId, radioLabel, true);


        FlowPanel mainPanel = new FlowPanel();
        mainPanel.getElement().getStyle().setPaddingLeft(1, Style.Unit.EM);
        mainPanel.add(radioButton);

        initWidget(mainPanel);

        radioButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    public void execute() {
                        picks.pickRadio(var);
                    }
                });

            }
        });
    }

    public void refresh() {

        radioButton.setValue(picks.isUiPicked(var));

        FixedPicks response = picks.proposePickRadio(var);

        if (response.isValidBuild()) {
            getElement().getStyle().setColor("black");
            setTitle("");
        } else {
            getElement().getStyle().setColor("#BBBBBB");
            setTitle(response.getErrorMessage());
        }


    }

}


