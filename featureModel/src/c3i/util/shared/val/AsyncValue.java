package c3i.util.shared.val;

import c3i.util.shared.events.ChangeListener;
import c3i.util.shared.events.LoadState;

import javax.annotation.Nullable;

public class AsyncValue<T> extends Value<T> implements WAVal<T>, Completer<T> {

    private final Value<LoadState> loadStateChange = new Value<LoadState>();
    private Throwable exception;

    @Override
    public void start() {
        loadStateChange.set(LoadState.LOADED);
    }

    @Override
    public void setException(Throwable e) {
        this.exception = e;
        loadStateChange.set(LoadState.FAILED);
    }

    @Override
    public void set(@Nullable T newValue) {
        loadStateChange.set(LoadState.LOADED);
        super.set(newValue);
    }

    public void resume() {
        super.resume();
        loadStateChange.resume();
    }

    public void suspend() {
        super.suspend();
        loadStateChange.suspend();
    }

    public int getLoadStateListenerCount() {
        return loadStateChange.getListenerCount();
    }

    @Override
    public LoadState getLoadState() {
        return loadStateChange.get();
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    @Override
    public void addLoadStateChangeListener(ChangeListener<LoadState> listener) {
        loadStateChange.addValueChangeListener(listener);
    }
}
