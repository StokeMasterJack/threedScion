package c3i.admin.client.featurePicker.varPanels;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import c3i.admin.client.featurePicker.VarPanel;
import c3i.admin.client.featurePicker.VarPanelModel;
import c3i.featureModel.shared.boolExpr.Var;

import javax.annotation.Nonnull;
import java.util.List;

public class RootVarPanel extends VarPanel {

    public RootVarPanel(@Nonnull VarPanelModel context, @Nonnull Var var) {
        super(context, var);

        StackLayoutPanel stackLayoutPanel = new StackLayoutPanel(Style.Unit.EM);
        initWidget(stackLayoutPanel);



        List<Var> topVars = var.getChildVars();
        for (Var childVar : topVars) {
            VarPanel childPanel = context.getVarPanel(childVar);
            if (childPanel == null) continue;
            String tabName = childVar.getDisplayName();
            stackLayoutPanel.add(childPanel, tabName, 2.2);
        }

        getElement().getStyle().setBackgroundColor("gold");
        setHeight("100%");

    }


}
