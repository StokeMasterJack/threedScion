package com.tms.threed.threedCore.imageModel.shared;

public class PngMatch {

    private ImPng png;

    public void add(ImPng newPng) {
        assert newPng != null;
        if (png == null) {
            this.png = newPng;
        } else {
            if (newPng.hasFeature("2Q")) {
                System.out.println();
            }
            if (newPng.getFeatureCount() > png.getFeatureCount()) {
                this.png = newPng;
            }
        }
    }

    public ImPng getPng() {
        return png;
    }
}
