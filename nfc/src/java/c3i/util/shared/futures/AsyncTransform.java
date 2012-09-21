package c3i.util.shared.futures;

public interface AsyncTransform<I, O> {
    Future<O> transform(I in);
}
