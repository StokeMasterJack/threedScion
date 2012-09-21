package c3i.smartClient.client.widgets.dragToSpin;

import com.google.common.base.Preconditions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.lang.shared.RectSize;

public class ClearGif extends Image {

    public ClearGif(RectSize imageSize) {
        super(GWT.getModuleBaseURL() + "clear.cache.gif");
        setPixelSize(imageSize.getWidth(), imageSize.getHeight());


    }

    public void setImageSize(RectSize imageSize) {
        setPixelSize(imageSize.getWidth(), imageSize.getHeight());
    }

    public void setPixelSize(RectSize imageSize) {
        Preconditions.checkNotNull(imageSize);
        this.setPixelSize(imageSize.getWidth(), imageSize.getHeight());
    }
}
