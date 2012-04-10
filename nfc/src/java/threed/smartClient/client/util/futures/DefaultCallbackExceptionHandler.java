package threed.smartClient.client.util.futures;

import smartsoft.util.gwt.client.Console;

public class DefaultCallbackExceptionHandler implements ExceptionHandler {

    @Override
    public void handleException(String name, Exception e) {
        FutureCallbackException ee = new FutureCallbackException(e);
        Console.error(name, ee);
        ee.printStackTrace();
    }

    public static class FutureCallbackException extends RuntimeException {
        public FutureCallbackException(Throwable cause) {
            super(cause);
        }
    }
}
