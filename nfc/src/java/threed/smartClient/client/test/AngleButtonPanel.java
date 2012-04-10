package threed.smartClient.client.test;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;

public class AngleButtonPanel extends FlowPanel {

    private Listener listener;

    private final Button bPrevious = new Button("<<");
    private final Button bNext = new Button(">>");

    public AngleButtonPanel() {
        add(bPrevious);
        add(bNext);


        bPrevious.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (listener != null) {
                    listener.onPrevious();
                }
            }
        });

        bNext.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (listener != null) {
                    listener.onNext();
                }
            }
        });
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public static interface Listener {

        void onNext();

        void onPrevious();

    }
}
