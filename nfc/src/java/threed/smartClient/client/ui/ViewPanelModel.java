package threed.smartClient.client.ui;

import threed.smartClient.client.api.ImageChangeListener;
import threed.smartClient.client.api.ImageStack;
import threed.smartClient.client.api.ViewSession;
import smartsoft.util.gwt.client.events3.ChangeListener;
import smartsoft.util.lang.shared.ImageSize;


//@Export
public interface ViewPanelModel {

    ImageSize getImageSize();

    ViewSession getViewSession();

    ImageStack getImageStack();

    boolean isVisible();

    void addImageChangeListener1(ImageChangeListener listener);

    void addImageChangeListener2(ImageChangeListener listener);

    void addAngleChangeListener(ChangeListener<ViewSession, Integer> listener);
}
