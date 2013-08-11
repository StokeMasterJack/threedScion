package c3i.core.featureModel.shared.search;

import c3i.core.featureModel.shared.Csp;
import c3i.core.featureModel.shared.CspTestBase;
import c3i.core.featureModel.shared.SatCountProductHandler;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class CspFindAllTest extends CspTestBase {


    @Test
    public void simple() throws Exception {

        Csp csp = buildCspSimpleVehicle();
        SatCountProductHandler counter = new SatCountProductHandler();

        csp.findAll(counter);

        assertEquals(11, counter.getCount());

    }

    @Test
    public void medium() throws Exception {

        Csp csp = buildCspMediumVehicle();
        SatCountProductHandler counter = new SatCountProductHandler();

        csp.findAll(counter);

        assertEquals(227, counter.getCount());

    }

    @Test
    public void complex() throws Exception {
        if (true) return;  //todo

        Csp csp = buildCspComplexVehicle();
        SatCountProductHandler counter = new SatCountProductHandler();

        csp.findAll(counter);

        assertEquals(2080512, counter.getCount());
    }


}
