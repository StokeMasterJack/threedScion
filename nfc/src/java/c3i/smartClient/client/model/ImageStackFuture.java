package c3i.smartClient.client.model;

import c3i.util.shared.futures.ForwardingFuture;
import c3i.util.shared.futures.Future;
import c3i.util.shared.futures.OnSuccess;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

@Export
public class ImageStackFuture extends ForwardingFuture<ImageStack> implements Exportable {

    public ImageStackFuture(Future<ImageStack> delegate) {
        super(delegate);
    }

    @Override
    public ImageStack getResult() throws RuntimeException {
        return super.getResult();
    }

    @Override
    public void success(OnSuccess<ImageStack> successHandler) {
        super.success(successHandler);
    }

}
