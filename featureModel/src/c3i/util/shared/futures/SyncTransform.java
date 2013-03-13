package c3i.util.shared.futures;

public interface SyncTransform<I, O> {
    O transform(I input);
}
