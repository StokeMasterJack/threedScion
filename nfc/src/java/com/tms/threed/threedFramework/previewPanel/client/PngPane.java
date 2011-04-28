package com.tms.threed.threedFramework.previewPanel.client;

import com.google.gwt.user.client.ui.AbsolutePanel;

public class PngPane extends AbsolutePanel {

    public PngPane() {
        this.setSize("599px", "366px");
    }

    public void refresh() {
        this.clear();

//        List<ImPng> pngList = session.getPngs();
//        if (pngList == null) return;
//        for (Png png : pngList) {
//            if (!png.isVisible()) continue;
//            if (!png.getLayer().isVisible()) continue;
//            String url = png.getPath(pngRootUrl) + "";
//            Image image = new Image();
//            image.setSize("599px", "366px");
//            this.add(image, 0, 0);
//            image.setUrl(url);
//        }

    }

}
