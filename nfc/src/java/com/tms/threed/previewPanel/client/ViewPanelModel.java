package com.tms.threed.previewPanel.client;

import com.tms.threed.smartClients.gwt.client.ThreedSession;

public class ViewPanelModel {

    private final ThreedSession threedSession;
    private final int panelIndex;

    public ViewPanelModel(ThreedSession threedSession, int panelIndex) {
        this.threedSession = threedSession;
        this.panelIndex = panelIndex;
    }



}
