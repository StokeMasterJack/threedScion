package c3i.core.threedModel.shared;

import smartsoft.util.lang.shared.RectSize;

public class ImageProfile {

    private final String key;
    private final RectSize size;

    public ImageProfile(String key, RectSize size) {
        this.key = key;
        this.size = size;
    }

    public String getKey() {
        return key;
    }

    public RectSize getSize() {
        return size;
    }

}
