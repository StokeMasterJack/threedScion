package c3i.smartClient.client.model.event;

import c3i.imageModel.shared.ViewKey;
import c3i.util.shared.events.Topic1;

public class ViewChangeTopic extends Topic1<ViewChangeListener, ViewKey> {

    public ViewChangeTopic() {
        super("ViewChangeTopic");
    }

    @Override
    protected void send(ViewChangeListener listener, ViewKey arg) {
        listener.onChange(arg);
    }

    @Override
    public void fire(ViewKey arg) {
        super.fire(arg);
    }


}
