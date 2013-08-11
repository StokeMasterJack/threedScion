package c3i.util.shared.futures;

import javax.annotation.Nonnull;

public interface OnSuccess<T> {

    void onSuccess(@Nonnull T result);
}
