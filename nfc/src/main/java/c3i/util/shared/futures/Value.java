package c3i.util.shared.futures;

import c3i.util.shared.events.ChangeListener;
import c3i.util.shared.events.ValueChangeTopic;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class Value<VT> implements RWValue<VT> {

    private final ValueChangeTopic<VT> changeTopic;

    private String name;
    private VT value;

    public Value(String name, VT initialValue) {
        Preconditions.checkNotNull(name);
        this.name = name;
        this.value = initialValue;
        changeTopic = new ValueChangeTopic<VT>("ValueChangeTopic for Value[" + name + "]");
    }


    public Value(String name) {
        this(name, null);
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
