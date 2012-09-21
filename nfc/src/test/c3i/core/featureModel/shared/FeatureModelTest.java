package c3i.core.featureModel.shared;

//import com.tms.threed.config.server.ThreedConfigHelper;
//import com.tms.threed.config.shared.ThreedConfig;

import c3i.core.featureModel.data.Camry2011;
import c3i.core.featureModel.data.TrimColor;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.featureModel.shared.picks.Picks;
import smartsoft.util.lang.shared.Path;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class FeatureModelTest extends TestCase {

    Path pngRoot;

    public void test1() throws Exception {
        FeatureModel fm = new Camry2011();
    }

    public void testVar() throws Exception {
        FeatureModel fm = new Camry2011();

        for (Var var : fm.getAccessories()) {
            System.out.println(var);
        }


    }

    public void testFixupNoBdd() throws Exception {

        FeatureModel fm = new Camry2011();

        Picks picks = fm.createPicks();
        picks.pick("Base", "040", "Ash", "6AT");

        System.out.println("Raw Picks: ");
        System.out.println("\t" + picks.getAllPicks2());
        System.out.println();

        long t1 = System.currentTimeMillis();

        picks.fixup();
        long t2 = System.currentTimeMillis();
        long delta = t2 - t1;

        System.out.println();
        System.out.println("FixedUp Picks: ");
        System.out.println("\t" + picks.getAllPicks2());

        System.out.println("Delta: " + delta);
        System.out.println();

        picks.printUnassignedVars();


//        fm.printAssignments();

//        fm.getRootVar().printVarTree();

    }


    public void testPicksForCamry2011_A() throws Exception {
        FeatureModel fm = new Camry2011();
//        System.out.println("fm.satCount() = [" + fm.satCount() + "]");

        System.out.println("fm.getVarCount() = [" + fm.size() + "]");
        System.out.println("fm.getAllConstraintCount() = [" + fm.getAllConstraintCount() + "]");


        {
            long t1 = System.currentTimeMillis();
//            fm.getBddNode();
            long t2 = System.currentTimeMillis();
            long delta = t2 - t1;
            System.out.println("fm.getBdd() Delta: " + delta);
        }


    }


    public void test_PrintFeatureTree() throws Exception {
        TrimColor fm = new TrimColor();
        fm.printTree();

    }

    private void showRemainsPicks(FeatureModel fm) {
//        boolean v = fm.isPickValid("nv");
//        System.out.println("v = [" + v + "]");
//        Set<Var> vars = fm.getUnsetVars();

    }


    public static void printSize(Serializable o) throws IOException {
        System.out.println("byteCount: " + sizeOf(o));
    }

    public static int sizeOf(Serializable o) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);

        oos.writeObject(o);
        oos.flush();

        return os.size();

    }


}
