package c3i.core.imageModel.test;

import c3i.core.imageModel.server.PngVars;
import junit.framework.TestCase;

import java.io.File;
import java.util.Set;

public class PngVarsTest extends TestCase {

    static String expectedVarCodeString = "[Double, Graphite, Regular, HomeLink, Black, NV, SportFabric, 8S6, Work, TM, 3L5, DZ, Overhead, 3R3, LayerPad1, 2WD, LTD, 4T3, 202, Red, 6V4, Crew, Bucket, 1D6, Beige, EV, MO, SR, PT, 4WD, 040, 1G3, SR5, Vinyl, EJ, BedRug]";
    static int expectedVarCodeCount = 36;

    static String viewName = "interior";
    static int angle = 2;

    File repoDir;
    File seriesDir;
    File seriesYearDir;
    File modelXml;
    File viewDir;

    @Override
    protected void setUp() throws Exception {
        repoDir = new File("/configurator-content-toyota");
        seriesDir = new File(repoDir, "tundra");
        seriesYearDir = new File(seriesDir, "2014");
        modelXml = new File(seriesYearDir, "model.xml");
        viewDir = new File(seriesYearDir, viewName);
    }

    public void test_getPngVars() throws Exception {
        Set<String> pngVarCodes = PngVars.getPngVars(viewDir, angle);
        assertEquals(expectedVarCodeCount, pngVarCodes.size());
        assertEquals(expectedVarCodeString, pngVarCodes.toString());
    }

}

