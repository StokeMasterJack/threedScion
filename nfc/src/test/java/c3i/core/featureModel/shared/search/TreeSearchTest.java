package c3i.core.featureModel.shared.search;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.data.Camry2011;
import c3i.core.featureModel.data.Trim;
import c3i.core.featureModel.data.TrimColor;
import c3i.core.featureModel.data.TrimColorOption;
import c3i.core.featureModel.shared.AssignmentsForTreeSearch;
import c3i.core.featureModel.shared.CspForTreeSearch;
import c3i.core.featureModel.shared.CspTestBase;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.imageModel.shared.CoreImageStack;
import c3i.core.imageModel.shared.ImView;
import c3i.core.imageModel.shared.ImageMode;
import c3i.core.imageModel.shared.Profile;
import c3i.core.imageModel.shared.RawImageStack;
import c3i.core.imageModel.shared.ViewKeyOld;
import c3i.core.imageModel.shared.ViewSlice;
import c3i.core.threedModel.server.TmToJsonJvm;
import c3i.core.threedModel.shared.BaseImageKey;
import c3i.core.threedModel.shared.JpgWidth;
import c3i.core.threedModel.shared.Slice;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.repo.server.Repos;
import c3i.repo.server.SeriesRepo;
import c3i.repo.server.rt.RtRepo;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class TreeSearchTest extends CspTestBase {

    Repos repos;

    @Before
    public void setup() throws Exception {
        repos = Repos.getToyotaRepoTest();
    }


    @Test
    public void testTrim() throws Exception {

        FeatureModel fm = new Trim();
        CspForTreeSearch csp = fm.createCspForTreeSearch();

        final HashSet<Set<Var>> products = new HashSet<Set<Var>>();

        TreeSearch treeSearch = new TreeSearch();

        treeSearch.start(csp);
        treeSearch.printCounts();


    }


    @Test
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
    @Test
    public void testCamry2011() throws Exception {

        Camry2011 fm = new Camry2011();
        CspForTreeSearch csp = fm.createCspForTreeSearch();

        TreeSearch treeSearch = new TreeSearch();
        treeSearch.start(csp);

        System.out.println("satCount = " + treeSearch.getSolutionCount());


    }


    @Test
    public void test_Reduce_ComplexVehicle() throws Exception {
        CspForTreeSearch csp = (CspForTreeSearch) buildCspComplexVehicle();
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


    @Test
    public void testTundraJpgs() throws Exception {
        SeriesKey sk = new SeriesKey(BrandKey.TOYOTA, 2011, "tundra");
        SeriesId head = repos.getHead(sk);
        System.err.println("rootTreeId[" + head + "]");
        ThreedModel tm = repos.getThreedModel(head);
        String tmJson = TmToJsonJvm.toJson(tm);
    }


    @Test
    public void testAvalonJpgs() throws Exception {
        jpgCounter(getAvalon());
    }

    @Test
    public void testVenzaJpgs() throws Exception {
        jpgCounter(getVenza());
    }


    private void jpgCounter(ThreedModel threedModel) {
        final Profile profile = new Profile(JpgWidth.W_STD);
        long totalJpgCount = 0;
        FeatureModel fm = threedModel.getFeatureModel();
        for (ImView view : threedModel.getImageModel().getViews()) {
            System.err.println(view.getName());
            for (int angle = 1; angle <= view.getAngleCount(); angle++) {
                final Set<String> jpgs = new HashSet<String>();
                System.err.println("\t angle: " + angle);

                final ViewSlice viewSlice = view.getViewSlice(angle);

                Set<Var> pngVars = viewSlice.getPngVars();
                Set<Var> careVars = new HashSet<Var>();
                careVars.addAll(pngVars);

                CspForTreeSearch csp = fm.createCspForTreeSearch(careVars);
                csp.propagateSimplify();
                final TreeSearch treeSearch = new TreeSearch();
                treeSearch.setProductHandler(new ProductHandler() {
                    @Override
                    public void onProduct(AssignmentsForTreeSearch product) {
                        RawImageStack rawImageStack = viewSlice.getRawImageStack(product);
                        CoreImageStack coreImageStack = rawImageStack.getCoreImageStack(profile, ImageMode.JPG);
                        String jpgFingerprint = coreImageStack.getBaseImageFingerprint();
                        if (jpgs.add(jpgFingerprint)) {
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


        BaseImageKey jpgKey = new BaseImageKey(seriesKey, Profile.STD, fp);
        boolean exists = genRepo.exists(jpgKey);
        System.out.println("exists = " + exists);

    }

    @Test
    public void test_countJpgsForSlice() throws Exception {
        ThreedModel threedModel = getTundra();
        int angle = 2;

        Slice slice = new Slice("exterior", angle);

        long jpgCount = countJpgsForSlice(threedModel, slice);

        System.out.println("jpgCount = " + jpgCount);


    }

    private long countJpgsForSlice(ThreedModel threedModel, final Slice slice) {
        FeatureModel fm = threedModel.getFeatureModel();
        ImView view = threedModel.getView(slice.getViewName());
        final ViewSlice viewSlice = threedModel.getViewSlice(slice);

        Set<Var> careVars = new HashSet<Var>();
        careVars.addAll(viewSlice.getPngVars());

        final CspForTreeSearch csp = fm.createCspForTreeSearch(careVars);

        csp.propagateSimplify();

        final TreeSearch treeSearch = new TreeSearch();

        final HashSet<String> set = new HashSet<String>();
        treeSearch.setProductHandler(new ProductHandler() {
            @Override
            public void onProduct(AssignmentsForTreeSearch product) {
                RawImageStack rawImageStack = viewSlice.getRawImageStack(product);
                String jpgFingerprint = rawImageStack.getJpgFingerprint();
                set.add(jpgFingerprint);
            }
        });
        treeSearch.start(csp);
        return set.size();
    }


    private static class RepoJpgExistsChecker {

        SeriesKey seriesKey;
        JpgWidth jpgWidth;
        RtRepo genRepo;

        public RepoJpgExistsChecker(Repos repos, SeriesKey seriesKey, JpgWidth jpgWidth) {
            this.seriesKey = seriesKey;
            this.jpgWidth = jpgWidth;
            this.genRepo = repos.getSeriesRepo(seriesKey).getRtRepo();
        }

        public boolean exists(String fingerprint) {
            BaseImageKey jpgKey = new BaseImageKey(seriesKey, Profile.STD, fingerprint);
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


        Slice slice = threedModel.getSlice(ViewKeyOld.EXTERIOR, 2);

        Collection<Var> outputVars1 = threedModel.getPngVarsForSlice1(slice);
//        Collection<Var> outputVars2 = threedModel.getPngVarsForSlice2(slice);

//        assert outputVars1.size() == outputVars2.size() : "outputVars1.size() == outputVars2.size() failed. " + outputVars1.size() + " " + outputVars2.size();
//
//        CspForTreeSearch csp = fm.createCspForTreeSearch(outputVars2);
//
//        final TreeSearch treeSearch = new TreeSearch();
//        treeSearch.start(csp);
//        treeSearch.printCounts();

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

        ViewSlice viewSlice = threedModel.getViewSlice("exterior", 2);
        fm = threedModel.getFeatureModel();


        Set<Var> outputVars = new HashSet<Var>();
        outputVars.addAll(viewSlice.getPngVars());


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
        final ViewSlice imageSlice = threedModel.getViewSlice("exterior", 2);


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


    @Test
    public void testAvalon() throws Exception {
        FeatureModel fm;
        ThreedModel threedModel = getAvalon();

        ViewSlice imageSlice = threedModel.getViewSlice("exterior", 2);
        fm = threedModel.getFeatureModel();


        Set<Var> careVars = imageSlice.getPngVars();


        System.out.println(careVars);
        CspForTreeSearch csp = fm.createCspForTreeSearch(careVars);


        final TreeSearch treeSearch = new TreeSearch();
        treeSearch.start(csp);
        treeSearch.printCounts();


    }


    private ThreedModel getAvalon() {
        return repos.getThreedModel(BrandKey.TOYOTA, "avalon", 2011);
    }

    private ThreedModel getVenza() {
        return repos.getThreedModel(BrandKey.TOYOTA, "venza", 2011);
    }

    private ThreedModel getTundra() {
        SeriesKey sk = new SeriesKey(BrandKey.TOYOTA, 2011, "tundra");
        SeriesId head = repos.getHead(sk);
        System.err.println("rootTreeId[" + head + "]");
        System.err.println();
        System.err.println();
        System.err.println();
        System.err.println();
        return repos.getThreedModel(head);
    }


}
