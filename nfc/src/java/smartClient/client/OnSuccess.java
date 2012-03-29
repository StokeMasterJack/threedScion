package smartClient.client;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.Exportable;

@ExportClosure
@Export
public interface OnSuccess extends Exportable {

    void call();

}
