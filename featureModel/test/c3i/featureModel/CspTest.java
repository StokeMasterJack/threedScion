package c3i.featureModel;

import c3i.featureModel.data.Camry2011;
import c3i.featureModel.data.Trim;
import c3i.featureModel.data.TrimColor;
import c3i.featureModel.data.TrimColorOption;
import c3i.featureModel.shared.FeatureModel;
import c3i.featureModel.shared.boolExpr.MoreThanOneTrueTermXorAssignmentException;
import c3i.featureModel.shared.node.Csp;
import c3i.featureModel.shared.search.CountingProductHandler;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CspTest {

    @Test
    public void test_IsSat_SimpleVehicle() throws Exception {
        Csp csp = buildCspSimpleVehicle();
//        csp.print();
        boolean sat = csp.isSat();
        assertTrue(sat);
    }

    @Test
    public void test_ForEachProduct_SimpleVehicle() throws Exception {
        Csp csp = buildCspSimpleVehicle();
//        csp.printMultiLine();
//        System.out.println();
        CountingProductHandler counter = new CountingProductHandler();
        csp.forEachProduct(counter);
        assertEquals(11, counter.getCount());
    }

    @Test
    public void test_ForEachProduct_MediumVehicle() throws Exception {
        Csp csp = buildCspMediumVehicle();
        CountingProductHandler counter = new CountingProductHandler();
        csp.forEachProduct(counter);
        assertEquals(227, counter.getCount());
    }

    @Test
    public void test_ForEachProduct_SemiComplexVehicle() throws Exception {
        Csp csp = buildCspSemiComplexVehicle();
        CountingProductHandler counter = new CountingProductHandler();
        csp.forEachProduct(counter);
        assertEquals(44944, counter.getCount());
    }

    @Test
    public void test_ForEachProduct_ComplexVehicle() throws Exception {
        Csp csp = buildCspComplexVehicle();
        CountingProductHandler counter = new CountingProductHandler();
        csp.forEachProduct(counter);
        assertEquals(2080512L, counter.getCount());
    }

    @Test
    public void test_SatCount_MediumVehicle() throws Exception {
        Csp csp = buildCspMediumVehicle();
        long satCount = csp.getProductCount();
        assertEquals(227, satCount);
    }

    @Test
    public void test_SatCount_SemiComplexVehicle() throws Exception {
        Csp csp = buildCspSemiComplexVehicle();
        long satCount = csp.getProductCount();
        assertEquals(44944, satCount);
    }

    @Test
    public void test_SatCount_ComplexVehicle() throws Exception {
        Csp csp = buildCspComplexVehicle();
        long satCount = csp.getProductCount();
        assertEquals(2080512, satCount);
    }

//    @Test
//    public void test_Reduce_SimpleVehicle() throws Exception {
//        Csp csp = buildCspSimpleVehicle();
//        csp = reduce(csp, null);
//        csp = reduce(csp, "LE");
//        csp = reduce(csp, "V6");
//    }
//
//
//    @Test
//    public void test_Reduce_ComplexVehicle() throws Exception {
//        Csp csp = buildCspComplexVehicle();
//
//        csp = reduce(csp, null);
//        csp = reduce(csp, "LE");
////        csp = reduce(csp, "V6");
////        csp = reduce(csp, "040");
////        csp = reduce(csp, "Ash");
////        csp = reduce(csp, "QA");
////        csp = reduce(csp, "2Q");
//
//
//    }


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
     * varCode null means that no reduction occurs, cspIn is returned
     * @return if varCode !=null, reduce the cspIn by assigning the varCode=true and return the reduced csp, else (if varCode==null) simple return cspIn
     */
//    private Csp reduce(Csp cspIn, @Nullable String varCode) {
//        if (varCode == null) {
//            System.out.println("*** Nothing Picked ***");
//            cspIn.print();
//            System.out.println("satCount: " + cspIn.getProductCount());
//            System.out.println();
//            return cspIn;
//        } else {
//            System.out.println("*** Pick " + varCode + " ***");
//            Csp reduced = cspIn.reduce(varCode);
//
//            reduced.print();
//            System.out.println("satCount: " + reduced.getProductCount());
//            System.out.println();
//            return reduced;
//        }
//    }
    @After
    public void tearDown() throws Exception {
        System.out.flush();
    }

    private Csp buildCspSimpleVehicle() {
        FeatureModel fm = new Trim();
        Csp csp = fm.createCsp();
        return csp;
    }

    private Csp buildCspMediumVehicle() {
        FeatureModel fm = new TrimColor();
        Csp csp = fm.createCsp();
        return csp;
    }

    private Csp buildCspSemiComplexVehicle() {
        FeatureModel fm = new TrimColorOption();
        //        Csp csp = new CspSimple(fm, fm.getConstraint());
        Csp csp = fm.createCsp();
        return csp;
    }

    private Csp buildCspComplexVehicle() {
        FeatureModel fm = new Camry2011();
        Csp csp = fm.createCsp();
        return csp;
    }

}
