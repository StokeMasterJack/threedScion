package c3i.util.shared.futures;

import smartsoft.util.gwt.client.Console;

public class Loader<K, V> {

    private final K input;
    private final Completer<V> completer;

    public Loader(K input, AsyncFunction<K, V> asyncFunction) {
        this.input = input;
        this.completer = Futures.createCompleter();
        try {
            asyncFunction.start(input, completer);
        } catch (Throwable e) {
            Console.error(e);
            completer.setException(e);
        }
    }

    public Future<V> ensureLoaded() {
        return completer.getFuture();
    }

}
