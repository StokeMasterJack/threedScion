package c3i.util.shared.futures;

import smartsoft.util.gwt.client.Console;

public class DefaultCallbackExceptionHandler implements ExHandler {

    @Override
    public void handleException(String name, Throwable e) {
        Console.error(name, e);
        e.printStackTrace();
    }

}
