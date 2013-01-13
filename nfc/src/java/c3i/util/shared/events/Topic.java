package c3i.util.shared.events;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Topic<LT> {

    protected final String name;

    protected Topic(String name) {
        this.name = name;
    }

    protected final ArrayList<LT> listeners = new ArrayList<LT>();

    private boolean suspended;

    protected int fireCount = 0;

    public void add(LT listener) {
        listeners.add(listener);
    }

    public void remove(LT listener) {
        listeners.remove(listener);
    }

    public void removeAll() {
        listeners.clear();
    }

    protected void _fireInternal(Object... args) {
        if (args == null) throw new IllegalArgumentException();
        if (!suspended) {
            for (LT listener : listeners) {
                try {
                    sendInternal(listener, args);
                } catch (Throwable e) {
                    log.log(Level.SEVERE, "Should never occur");
                }
            }
        }
        fireCount++;
    }

    abstract protected void sendInternal(LT listener, Object... args);

    public void resume() {
        suspended = false;
    }

    public void suspend() {
        suspended = true;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public boolean noListeners() {
        return listeners.isEmpty();
    }

    public int getListenerCount() {
        return listeners.size();
    }

    private static Logger log = Logger.getLogger(Topic.class.getName());

}
