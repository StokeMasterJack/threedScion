package threed.smartClient.client.api;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import threed.smartClient.client.util.futures.Future;

@Export
 class ThreedSessionFuture extends Future<ThreedSession> implements Exportable {

    public ThreedSessionFuture() {
        super("ThreedSessionFuture");
    }

    @Override
    public ThreedSession getResult() {
        return super.getResult();
    }
}
