package c3i.util.shared.events;

import com.google.common.base.Preconditions;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Topic0<LT> extends Topic<LT> {

    protected Topic0(String name) {
        super(name);
    }

    public void fire() {
        this._fireInternal();
    }

    @Override
    protected void sendInternal(LT listener, Object[] args) {
        Preconditions.checkNotNull(listener);
        Preconditions.checkArgument(args.length == 0);
        try {
            send(listener);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in Topic[" + name + "] event listener", e);
            throw new RuntimeException("Error in Topic[" + name + "] event listener", e);
        }
    }

    abstract protected void send(LT listener);

    private static Logger log = Logger.getLogger(Topic0.class.getName());
}
