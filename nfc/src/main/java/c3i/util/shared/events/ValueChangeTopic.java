package c3i.util.shared.events;

import com.google.common.base.Objects;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ValueChangeTopic<VT> extends Topic1<ChangeListener<VT>, VT> {

    public ValueChangeTopic(String name) {
        super(name);
    }

    @Override
    public void fire(VT newValue) {
        super.fire(newValue);
    }

    @Override
    protected void send(ChangeListener<VT> listener, VT newValue) {
        try {
            listener.onChange(newValue);
        } catch (Throwable e) {
            log.log(Level.SEVERE, "error", e);
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

    private static Logger log = Logger.getLogger(ValueChangeTopic.class.getName());

}
