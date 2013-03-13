package c3i.util.shared.val;

import c3i.util.shared.events.ChangeListener;
import c3i.util.shared.events.LoadState;

public class OneOffAsyncValue<T> implements AVal<T> {

    private final AsyncValue<T> value = new AsyncValue<T>();

    private AsyncLoader<T> asyncLoader;

    public void setAsyncLoader(AsyncLoader<T> asyncLoader) {
        this.asyncLoader = asyncLoader;
    }

    public void start() {
        if (asyncLoader == null) throw new IllegalStateException();
        value.start();
        asyncLoader.start(value);
    }

    @Override
    public LoadState getLoadState() {
        return value.getLoadState();
    }

    @Override
    public Throwable getException() {
        return value.getException();
    }

    @Override
    public void addLoadStateChangeListener(ChangeListener<LoadState> listener) {
        value.addLoadStateChangeListener(listener);
    }

    @Override
    public void addValueChangeListener(ChangeListener<T> listener) {
        value.addValueChangeListener(listener);
    }

    @Override
    public T get() {
        return value.get();
    }
}
