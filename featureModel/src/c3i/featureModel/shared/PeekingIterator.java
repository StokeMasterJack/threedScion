package c3i.featureModel.shared;

import java.util.Iterator;

public interface PeekingIterator<E> extends Iterator<E> {
    /**
     * Returns the next element in the iteration, without advancing the iteration.
     * <p/>
     * <p>Calls to {@code peek()} should not change the state of the iteration,
     * except that it <i>may</i> prevent removal of the most recent element via
     * {@link #remove()}.
     *
     * @throws java.util.NoSuchElementException if the iteration has no more elements
     *                                according to {@link #hasNext()}
     */
    E peek();

    /**
     * {@inheritDoc}
     * <p/>
     * <p>The objects returned by consecutive calls to {@link #peek()} then {@link
     * #next()} are guaranteed to be equal to each other.
     */
    E next();

    /**
     * {@inheritDoc}
     * <p/>
     * <p>Implementations may or may not support removal when a call to {@link
     * #peek()} has occurred since the most recent call to {@link #next()}.
     *
     * @throws IllegalStateException if there has been a call to {@link #peek()}
     *                               since the most recent call to {@link #next()} and this implementation
     *                               does not support this sequence of calls (optional)
     */
    void remove();
}
