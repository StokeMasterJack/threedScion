package c3i.core.featureModel.shared;

import java.util.Iterator;

public class PeekingImpl<E> implements PeekingIterator<E> {

    private final Iterator<? extends E> iterator;
    private boolean hasPeeked;
    private E peekedElement;

    public PeekingImpl(Iterator<? extends E> iterator) {
        assert iterator != null;
        this.iterator = iterator;
    }

    public boolean hasNext() {
        return hasPeeked || iterator.hasNext();
    }

    public E next() {
        if (!hasPeeked) {
            return iterator.next();
        }
        E result = peekedElement;
        hasPeeked = false;
        peekedElement = null;
        return result;
    }

    public void remove() {
        assert !hasPeeked:"Can't remove after you've peeked at next";
        iterator.remove();
    }

    public E peek() {
        if (!hasPeeked) {
            peekedElement = iterator.next();
            hasPeeked = true;
        }
        return peekedElement;
    }
}
