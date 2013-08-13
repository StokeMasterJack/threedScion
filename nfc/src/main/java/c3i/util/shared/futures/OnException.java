package c3i.util.shared.futures;

public interface OnException {

    /**
     *
     * @param e
     * @return true if this exception has been handled
     */
    boolean onException(Throwable e);
}
