package c3i.util.shared.futures;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.Exportable;

@ExportClosure
@Export
public interface OnComplete extends Exportable {

    void call();

}
