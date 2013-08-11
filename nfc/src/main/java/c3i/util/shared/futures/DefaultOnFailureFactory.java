package c3i.util.shared.futures;

import java.util.logging.Level;import java.util.logging.Logger;
import smartsoft.util.shared.ExceptionRenderer;

public class DefaultOnFailureFactory implements OnFailureFactory {
    @Override
    public OnException createOnFailure(Future future, final Throwable exception) {
        return new OnException() {
            @Override
            public boolean onException(Throwable e) {
                FutureFailure ee = new FutureFailure(exception);
                log.severe(ExceptionRenderer.render(ee));
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

    private static Logger log = Logger.getLogger(DefaultOnFailureFactory.class.getName());
}
