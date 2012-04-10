package threed.smartClient.client.util.futures;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.Exportable;

@ExportClosure
@Export
public interface OnComplete extends Exportable {

    void call();

}