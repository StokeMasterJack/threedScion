package c3i.util.shared.futures;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultCallbackExceptionHandler implements ExHandler {

    @Override
    public void handleException(String name, Throwable e) {
        log.log(Level.SEVERE, name, e);
        e.printStackTrace();
    }

    private static Logger log = Logger.getLogger(DefaultCallbackExceptionHandler.class.getName());

}
