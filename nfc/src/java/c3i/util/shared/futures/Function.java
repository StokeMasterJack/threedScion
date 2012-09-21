package c3i.util.shared.futures;

import javax.annotation.Nonnull;

public interface Function<I, T> {
    @Nonnull
    T apply(@Nonnull I input);

}
