package c3i.util.shared.futures;

import com.google.common.base.Objects;
import c3i.util.shared.events.ChangeListener;
import c3i.util.shared.events.ValueChangeTopic;

public class Value<VT> implements RWValue<VT> {

    private final ValueChangeTopic<VT> changeTopic = new ValueChangeTopic<VT>();

    private VT value;

    public Value(VT initialValue) {
        this.value = initialValue;
    }


    public Value() {
        value = null;
    }

    public VT get() {
        return value;
    }


    public void set(VT newValue) {
        VT oldValue = this.value;
        if (!Objects.equal(oldValue, newValue)) {
            this.value = newValue;
            changeTopic.fire(newValue);
        }
    }

    public void addChangeListener(ChangeListener<VT> listener) {
        changeTopic.add(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener<VT> listener) {
        changeTopic.remove(listener);
    }

    @Override
    public void removeAll() {
        changeTopic.removeAll();
    }


    public void suspend() {
        changeTopic.suspend();
    }

    public void resume() {
        changeTopic.resume();
    }

    public void forceFireChangeEvent() {
        changeTopic.fire(value);
    }

    public boolean isEmpty() {
        return value == null;
    }
}
