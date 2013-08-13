package c3i.util.shared.futures;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractAsync<I, T> implements AsyncFunction<I, T> {

    protected final String name;

    protected AbstractAsync(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    abstract public void start(I arg, Completer<T> completer) throws Exception;

    final public Future<T> start(final I arg) {
        FutureCompleter<T> completer = Futures.createCompleter();
        Future<T> future = completer.getFuture();

        future.failure(new OnException() {
            @Override
            public boolean onException(Throwable e) {
                String msg = "error attempting to call asyncFunction " + name + "(" + arg + ")";
                log.log(Level.SEVERE, msg, e);
                return false;
            }
        });

        try {
            this.start(arg, completer);
        } catch (Exception e) {
            String msg = "Uncaught exception while attempting to call asyncFunction " + name + "(" + arg + ") on loader";
            log.log(Level.SEVERE, msg, e);
            completer.setException(e);
        }

        return future;
    }

    private static Logger log = Logger.getLogger(AbstractAsync.class.getName());


}
