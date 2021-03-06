package c3i.admin.client.featurePicker;

import c3i.featureModel.shared.boolExpr.Var;
import c3i.admin.client.featurePicker.varPanels.L1Group;
import c3i.admin.client.featurePicker.varPanels.L2Group;
import c3i.admin.client.featurePicker.varPanels.MandatoryLeaf;
import c3i.admin.client.featurePicker.varPanels.OptionLeaf;
import c3i.admin.client.featurePicker.varPanels.PickOneGroup;
import c3i.admin.client.featurePicker.varPanels.PickOneLeaf;
import c3i.admin.client.featurePicker.varPanels.RootVarPanel;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class VarPanelFactory {

    private VarPanelModel context;

    private final Map<Var, VarPanel> varPanelMap = new HashMap<Var, VarPanel>();

    public void setVarPanelContext(VarPanelModel varPanelContext) {
        this.context = varPanelContext;
    }

    private VarPanel createVarPanel(final Var var) {
        final VarPanel varPanel = createVarPanelInternal(var);
        varPanelMap.put(var, varPanel);
        return varPanel;
    }

    public VarPanel getVarPanel(Var var) {
        VarPanel p = varPanelMap.get(var);
        if (p == null) {
            p = createVarPanel(var);
        }
        return p;
    }

    private VarPanel createVarPanelInternal(@Nonnull Var var) {
        assert context != null : "Must call setVarPanelContext before calling createVarPanel";

//        System.out.println("var = [" + var + "]");
//        if(var.getCode().equals("E5")){
//            System.out.println(var);
//        }

        if (var.isRoot()) {
            return new RootVarPanel(context, var);
        } else if (var.hasChildVars()) {
            if (var.isXorParent()) {
                if (var.isDerived()) {
                    return null;
                } else {
                    return new PickOneGroup(context, var);
                }
            } else {
                int fmTreeDepth = var.getDepth();
                if (fmTreeDepth == 1) {
                    return new L1Group(context, var);
                } else if (fmTreeDepth == 2) {
                    return new L2Group(context, var);
                } else {
                    System.out.println("var.getLabel() = [" + var.getLabel() + "]");
                    System.out.println("var.getDepth() = [" + var.getDepth() + "]");
                    throw new IllegalStateException("Could not create a VarPanel for var[" + var + "]");
                }
            }
        } else { //isLeaf
            if (var.isXorChild()) {
                return new PickOneLeaf(context, var);
            } else if (var.isOptional()) {
                if (var.isDerived()) {
                    return null;
                } else {
                    return new OptionLeaf(context, var);
                }
            } else {
                return new MandatoryLeaf(context, var);
            }
        }

    }


}
