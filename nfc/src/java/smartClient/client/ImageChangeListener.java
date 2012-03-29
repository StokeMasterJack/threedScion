package smartClient.client;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.Exportable;
import smartsoft.util.gwt.client.events3.ChangeEvent;
import smartsoft.util.gwt.client.events3.ChangeListener;

@Export
@ExportClosure
public interface ImageChangeListener extends ChangeListener<ViewSession, ImageBatch>, Exportable {

    @Override
    void onEvent(ChangeEvent<ViewSession, ImageBatch> ev);

}
