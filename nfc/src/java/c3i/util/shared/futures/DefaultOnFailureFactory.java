package c3i.util.shared.futures;

import smartsoft.util.gwt.client.Console;
import smartsoft.util.lang.shared.ExceptionRenderer;

public class DefaultOnFailureFactory implements OnFailureFactory {
    @Override
    public OnException createOnFailure(Future future, final Throwable exception) {
        return new OnException() {
            @Override
            public boolean onException(Throwable e) {
                FutureFailure ee = new FutureFailure(exception);
                Console.error(ExceptionRenderer.render(ee));
                ee.printStackTrace();
                return true;
            }
        };
    }

    public static class FutureFailure extends RuntimeException {
        public FutureFailure(Throwable cause) {
            super(cause);
        }
    }
}
