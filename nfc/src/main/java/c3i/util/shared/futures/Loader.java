package c3i.util.shared.futures;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Loader<K, V> {

    private final K input;
    private final FutureCompleter<V> completer;
    private final String functionName;

    public Loader(K input, AsyncFunction<K, V> asyncFunction) {
        this(input, asyncFunction, null);
    }

    public Loader(K input, AsyncFunction<K, V> asyncFunction, String functionName) {
        this.input = input;
        this.completer = Futures.createCompleter();

        if (functionName == null) {
            this.functionName = input.toString();
        } else {
            this.functionName = functionName;
        }

        try {
            asyncFunction.start(input, completer);
        } catch (Throwable e) {
            String msg = "Error attempting to call asyncFunction.start(" + input + ") on loader named: [" + functionName + "]";
            log.log(Level.SEVERE, msg, e);
            completer.setException(e);
        }
    }

    public Future<V> ensureLoaded() {
        return completer.getFuture();
    }

    private static Logger log = Logger.getLogger(Loader.class.getName());

}
