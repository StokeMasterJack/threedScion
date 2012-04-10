package threed.smartClient.client.util.futures;

import smartsoft.util.gwt.client.Console;
import smartsoft.util.lang.shared.ExceptionRenderer;

public class DefaultOnFailureFactory implements OnFailureFactory {
    @Override
    public OnFailure createOnFailure(Future future, final Throwable exception) {
        return new OnFailure() {
            @Override
            public void call() {
                FutureFailure ee = new FutureFailure(exception);
                Console.error(ExceptionRenderer.render(ee));
                ee.printStackTrace();

            }
        };
    }

    public static class FutureFailure extends RuntimeException {
        public FutureFailure(Throwable cause) {
            super(cause);
        }
    }
}
