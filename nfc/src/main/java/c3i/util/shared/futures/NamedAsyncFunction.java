package c3i.util.shared.futures;

public interface NamedAsyncFunction<A, T> extends AsyncFunction<A, T> {

    String getName();

}
