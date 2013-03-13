package c3i.util.shared.val;

import c3i.util.shared.events.ChangeListener;
import c3i.util.shared.events.LoadState;

public interface AVal<T> extends DVal<T> {

    LoadState getLoadState();

    Throwable getException();

    void addLoadStateChangeListener(ChangeListener<LoadState> listener);
}
