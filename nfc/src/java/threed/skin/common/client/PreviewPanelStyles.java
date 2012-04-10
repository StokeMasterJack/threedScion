package threed.skin.common.client;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class PreviewPanelStyles {

    public static final String FONT_FAMILY = "Arial,Verdana,Helvetica,sans-serif";

    public static void set(Element element) {
        element.getStyle().setProperty("fontFamily", FONT_FAMILY);
    }

    public static void set(Widget w) {
        Element e = w.getElement();
        set(e);
    }

}
