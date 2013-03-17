package c3i.featureModel;

import junit.framework.TestCase;

public class SearchTest extends TestCase {

    public void test1() throws Exception {


    }

    //5.5s, 2.5s
    //.6s
    public void testForEachCamry2011WithOutputVars() throws Exception {

//        Camry2011 fm = new Camry2011();
//
//        Set<Var> outVars = getOutVars(fm);
//
//        Query query = new Query();
//        query.setStartNode(fm.createCspForTreeSearch());
//        query.setOutVars(outVars);
//
//        Search search = query.execute();
//
//
//        assertEquals(203008, fm.getSatCount(outputVars));
//
//        ImmutableSet<Var> outVars = ImmutableSet.copyOf(outputVars);
//        ProductHandler.Default productHandler = new ProductHandler.Default();
//        f.forEach(outVars, productHandler);
//        assertEquals(203008, productHandler.getProductCount());
//
//        search.getResult()
    }

//    private Set<Var> getOutVars(FeatureModel fm) {
//
//        Formula constraint = fm.getFormula();
//        Set<Var> careVars = constraint.getCareVars();
//
//        HashSet<Var> outputVars = new HashSet<Var>(careVars);
//        outputVars.remove(fm.getVar("R7"));
//        outputVars.remove(fm.getVar("BM"));
//        outputVars.remove(fm.getVar("28"));
//        outputVars.remove(fm.getVar("2Q"));
//
//        return outputVars;
//    }

}
