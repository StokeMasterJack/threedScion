package c3i.core.imageModel.shared;

public enum ImageMode {
    JPG, JPG_SKIP_Z_LAYERS, PNG;

    public boolean isPngMode() {
        return this == PNG;
    }

    public boolean isSkipZLayers() {
        return this == JPG_SKIP_Z_LAYERS;
    }

    public boolean isJpgMode() {
        return this == JPG;
    }
}
