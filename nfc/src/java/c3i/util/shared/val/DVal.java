package c3i.util.shared.val;

import c3i.util.shared.events.ChangeListener;

public interface DVal<T> extends Val<T> {
    void addValueChangeListener(ChangeListener<T> listener);
}
