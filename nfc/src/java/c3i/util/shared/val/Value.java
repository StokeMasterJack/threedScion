package c3i.util.shared.val;

import c3i.util.shared.events.ChangeListener;
import c3i.util.shared.events.ValueChangeTopic;

import javax.annotation.Nullable;

public class Value<T> implements WVal<T> {

    private final ValueChangeTopic<T> valueChange = new ValueChangeTopic<T>();
    private T value;

    public Value(T value) {
        this.value = value;
    }

    public Value() {
    }

    @Override
    public void set(T newValue) {
        if (change(newValue)) {
            this.value = newValue;
            valueChange.fire(newValue);
        }
    }

    @Override
    public void addValueChangeListener(ChangeListener<T> listener) {
        valueChange.add(listener);
    }

    @Override
    public T get() {
        return this.value;
    }

    private boolean change(T newValue) {
        return !valueEquals(newValue);
    }

    public boolean valueEquals(T newValue) {
        return eq(this.value, newValue);
    }

    public static boolean eq(@Nullable Object a, @Nullable Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public void resume() {
        valueChange.resume();
    }

    public void suspend() {
        valueChange.suspend();
    }

    public int getListenerCount() {
        return valueChange.getListenerCount();
    }
}
