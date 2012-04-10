package threed.core.featureModel.shared.search.decision;

import threed.core.featureModel.shared.AssignContext;
import threed.core.featureModel.shared.boolExpr.AssignmentException;
import threed.core.featureModel.shared.boolExpr.Var;

public class XorDecision implements Decision {

    private final Var xorChild;

    public XorDecision(Var xorChild) {
        this.xorChild = xorChild;
    }

    public void makeAssignment(AssignContext ctx) throws AssignmentException {
        ctx.assignTrue(xorChild);
        for (Var sibling : xorChild.getSiblings()) {
            ctx.assignFalse(sibling);
        }

    }

    @Override
    public String toString() {
        StringBuffer sb1 = new StringBuffer();

        sb1.append("true[");
        sb1.append(xorChild);
        sb1.append("] false[");

        StringBuffer sb2 = new StringBuffer();

        for (Var sibling : xorChild.getSiblings()) {
            sb2.append(',');
            sb2.append(sibling);
        }

        sb1.append(sb2.substring(1));

        sb1.append(']');

        return sb1.toString();
    }
}
