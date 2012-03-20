package com.tms.threed.threedCore.featureModel.shared.search;

import com.tms.threed.threedCore.featureModel.data.Camry2011;
import com.tms.threed.threedCore.featureModel.data.Trim;
import com.tms.threed.threedCore.featureModel.data.TrimColor;
import com.tms.threed.threedCore.featureModel.shared.Csp;
import com.tms.threed.threedCore.featureModel.shared.CspForTreeSearch;
import com.tms.threed.threedCore.featureModel.shared.FeatureModel;
import com.tms.threed.threedCore.featureModel.shared.SatCountProductHandler;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.MoreThanOneTrueTermXorAssignmentException;
import org.junit.After;
import org.junit.Test;

import javax.annotation.Nullable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class CspTest {

    @Test
    public void test_FindAll_SimpleVehicle() throws Exception {

        Csp csp = buildCspSimpleVehicle();
        SatCountProductHandler counter = new SatCountProductHandler();

        csp.findAll(counter);

        assertEquals(11, counter.getCount());

    }

    @Test
    public void test_FindAll_MediumVehicle() throws Exception {

        Csp csp = buildCspMediumVehicle();
        SatCountProductHandler counter = new SatCountProductHandler();

        csp.findAll(counter);

        assertEquals(227, counter.getCount());

    }


    @Test
    public void test_FindAll_ComplexVehicle() throws Exception {

        Csp csp = buildCspComplexVehicle();
        SatCountProductHandler counter = new SatCountProductHandler();

        csp.findAll(counter);

        assertEquals(2080512, counter.getCount());
    }

    @Test
    public void test_SatCount_SimpleVehicle() throws Exception {
        Csp csp = buildCspSimpleVehicle();
        long satCount = csp.satCount();
        assertEquals(11, satCount);
    }

    @Test
    public void test_SatCount_MediumVehicle() throws Exception {
        Csp csp = buildCspMediumVehicle();
        long satCount = csp.satCount();
        assertEquals(227, satCount);
    }

    @Test
    public void test_SatCount_ComplexVehicle() throws Exception {
        Csp csp = buildCspComplexVehicle();
        long satCount = csp.satCount();
        assertEquals(2080512, satCount);
    }

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

        csp = reduce(csp, null);
        csp = reduce(csp, "LE");
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


    @After
    public void tearDown() throws Exception {
        System.out.flush();
    }


    private CspForTreeSearch buildCspSimpleVehicle() {
        FeatureModel fm = new Trim();
        CspForTreeSearch csp = new CspForTreeSearch(fm, fm.getConstraint());
        return csp;
    }

    private Csp buildCspMediumVehicle() {
        FeatureModel fm = new TrimColor();
        Csp csp = new CspForTreeSearch(fm, fm.getConstraint());
        return csp;
    }

    private Csp buildCspComplexVehicle() {
        FeatureModel fm = new Camry2011();
//        Csp csp = new CspSimple(fm, fm.getConstraint());
        Csp csp = new CspForTreeSearch(fm, fm.getConstraint(), null);
        return csp;
    }
}
