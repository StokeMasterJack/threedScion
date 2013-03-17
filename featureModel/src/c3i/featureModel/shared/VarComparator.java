package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.Var;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class VarComparator implements Comparator<Var> {

    @Override
    public int compare(Var v1, Var v2) {

        boolean isModelCode1 = v1.isModelCodeXorChild();
        boolean isModelCode2 = v2.isModelCodeXorChild();
        if (isModelCode1 && !isModelCode2) return -1;
        else if (!isModelCode1 && isModelCode2) return 1;
        else if (isModelCode1 && isModelCode2) return 0;


        boolean isExtColor1 = v1.isExteriorColorXorChild();
        boolean isExtColor2 = v2.isExteriorColorXorChild();
        if (isExtColor1 && !isExtColor2) return -1;
        if (!isExtColor1 && isExtColor2) return 1;
        if (isExtColor1 && isExtColor2) return 0;


        boolean isIntColor1 = v1.isInteriorColorXorChild();
        boolean isIntColor2 = v2.isInteriorColorXorChild();
        if (isIntColor1 && !isIntColor2) return -1;
        if (!isIntColor1 && isIntColor2) return 1;
        if (isIntColor1 && isIntColor2) return 0;

        boolean xorChild1 = v1.isXorChild();
        boolean xorChild2 = v2.isXorChild();
        if (xorChild1 && !xorChild2) {
            return -1;
        } else if (!xorChild1 && xorChild2) {
            return 1;
        } else if (xorChild1 && xorChild2) {

            int xorSiblingCount1 = v1.getParent().getChildCount();
            int xorSiblingCount2 = v2.getParent().getChildCount();

            if (xorSiblingCount1 < xorSiblingCount2) {
                return 1;
            } else if (xorSiblingCount1 > xorSiblingCount2) {
                return -1;
            } else {
                String parentCode1 = getParentCode(v1);
                String parentCode2 = getParentCode(v2);
                return parentCode1.compareTo(parentCode2);
            }

        } else {
            return 0;
//                Integer conflictCount1 = getConflictCountForVar(v1);
//                Integer conflictCount2 = getConflictCountForVar(v2);
//                return conflictCount2.compareTo(conflictCount1);
        }
    }

    public String getParentCode(Var var) {
        boolean r = var.isRoot();
        if (r) {
            return "NoParent";
        } else {
            return var.getParent().getCode();
//                return p.getCode();
        }

    }

    public void printVarSort(List<Var> vars, Collection<Var> outputVars) {

        System.err.println("Var sort:");
        for (Var var : vars) {

            boolean png = outputVars != null && outputVars.contains(var);
            boolean xorChild = var.isXorChild();
            int siblingCount = xorChild ? var.getParent().getChildCount() : 0;

            String sPng = png ? "yPng" : "nPng";
            String sXorChild = xorChild ? "yXorChild" : "nXorChild";

            String sSiblingCount = xorChild ? siblingCount + "SiblingCount" : "NSiblingCount";

            String parentName = var.isRoot() ? "NoParent" : getParentCode(var);

            System.err.println("\t " + sPng + "\t" + sXorChild + "\t" + sSiblingCount + "\t" + parentName + "\t" + var.getCode());
        }

    }

//        public void printVarSort(List<Var> vars) {
//            printVarSort(vars, null);
//        }


}
