package c3i.smartClient.client.model.event;

import c3i.util.shared.events.Topic1;
import c3i.core.imageModel.shared.AngleKey;

public class AngleChangeTopic extends Topic1<AngleChangeListener, AngleKey> {

    @Override
    protected void send(AngleChangeListener listener, AngleKey arg) {
        listener.onChange(arg);
    }

    @Override
    public void fire(AngleKey arg) {
        super.fire(arg);
    }


}
