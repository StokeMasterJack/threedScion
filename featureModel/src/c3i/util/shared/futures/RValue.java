package c3i.util.shared.futures;

import c3i.util.shared.events.ChangeListener;

public interface RValue<VT> {

    VT get();

    void addChangeListener(ChangeListener<VT> listener);

    void removeChangeListener(ChangeListener<VT> listener);

    void removeAll();

    boolean isEmpty();
}
