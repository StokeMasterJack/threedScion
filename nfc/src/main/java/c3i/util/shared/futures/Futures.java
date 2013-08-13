package c3i.util.shared.futures;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * [Futures] holds additional utility functions that operate on [Future]s (for
 * example, waiting for a collection of Futures to complete).
 */
public class Futures {

    public static <T> Future immediate(T value) {
        return FutureImpl.immediate(value);
    }


    /**
     * Returns a future which will complete once all the futures in a list are
     * complete. If any of the futures in the list completes with an exception,
     * the resulting future also completes with an exception. (The value of the
     * returned future will be a list of all the values that were produced.)
     */
    public static Future<List> wait(List<Future> futures) {
        if (futures.isEmpty()) {
            return immediate(ImmutableList.of());
        }

        final FutureCompleter<List> completer = new CompleterImpl<List>();
        final Future<List> result = completer.getFuture();
        final int[] remaining = {futures.size()};
        final List<Object> values = new ArrayList(futures.size());

        // As each future completes, put its value into the corresponding
        // position in the list of values.
        for (int i = 0; i < futures.size(); i++) {
            // TODO(mattsh) - remove this after bug
            // http://code.google.com/p/dart/issues/detail?id=333 is fixed.
            final int pos = i;
            Future future = futures.get(pos);

            future.success(new OnSuccess() {
                @Override
                public void onSuccess(@Nonnull Object value) {
                    values.set(pos, value);
//                    values[pos] = value;
                    if (--remaining[0] == 0 && !result.isComplete()) {
                        completer.setResult(values);
                    }
                }
            });


            future.failure(new OnException() {
                @Override
                public boolean onException(Throwable e) {
                    if (!result.isComplete()) completer.setException(e);
                    return true;
                }
            });

        }
        return result;
    }

    public static <T> FutureCompleter<T> createCompleter() {
        return new CompleterImpl<T>();
    }

    public static <I, T> NamedAsyncFunction<I, T> createNamedAsyncFunction(final String name, final AsyncFunction<I, T> asyncFunction) {

        return new NamedAsyncFunction<I, T>() {

            @Override
            public String getName() {
                return name;
            }

            @Override
            public void start(I arg, Completer<T> completer) throws Exception {
                asyncFunction.start(arg, completer);
            }

        };
    }


}
