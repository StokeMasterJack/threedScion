package com.tms.threed.threedFramework.previewPane.client.notification;

public abstract class AccessoryWithFlashOrientationHandler {
    public void handleEvent(String eventName, int flashOrientation) {
        this.handleEvent(flashOrientation);
    }

    abstract public void handleEvent(int flashOrientation);
}
