package c3i.smartClient.client.model;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.Exportable;

import javax.annotation.Nonnull;

@ExportClosure
public interface ThreedSessionOnSuccess extends Exportable {

    @Export
    void onSuccess(@Nonnull ThreedSession threedSession);

}
