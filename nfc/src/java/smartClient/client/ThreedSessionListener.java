package smartClient.client;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.Exportable;
import smartsoft.util.gwt.client.events3.Listener;

@Export
@ExportClosure
public interface ThreedSessionListener extends Listener<ThreedSession, ThreedSessionEvent>, Exportable {

    void onEvent(ThreedSessionEvent ev);

}
