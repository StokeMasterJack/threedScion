package c3i.util.shared.events;

import com.google.common.base.Preconditions;
import java.util.logging.Level;import java.util.logging.Logger;

public abstract class Topic2<LT, ARG0, ARG1> extends Topic<LT> {

    public void fire(ARG0 arg0, ARG1 arg1) {
        this.fireInternal(arg0, arg1);
    }

    @Override
    protected void sendInternal(LT listener, Object[] args) {
        Preconditions.checkNotNull(listener);
        Preconditions.checkArgument(args.length == 2);

        ARG0 arg0 = (ARG0) args[0];
        ARG1 arg1 = (ARG1) args[1];

        send(listener, arg0, arg1);


        try {
            send(listener, arg0, arg1);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in exception handler", e);
            e.printStackTrace();
            throw new RuntimeException("Error in exception handler", e);
        }
    }

    abstract protected void send(LT listener, ARG0 arg0, ARG1 arg1);

    private static Logger log = Logger.getLogger(Topic2.class.getName());
}
