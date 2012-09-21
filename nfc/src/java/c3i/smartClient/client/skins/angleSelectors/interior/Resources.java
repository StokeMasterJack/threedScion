package c3i.smartClient.client.skins.angleSelectors.interior;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {

    Resources INSTANCE = GWT.create(Resources.class);

    ImageResource angle1ButtonUp();

    ImageResource angle1ButtonDown();

    ImageResource angle2ButtonUp();

    ImageResource angle2ButtonDown();

    ImageResource angle3ButtonUp();

    ImageResource angle3ButtonDown();
}