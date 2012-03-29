package smartClient.client;

import com.google.common.base.Objects;
import smartsoft.util.gwt.client.events3.ChangeListener;
import smartsoft.util.gwt.client.events3.ChangeTopic;

public class ImageModeSession {

    private final ChangeTopic<ImageModeSession, ImageMode> imageModeChange = new ChangeTopic(this);

    private ImageMode imageMode;

    public ImageModeSession(ImageMode initialValue) {
        this.imageMode = initialValue;
    }

    public ImageModeSession() {
        imageMode = ImageMode.PNG;
    }

    public ImageMode getImageMode() {
        return imageMode;
    }

    public void setImageMode(ImageMode newValue) {
        ImageMode oldValue = this.imageMode;
        if (!Objects.equal(oldValue, newValue)) {
            this.imageMode = newValue;
            imageModeChange.fire(oldValue, newValue);
        }
    }

    public void addImageModeChangeListener(ChangeListener<ImageModeSession, ImageMode> listener) {
        imageModeChange.addListener(listener);
    }

    public boolean isPngMode(){
        return imageMode.equals(ImageMode.PNG);
    }


}
