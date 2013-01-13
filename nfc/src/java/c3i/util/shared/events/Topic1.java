package c3i.util.shared.events;

import com.google.common.base.Preconditions;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Topic1<LT, ARG> extends Topic<LT> {

    protected Topic1(String name) {
        super(name);
    }

    public void fire(ARG arg) {
        this._fireInternal(arg);
    }

    @Override
    protected void sendInternal(LT listener, Object[] args) {
        Preconditions.checkNotNull(listener);
        Preconditions.checkArgument(args.length == 1);
        ARG arg = (ARG) args[0];

        try {
            send(listener, arg);
        } catch (Throwable e) {
            log.log(Level.SEVERE, "Error in Topic[" + name + "] event listener", e);
        }
    }

    abstract protected void send(LT listener, ARG arg);

    private static Logger log = Logger.getLogger(Topic1.class.getName());

}
