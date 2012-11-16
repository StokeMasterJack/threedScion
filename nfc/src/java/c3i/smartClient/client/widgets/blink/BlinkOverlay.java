package c3i.smartClient.client.widgets.blink;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import smartsoft.util.shared.Path;

public class BlinkOverlay extends Image {

    public BlinkOverlay() {
        setVisible(false);
    }

    public void doFeatureBlink(Path pngToBlink) {
        assert pngToBlink != null;
        String url = pngToBlink.toString();
        assert url != null;

        setVisible(false);
        setUrl(url);
        toggleVisibility(0);
    }

    private void toggleVisibility(final int count) {
        setVisible(!isVisible());

        if (count > 10) {
            setVisible(false);
            return;
        }
        Timer t = new Timer() {
            @Override public void run() {
                toggleVisibility(count + 1);
            }
        };
        t.schedule(100);
    }
}
