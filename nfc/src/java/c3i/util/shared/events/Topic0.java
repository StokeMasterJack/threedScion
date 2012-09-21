package c3i.util.shared.events;

import com.google.common.base.Preconditions;
import smartsoft.util.gwt.client.Console;

public abstract class Topic0<LT> extends Topic<LT> {

    public void fire() {
        this.fireInternal();
    }

    @Override
    protected void sendInternal(LT listener, Object[] args) {
        Preconditions.checkNotNull(listener);
        Preconditions.checkArgument(args.length == 0);
        try {
            send(listener);
        } catch (Exception e) {
            Console.error("Error in exception handler", e);
            e.printStackTrace();
            throw new RuntimeException("Error in exception handler", e);
        }
    }

    abstract protected void send(LT listener);
}
