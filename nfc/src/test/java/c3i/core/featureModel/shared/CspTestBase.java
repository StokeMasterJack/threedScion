package c3i.core.featureModel.shared;

import c3i.core.featureModel.data.Camry2011;
import c3i.core.featureModel.data.Trim;
import c3i.core.featureModel.data.TrimColor;
import c3i.core.featureModel.shared.boolExpr.MasterConstraint;

public class CspTestBase {

    protected CspForTreeSearch buildCspSimpleVehicle() {
        FeatureModel fm = new Trim();
        CspForTreeSearch csp = new CspForTreeSearch(fm, fm.getConstraint());
        return csp;
    }

    protected Csp buildCspMediumVehicle() {
        FeatureModel fm = new TrimColor();
        Csp csp = new CspForTreeSearch(fm, fm.getConstraint());
        return csp;
    }


    protected Csp buildCspComplexVehicle() {
        FeatureModel fm = new Camry2011();
        //        Csp csp = new CspSimple(fm, fm.getConstraint());

        MasterConstraint constraint = fm.getConstraint();
        constraint.print();
        Csp csp = new CspForTreeSearch(fm, constraint, null);
        return csp;
    }
}
