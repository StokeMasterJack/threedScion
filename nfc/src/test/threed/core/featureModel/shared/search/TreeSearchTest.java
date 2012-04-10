package threed.core.featureModel.shared.search;

import threed.core.threedModel.shared.JpgKey;
import threed.core.featureModel.data.Camry2011;
import threed.core.featureModel.data.Trim;
import threed.core.featureModel.data.TrimColor;
import threed.core.featureModel.data.TrimColorOption;
import threed.core.featureModel.shared.AssignmentsForTreeSearch;
import threed.core.featureModel.shared.CspForTreeSearch;
import threed.core.featureModel.shared.FeatureModel;
import threed.core.featureModel.shared.boolExpr.Var;
import threed.core.imageModel.shared.ImView;
import threed.core.imageModel.shared.slice.ImageSlice;
import threed.core.imageModel.shared.slice.Jpg;
import threed.repo.server.Repos;
import threed.repo.server.SeriesRepo;
import threed.repo.server.rt.RtRepo;
import threed.core.threedModel.shared.JpgWidth;

import threed.core.threedModel.shared.SeriesKey;
import threed.core.threedModel.shared.*;
import threed.core.threedModel.shared.Slice;
import threed.core.threedModel.shared.ViewKey;
import junit.framework.TestCase;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class TreeSearchTest extends TestCase {

    Repos repos;

    @Override
    protected void setUp() throws Exception {
        repos = Repos.get();
    }

    public void testTrim() throws Exception {

        FeatureModel fm = new Trim();
        CspForTreeSearch csp = fm.createCspForTreeSearch();

        final HashSet<Set<Var>> products = new HashSet<Set<Var>>();

        TreeSearch treeSearch = new TreeSearch();

        treeSearch.start(csp);
        treeSearch.printCounts();


    }


    public void testTrimColor() throws Exception {

        FeatureModel fm = new TrimColor();
        CspForTreeSearch csp = fm.createCspForTreeSearch();

        TreeSearch treeSearch = new TreeSearch();

        treeSearch.start(csp);
        treeSearch.printCounts();


    }


    /**
     *
     * No careVars
     *      satCount: 44944
     *          25s
     *          1s
     *
     *
     */
    public void testTrimColorOptions() throws Exception {

        FeatureModel fm = new TrimColorOption();
        CspForTreeSearch csp = fm.createCspForTreeSearch();

        TreeSearch treeSearch = new TreeSearch();

        treeSearch.start(csp);
        treeSearch.printCounts();


    }

    /*

       older times:
           86s
           80s
           64s: added var sort
           61s: gave special decision for xors
           64s
           59s replace isSolved with decide returns null
           16s added constraint reduction
           18s
           14s
           3.7s added output filter
           2.6  remove HashSet dup check dup

       BDD: satCount: 2080512

       No careVarFilter:   satCount: 2080512
           400s    simplify in search: N   featureFilter: none
           145s    simplify in search: Y   featureFilter: none
           13s
           8.7     added conflict-based varSort
           2.5s

       With careVarFilter: satCount: 152512
           simplify in search: N   featureFilter: none
               29s

           simplify in search: Y   featureFilter: none
               2.6s
               2.2s    added shallowCopy of topExpr
               1.5s    pre-computed hash for BoolExpr subtypes

    */
    public void testCamry2011() throws Exception {

        Camry2011 fm = new Camry2011();
        CspForTreeSearch csp = fm.createCspForTreeSearch();

        TreeSearch treeSearch = new TreeSearch();
        treeSearch.start(csp);

        System.out.println("satCount = " + treeSearch.getSolutionCount());


    }


    public void test_Reduce_ComplexVehicle() throws Exception {
        CspForTreeSearch csp = buildCspComplexVehicle();
        csp = reduce(csp, null);
        csp = reduce(csp, "LE");
        csp = reduce(csp, "V6");
        csp = reduce(csp, "040");
        csp = reduce(csp, "Ash");
        csp = reduce(csp, "QA");
        csp = reduce(csp, "2Q");
    }




    /**
     *
     * @param cspIn
     * @param varCode null means that no reduction occurs, cspIn is returned
     * @return if varCode !=null, reduce the cspIn by assigning the varCode=true and return the reduced csp, else (if varCode==null) simple return cspIn
     */
    private CspForTreeSearch reduce(CspForTreeSearch cspIn, @Nullable String varCode) {
        if (varCode == null) {
            System.err.println("*** Nothing Picked ***");
            cspIn.print();
            System.err.println("satCount: " + cspIn.satCount());
            System.err.println();
            return cspIn;
        } else {
            System.err.println("*** Pick " + varCode + " ***");
            CspForTreeSearch reduced = cspIn.reduce(varCode);

            reduced.print();
            System.err.println("satCount: " + reduced.satCount());
            System.err.println();
            return reduced;
        }
    }



    public void testTundraJpgs() throws Exception {
        jpgCounter(getTundra());
    }

    public void testAvalonJpgs() throws Exception {
        jpgCounter(getAvalon());
    }

    public void testVenzaJpgs() throws Exception {
        jpgCounter(getVenza());
    }

    public void testTacomaJpgs() throws Exception {
        ThreedModel threedModel = getTacoma();
        jpgCounter(threedModel);
    }



    public void jpgCounter(ThreedModel threedModel) {
        long totalJpgCount = 0;
        FeatureModel fm = threedModel.getFeatureModel();
        for (ImView view : threedModel.getImageModel().getViews()) {
            System.err.println(view.getName());
            for (int angle = 1; angle <= view.getAngleCount(); angle++) {
                final Set<String> jpgs = new HashSet<String>();
                System.err.println("\t angle: " + angle);
                final ImageSlice imageSlice = threedModel.getImageSlice(view.getName(), angle);

                Set<Var> careVars = new HashSet<Var>();
                careVars.addAll(imageSlice.getJpgVars());
//                careVars.addAll(fm.getPickableVars());

                CspForTreeSearch csp = fm.createCspForTreeSearch(careVars);
                csp.propagateSimplify();
                final TreeSearch treeSearch = new TreeSearch();
                treeSearch.setProductHandler(new ProductHandler() {
                    @Override
                    public void onProduct(AssignmentsForTreeSearch product) {
                        String fingerprint = imageSlice.computeJpg(product).getFingerprint();
                        if (jpgs.add(fingerprint)) {
//                            System.out.println(fingerprint);
                        }
                    }
                });
                treeSearch.start(csp);
                System.err.println("\t\t jpgCount: " + jpgs.size());
                totalJpgCount += jpgs.size();

//                treeSearch.printCounts();

            }
        }
        System.err.println(threedModel.getSeriesKey() + " - Final jpg count: " + totalJpgCount);
//        System.err.println();

    }


    public void testExists() throws Exception {
        String fp = "0e24056-2a308f6";

        SeriesKey seriesKey = SeriesKey.AVALON_2011;
        SeriesRepo seriesRepo = repos.getSeriesRepo(seriesKey);
        RtRepo genRepo = seriesRepo.getRtRepo();


        JpgKey jpgKey = new JpgKey(seriesKey, JpgWidth.W_STD, fp);
        boolean exists = genRepo.exists(jpgKey);
        System.out.println("exists = " + exists);

    }

    public void test_countJpgsForSlice() throws Exception {
        ThreedModel threedModel = getTundra();
        ViewKey view = threedModel.exteriorViewKey;
        int angle = 2;

        Slice slice = new Slice(view.getName(), angle);

        long jpgCount = countJpgsForSlice(threedModel, slice);

        System.out.println("jpgCount = " + jpgCount);


    }

    public long countJpgsForSlice(ThreedModel threedModel, final Slice slice) {
        FeatureModel fm = threedModel.getFeatureModel();
        final ImageSlice imageSlice = threedModel.getImageSlice(slice);

        Set<Var> careVars = new HashSet<Var>();
        careVars.addAll(imageSlice.getJpgVars());

        final CspForTreeSearch csp = fm.createCspForTreeSearch(careVars);

        csp.propagateSimplify();

        final TreeSearch treeSearch = new TreeSearch();

        final HashSet<String> set = new HashSet<String>();
        treeSearch.setProductHandler(new ProductHandler() {
            @Override
            public void onProduct(AssignmentsForTreeSearch product) {
                Jpg jpg = imageSlice.computeJpg(product);
                String fingerprint = jpg.getFingerprint();
                set.add(fingerprint);
            }
        });
        treeSearch.start(csp);
        return set.size();
    }


    public static class RepoJpgExistsChecker {

        SeriesKey seriesKey;
        JpgWidth jpgWidth;
        RtRepo genRepo;

        public RepoJpgExistsChecker(Repos repos, SeriesKey seriesKey, JpgWidth jpgWidth) {
            this.seriesKey = seriesKey;
            this.jpgWidth = jpgWidth;
            this.genRepo = repos.getSeriesRepo(seriesKey).getRtRepo();
        }

        public boolean exists(String fingerprint) {
            JpgKey jpgKey = new JpgKey(seriesKey, jpgWidth, fingerprint);
            return genRepo.exists(jpgKey);
        }
    }


    /*
       No careVarFilter:
           picks filter: {LTD,202,LG21}        satCount:  104960       (bdd 2903133365248)
               3s (bdd 6.7s)
           picks filter: {202,8272}            satCount:  5982720
           picks filter: {LTD,202,LG21,8272}   satCount:  52480        (bdd 2903133365248)
           picks filter: {202,LG21,8272}       satCount:  52480        (bdd 2903133365248)
               3s (bdd 6.7s)

       With careVarFilter(exterior-a2): satCount: 1820
           ???s     simplify in search: N   featureFilter: none
           ???s     simplify in search: Y   featureFilter: none
           2.7s     simplify in search: Y   featureFilter: none shallowCopy of topExpr
    */
    public void testTundra() throws Exception {

        ThreedModel threedModel = getTundra();

        FeatureModel fm = threedModel.getFeatureModel();


        Slice slice = threedModel.getSlice(ViewKey.EXTERIOR, 2);

        Collection<Var> outputVars1 = threedModel.getPngVarsForSlice1(slice);
        Collection<Var> outputVars2 = threedModel.getPngVarsForSlice2(slice);

        assert outputVars1.size() == outputVars2.size() : "outputVars1.size() == outputVars2.size() failed. " + outputVars1.size() + " " + outputVars2.size();

        CspForTreeSearch csp = fm.createCspForTreeSearch(outputVars2);

        final TreeSearch treeSearch = new TreeSearch();
        treeSearch.start(csp);
        treeSearch.printCounts();

    }


    /*
        bdd - no images, no fixup, no pre-processing: satCount =  2338560
           1.2S
                                                 155904
        No careVarFilter:   satCount: 2338560, 2338560,
            7.7s
            5.8  added conflict to sort
        With careVarFilter(exterior-a2): satCount: 864
            1.1s
     */
    public void testVenza() throws Exception {
        FeatureModel fm;
        ThreedModel threedModel = getVenza();

        final ImageSlice imageSlice = threedModel.getImageSlice("exterior", 2);
        fm = threedModel.getFeatureModel();


        Set<Var> outputVars = new HashSet<Var>();
        outputVars.addAll(imageSlice.getJpgVars());


        CspForTreeSearch csp;
        csp = fm.createCspForTreeSearch(outputVars);

        TreeSearch treeSearch = new TreeSearch();
        treeSearch.start(csp);
        treeSearch.printCounts();

    }




    /*
       From UI:
           Picks [8202, FM13, 040]
           Fixed [Root, Trim, Grade, Base, cab, Regular, bed, Standard, Engine, V6, Transmission, 5AT, drive, 2WD, ModelCode, 8202, Color, ExteriorColor, 040, InteriorColor, FM13, iColor, Graphite, iFabric, Fabric, Options, LayerPad1, LayerPad2, 13, Accessories]
           Fixed - outputVarsOnly: [Root, Trim, Grade, Base, cab, Regular, bed, Standard, Engine, V6, Transmission, 5AT, drive, 2WD, ModelCode, 8202, Color, ExteriorColor, 040, InteriorColor, FM13, iColor, Graphite, iFabric, Fabric, Options, LayerPad1, LayerPad2, 13, Accessories]

           Fixed - OutputVarsOnly (angle 2): [Base, Regular, Standard, 8202, 040, LayerPad1]
           Jpg:  3965612-d14e28c-961cdda-0d8cc79-f23716f-d94d9ec-450317c-43b4640-5a12705
                 3965612-d14e28c-961cdda-0d8cc79-f23716f-d94d9ec-450317c-43b4640-5a12705
                 3965612-d14e28c-961cdda-0d8cc79-f23716f-d94d9ec-450317c-43b4640-5a12705
                 3965612-d14e28c-0d8cc79-d94d9ec-450317c-43b4640-5a12705
                 3965612-d14e28c-961cdda-0d8cc79-f23716f-d94d9ec-450317c-43b4640-5a12705
                 3965612-d14e28c-961cdda-0d8cc79-f23716f-d94d9ec-450317c-43b4640-5a12705
    */
    public void testTundraTmp2() throws Exception {

        ThreedModel threedModel = getTundra();

        final FeatureModel fm = threedModel.getFeatureModel();
        final ImageSlice imageSlice = threedModel.getImageSlice("exterior", 2);


        Set<Var> outputVars = new HashSet<Var>();
//        outputVars.addAll(imageSlice.getJpgVars());
        outputVars.addAll(fm.getPickableVars());

        CspForTreeSearch csp = fm.createCspForTreeSearch(outputVars);

        csp.flattenTopLevelConflicts();
        csp.flattenTopLevelImplications();


        csp.assignTrue("8374");
//        csp.assignTrue("202");
//        csp.assignTrue("LF13");
        csp.propagateSimplify();

        TreeSearch treeSearch = new TreeSearch();


        long t1 = System.currentTimeMillis();
        treeSearch.start(csp);
        long t2 = System.currentTimeMillis();
        System.err.println("TreeSearch Delta: " + (t2 - t1));

        treeSearch.printCounts();


    }


    public void testAvalon() throws Exception {
        FeatureModel fm;
        ThreedModel threedModel = getAvalon();

        ImageSlice imageSlice = threedModel.getImageSlice("exterior", 2);
        fm = threedModel.getFeatureModel();


        Set<Var> careVars = imageSlice.getJpgVars();


        CspForTreeSearch csp = fm.createCspForTreeSearch(careVars);


        final TreeSearch treeSearch = new TreeSearch();
        treeSearch.start(csp);
        treeSearch.printCounts();


    }


    public ThreedModel getAvalon() {
        return repos.getThreedModel(BrandKey.TOYOTA, "avalon", 2011);
    }

    public ThreedModel getVenza() {
        return repos.getThreedModel(BrandKey.TOYOTA,"venza", 2011);
    }

    public ThreedModel getTundra() {
        return repos.getThreedModel(BrandKey.TOYOTA,"tundra", 2011);
    }

    public ThreedModel getTacoma() {
        return repos.getThreedModel(BrandKey.TOYOTA,"tacoma", 2011);
    }

    private CspForTreeSearch buildCspComplexVehicle() {
        Camry2011 fm = new Camry2011();
        CspForTreeSearch csp = fm.createCspForTreeSearch();
        return csp;
    }

}
