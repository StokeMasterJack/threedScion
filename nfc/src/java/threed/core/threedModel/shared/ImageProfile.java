package threed.core.threedModel.shared;

import smartsoft.util.lang.shared.ImageSize;

public class ImageProfile {

    private final String key;
    private final ImageSize size;

    public ImageProfile(String key, ImageSize size) {
        this.key = key;
        this.size = size;
    }

    public String getKey() {
        return key;
    }

    public ImageSize getSize() {
        return size;
    }

}
