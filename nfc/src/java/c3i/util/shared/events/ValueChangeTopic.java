package c3i.util.shared.events;

import com.google.common.base.Objects;
import smartsoft.util.gwt.client.Console;

public class ValueChangeTopic<VT> extends Topic1<ChangeListener<VT>, VT> {

    @Override
    public void fire(VT newValue) {
        super.fire(newValue);
    }

    @Override
    protected void send(ChangeListener<VT> listener, VT newValue) {
        try {
            listener.onChange(newValue);
        } catch (Throwable e) {
            Console.error(e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void maybeFire(VT oldValue, VT newValue) {
        if (Objects.equal(oldValue, newValue)) {
            //do nothing
        } else {
            fire(newValue);
        }
    }

}
