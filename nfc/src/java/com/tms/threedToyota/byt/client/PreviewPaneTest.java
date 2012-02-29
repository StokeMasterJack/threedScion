package com.tms.threedToyota.byt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.tms.threedToyota.byt.client.externalState.raw.ExternalStateSnapshot;
import com.tms.threedToyota.byt.client.main.PreviewPaneMain;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;

import java.util.HashMap;
import java.util.Map;

public class PreviewPaneTest implements EntryPoint {

    private PreviewPaneMain previewPane;

    public void onModuleLoad() {
        previewPane = new PreviewPaneMain();
        RootLayoutPanel.get().add(previewPane);

        firstCallFromLeftSide();
    }

    private void firstCallFromLeftSide() {

        final ExternalStateSnapshot newState;

        SeriesKey seriesKey = previewPane.getSeriesKey();

        if (seriesKey.isa(SeriesKey.CAMRY)) {
            //[Fabric, Ash, 040, 13, 2513]
            newState = new ExternalStateSnapshot("2513", "040", "13", null, null, "$33,333", null, null, null, null);
//            newState = new ExternalStateSnapshot("camry", "2011", "2513", "040", "13", "CF", "WB", "$33,333", null, null, null, null);
            addAccessoryPickButtons();
        } else if (seriesKey.isa(SeriesKey.VENZA)) {
            newState = new ExternalStateSnapshot("2810", "202", "FA01", null, null, "22,222", "67938111", "http://www.google.com", null, null);
        } else if (seriesKey.isa(SeriesKey.AVALON)) {
            newState = new ExternalStateSnapshot( "3544", "1F7", "FE17", "CF", "EJ", "$55,555", null, null, null, null);
        } else {
            throw new IllegalStateException("Unknown Series Key: " + seriesKey);
        }
        previewPane.updateImage2(newState);
    }


    String msrp = "$33,333";
    Map<String, Boolean> map = new HashMap<String, Boolean>();

    private void addButton(final String code, final String name) {
        final Button b = new Button();
        b.setText("Add " + name);
        b.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                map.put(code, !isPicked(code));
                b.setText(getCaption(code, name));
                previewPane.updateImage2(getPicks());
            }
        });
        RootPanel.get().add(b);
    }

    ExternalStateSnapshot getPicks() {
        String accessoryCodes = "WB";
        for (String code : map.keySet()) {
            if (isPicked(code)) accessoryCodes += "," + code;
        }
        return new ExternalStateSnapshot( "2513", "040", "13", "CF", accessoryCodes, "$33,333", null, null, null, null);
//        return new RawPicksSnapshot("2513", "040", "13", "CF", accessoryCodes);
    }

    String getCaption(String code, String name) {
        String prefix = isPicked(code) ? "Remove" : "Add";
        return prefix + " " + name;
    }

    boolean isPicked(String code) {
        Boolean v = map.get(code);
        return v != null && v;
    }


    private void addAccessoryPickButtons() {
        addButton("BM", "Body Molding");
        addButton("E5", "Exhaust Tip");
    }


}