package c3i.util.shared.events;

import com.google.common.base.Preconditions;
import smartsoft.util.gwt.client.Console;

public abstract class Topic1<LT, ARG> extends Topic<LT> {

    public void fire(ARG arg) {
        this.fireInternal(arg);
    }

    @Override
    protected void sendInternal(LT listener, Object[] args) {
        Preconditions.checkNotNull(listener);
        Preconditions.checkArgument(args.length == 1);
        ARG arg = (ARG) args[0];

        try {
            send(listener, arg);
        } catch (Throwable e) {
            Console.error("Error in exception handler", e);
            e.printStackTrace();
            throw new RuntimeException("Error in exception handler", e);
        }
    }

    abstract protected void send(LT listener, ARG arg);


}
