package c3i.smartClient.client.model.event;

import c3i.imageModel.shared.AngleKey;
import c3i.util.shared.events.Topic1;

public class AngleChangeTopic extends Topic1<AngleChangeListener, AngleKey> {

    public AngleChangeTopic() {
        super("AngleChangeTopic");
    }

    @Override
    protected void send(AngleChangeListener listener, AngleKey arg) {
        listener.onChange(arg);
    }

    @Override
    public void fire(AngleKey arg) {
        super.fire(arg);
    }


}
