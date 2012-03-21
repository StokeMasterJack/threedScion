package com.tms.threed.previewPanel.client;

import com.google.common.collect.ImmutableList;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.tms.threed.smartClients.gwt.client.ThreedSession;
import smartsoft.util.lang.shared.Path;

import java.util.List;

public class ViewPanelModel {

//    private final ImmutableList<Path> urls;
    private final ThreedSession threedSession;
    private final int panelIndex;

//    private final ImageBatchLoader imageBatchLoader;

    public ViewPanelModel(ThreedSession threedSession, int panelIndex) {
        this.threedSession = threedSession;
        this.panelIndex = panelIndex;

        threedSession.addUrlChangeHandler(new ValueChangeHandler<ImmutableList<Path>>() {
            @Override
            public void onValueChange(ValueChangeEvent<ImmutableList<Path>> event) {

            }
        });

//        imageBatchLoader = new ImageBatchLoader();
    }


}
