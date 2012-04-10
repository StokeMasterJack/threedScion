package threed.admin.client.featurePicker.varPanels;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import threed.admin.client.featurePicker.VarPanel;
import threed.admin.client.featurePicker.VarPanelModel;
import threed.core.featureModel.shared.boolExpr.Var;

import javax.annotation.Nonnull;
import java.util.List;

public class RootVarPanel extends VarPanel {

    public RootVarPanel(@Nonnull VarPanelModel context, @Nonnull Var var) {
        super(context, var);

        StackLayoutPanel stackLayoutPanel = new StackLayoutPanel(Style.Unit.EM);
        initWidget(stackLayoutPanel);

        getElement().getStyle().setProperty("borderBottom", "#CCCCCC 1px solid");

//        setHeight("99%");
        List<Var> topVars = var.getChildVars();
        for (Var childVar : topVars) {
            VarPanel childPanel = context.getVarPanel(childVar);
            if (childPanel == null) continue;
            String tabName = childVar.getDisplayName();
            System.out.println("tabName = " + tabName);
            stackLayoutPanel.add(childPanel, tabName, 2.2);
        }

    }


}
