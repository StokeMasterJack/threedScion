package c3i.core.featureModel.shared.search;

import c3i.core.featureModel.shared.Csp;
import c3i.core.featureModel.shared.CspTestBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class CspSatCountTest extends CspTestBase {


    @Test
    public void simple() throws Exception {
        Csp csp = buildCspSimpleVehicle();
        long satCount = csp.satCount();
        assertEquals(11, satCount);
    }

    @Test
    public void medium() throws Exception {
        Csp csp = buildCspMediumVehicle();
        long satCount = csp.satCount();
        assertEquals(227, satCount);
    }

    @Test
    public void complex() throws Exception {
        if (true) return;     //todo
        Csp csp = buildCspComplexVehicle();
        long satCount = csp.satCount();
        assertEquals(2080512, satCount);
    }


}
