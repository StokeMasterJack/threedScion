package com.tms.threed.threedFramework.previewPanel.client.previewPanelModel.events;

import com.tms.threed.threedFramework.util.gwtUtil.client.events2.SimpleEvent;
import com.tms.threed.threedFramework.util.lang.shared.Path;

public class UrlChangeEvent extends SimpleEvent {

    private final Path newUrl;

    public UrlChangeEvent(Path newUrl) {
        this.newUrl = newUrl;
    }

    public Path getNewUrl() {
        return newUrl;
    }
}
