package com.tms.threed.previewPanel.client;

import smartClient.client.ThreedSession;

public class ViewPanelModel {

//    private final ImmutableList<Path> urls;
    private final ThreedSession threedSession;
    private final int panelIndex;

//    private final ImageBatchLoader imageBatchLoader;

    public ViewPanelModel(ThreedSession threedSession, int panelIndex) {
        this.threedSession = threedSession;
        this.panelIndex = panelIndex;



//        imageBatchLoader = new ImageBatchLoader();
    }


}
