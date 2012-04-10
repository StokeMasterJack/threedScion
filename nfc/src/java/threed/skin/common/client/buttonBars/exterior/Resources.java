package threed.skin.common.client.buttonBars.exterior;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {

    Resources INSTANCE = GWT.create(Resources.class);

    ImageResource nextButton();

    ImageResource prevButton();
}
