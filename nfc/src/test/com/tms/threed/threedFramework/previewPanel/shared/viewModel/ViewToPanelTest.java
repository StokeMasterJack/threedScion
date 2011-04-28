package com.tms.threed.threedFramework.previewPanel.shared.viewModel;

import junit.framework.TestCase;

public class ViewToPanelTest extends TestCase {

    static final int V0 = 0, V1 = 1, V2 = 2 ;
    static final int P0 = 0, P1 = 1, P2 = 2;

    public void test_getPanel() throws Exception {
        VS vs = new VS(3);

        vs.setViewForPanelZero(V0);
        assertEquals(P0, vs.getPanel(V0));
        assertEquals(P1, vs.getPanel(V1));
        assertEquals(P2, vs.getPanel(V2));

        vs.setViewForPanelZero(V1);
        assertEquals(P2, vs.getPanel(V0));
        assertEquals(P0, vs.getPanel(V1));
        assertEquals(P1, vs.getPanel(V2));

        vs.setViewForPanelZero(V2);
        assertEquals(P1, vs.getPanel(V0));
        assertEquals(P2, vs.getPanel(V1));
        assertEquals(P0, vs.getPanel(V2));
    }

    public void test_getView() throws Exception {
        VS vs = new VS(3);

        vs.setViewForPanelZero(V0);
        assertEquals(V0, vs.getView(P0));
        assertEquals(V1, vs.getView(P1));
        assertEquals(V2, vs.getView(P2));

        vs.setViewForPanelZero(V1);
        assertEquals(V1, vs.getView(P0));
        assertEquals(V2, vs.getView(P1));
        assertEquals(V0, vs.getView(P2));

        vs.setViewForPanelZero(V2);
        assertEquals(V2, vs.getView(P0));
        assertEquals(V0, vs.getView(P1));
        assertEquals(V1, vs.getView(P2));
    }




}
