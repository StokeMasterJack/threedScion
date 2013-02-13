package c3i.smartClient.client.model;

import c3i.util.shared.futures.ForwardingFuture;
import c3i.util.shared.futures.Future;
import c3i.util.shared.futures.OnSuccess;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

import javax.annotation.Nonnull;


public class ThreedSessionFuture extends ForwardingFuture<ThreedSession> implements Exportable {

    public ThreedSessionFuture(Future<ThreedSession> delegate) {
        super(delegate);
    }

    private ThreedSessionFuture() {
        super(null);
        throw new UnsupportedOperationException("This is apparently required by gwt-exporter");
    }

    @Export
    public ThreedSession getResult() {
        return delegate.getResult();
    }

    @Export
    public void success(final ThreedSessionOnSuccess successHandler) {
        delegate.success(new OnSuccess<ThreedSession>() {
            @Override
            public void onSuccess(@Nonnull ThreedSession result) {
                successHandler.onSuccess(result);
            }
        });
    }
}
