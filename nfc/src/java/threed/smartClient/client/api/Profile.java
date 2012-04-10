package threed.smartClient.client.api;

import threed.core.threedModel.shared.JpgWidth;
import smartsoft.util.lang.shared.ImageSize;

import javax.annotation.concurrent.Immutable;

@Immutable
public class Profile {

    private final String key;
    private final ImageSize image;
    private final ImageSize srcPng;

    private final JpgWidth jpgWidth;

    public Profile(String key, ImageSize image, ImageSize srcPng) {
        this.key = key;
        this.image = image;
        this.srcPng = srcPng;
        jpgWidth = new JpgWidth(key, image.getWidth());
    }

    public ImageSize getImage() {
        return image;
    }

    public ImageSize getSrcPng() {
        return srcPng;
    }

    public String getKey() {
        return key;
    }

    public JpgWidth getJpgWidth() {
        return jpgWidth;
    }
}
