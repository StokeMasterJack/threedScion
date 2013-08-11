package c3i.core.featureModel.shared.search;

import c3i.core.featureModel.shared.Csp;
import c3i.core.featureModel.shared.CspTestBase;
import c3i.core.featureModel.shared.boolExpr.MoreThanOneTrueTermXorAssignmentException;
import org.junit.Test;

import javax.annotation.Nullable;

import static org.junit.Assert.fail;


public class CspReduceTest extends CspTestBase {

    @Test
    public void test_Reduce_SimpleVehicle() throws Exception {
        Csp csp = buildCspSimpleVehicle();
        csp = reduce(csp, null);
        csp = reduce(csp, "LE");
        csp = reduce(csp, "V6");
    }


    @Test
    public void test_Reduce_ComplexVehicle() throws Exception {
        Csp csp = buildCspComplexVehicle();

        System.err.println(1);
        csp = reduce(csp, null);
        System.err.println(2);

        csp = reduce(csp, "LE");
        System.err.println(3);
//        csp = reduce(csp, "V6");
//        csp = reduce(csp, "040");
//        csp = reduce(csp, "Ash");
//        csp = reduce(csp, "QA");
//        csp = reduce(csp, "2Q");


    }


    @Test
    public void test_MoreThanOneTrueTermXorAssignmentException() throws Exception {
        Csp csp = buildCspSimpleVehicle();
        try {
            csp.assignTrue("LE");
            csp.assignTrue("SE");
            csp.propagate();
            fail();
        } catch (MoreThanOneTrueTermXorAssignmentException e) {
            //expected
        }
    }


    /**
     *
     * @param cspIn
     * @param varCode null means that no reduction occurs, cspIn is returned
     * @return if varCode !=null, reduce the cspIn by assigning the varCode=true and return the reduced csp, else (if varCode==null) simple return cspIn
     */
    private Csp reduce(Csp cspIn, @Nullable String varCode) {
        if (varCode == null) {
            System.out.println("*** Nothing Picked ***");
            cspIn.print();
            System.out.println("satCount: " + cspIn.satCount());
            System.out.println();
            return cspIn;
        } else {
            System.out.println("*** Pick " + varCode + " ***");
            Csp reduced = cspIn.reduce(varCode);

            reduced.print();
            System.out.println("satCount: " + reduced.satCount());
            System.out.println();
            return reduced;
        }
    }


}
