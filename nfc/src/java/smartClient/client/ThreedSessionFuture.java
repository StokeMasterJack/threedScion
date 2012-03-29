package smartClient.client;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

@Export
public class ThreedSessionFuture extends Future<ThreedSession> implements Exportable {

    public ThreedSessionFuture() {
        super("ThreedSessionFuture");
    }

    @Override
    public ThreedSession getResult() {
        return super.getResult();
    }
}
