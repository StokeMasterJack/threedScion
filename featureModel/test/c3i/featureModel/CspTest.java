package c3i.featureModel;

import c3i.featureModel.data.Camry2011;
import c3i.featureModel.data.Trim;
import c3i.featureModel.data.TrimColor;
import c3i.featureModel.data.TrimColorOption;
import c3i.featureModel.shared.FeatureModel;
import c3i.featureModel.shared.boolExpr.MoreThanOneTrueTermXorAssignmentException;
import c3i.featureModel.shared.common.SimplePicks;
import c3i.featureModel.shared.node.Csp;
import c3i.featureModel.shared.search.CountingProductHandler;
import c3i.featureModel.shared.search.ForEachSolutionSearch;
import c3i.featureModel.shared.search.ProductHandler;
import org.junit.After;
import org.junit.Test;
import smartsoft.util.Count2;

import javax.annotation.Nullable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CspTest {

    @Test
    public void test_IsSat_SimpleVehicle() throws Exception {
        Csp csp = buildCspSimpleVehicle();
        assertTrue(csp.isSat());
    }


    @Test
    public void test_FindAll_SimpleVehicle() throws Exception {
        Csp csp = buildCspSimpleVehicle();
        CountingProductHandler counter = new CountingProductHandler();
        csp.forEachProduct(counter);
        assertEquals(11, counter.getCount());
    }

    @Test
    public void test_FindAll_MediumVehicle() throws Exception {
        Csp csp = buildCspMediumVehicle();
        CountingProductHandler counter = new CountingProductHandler();
        csp.forEachProduct(counter);
        assertEquals(227, counter.getCount());
    }

    @Test
    public void test_ForEachProduct_ComplexVehicle() throws Exception {
        Csp csp = buildCspComplexVehicle();
        final Count2 count = new Count2();
        csp.forEachProduct(new ProductHandler() {
            @Override
            public void onProduct(SimplePicks product) {
                count.increment();
            }
        });
        assertEquals(2080512L, count.get());
    }

    @Test
    public void test_ForEachSolution_ComplexVehicle() throws Exception {
        Csp csp = buildCspComplexVehicle();

        final Count2 count = new Count2();
        ForEachSolutionSearch search = new ForEachSolutionSearch();
        search.setProductHandler(new ProductHandler() {
            @Override
            public void onProduct(SimplePicks product) {
                count.increment();
            }
        });

        search.start(csp);

        assertEquals(2080512L, count.get());
    }


    //103056
    @Test
    public void test_ForEachSolution_ComplexVehicle2() throws Exception {

        Csp csp = buildCspComplexVehicle();
        ForEachSolutionSearch nh = new ForEachSolutionSearch();

        csp.forEachSolution(nh);

        assertEquals(2080512, nh.getProductCount());
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
     * @param cspIn
     * @param varCode null means that no reduction occurs, cspIn is returned
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
        Csp csp = new Csp(fm);
        return csp;
    }

    private Csp buildCspMediumVehicle() {
        FeatureModel fm = new TrimColor();
        Csp csp = new Csp(fm);
        return csp;
    }

    private Csp buildCspSemiComplexVehicle() {
        FeatureModel fm = new TrimColorOption();
        //        Csp csp = new CspSimple(fm, fm.getConstraint());
        Csp csp = new Csp(fm);
        return csp;
    }

    private Csp buildCspComplexVehicle() {
        FeatureModel fm = new Camry2011();
        Csp csp = new Csp(fm);
        return csp;
    }

}
