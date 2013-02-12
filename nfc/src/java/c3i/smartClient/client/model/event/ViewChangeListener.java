package c3i.smartClient.client.model.event;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.Exportable;
import c3i.util.shared.events.ChangeListener;
import c3i.imageModel.shared.ViewKey;

@Export
@ExportClosure
public interface ViewChangeListener extends ChangeListener<ViewKey>, Exportable {

    void onChange(ViewKey newValue);

}
