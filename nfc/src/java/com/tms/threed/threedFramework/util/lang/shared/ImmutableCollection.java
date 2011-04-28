package com.tms.threed.threedFramework.util.lang.shared;

import java.util.Collection;

public abstract class ImmutableCollection<E> implements Collection<E> {

    @Override public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override public void clear() {
        throw new UnsupportedOperationException();
    }
}
