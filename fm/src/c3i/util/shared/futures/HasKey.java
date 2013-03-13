package c3i.util.shared.futures;

import javax.annotation.Nonnull;

public interface HasKey<K> {
    @Nonnull
    K getKey();
}
