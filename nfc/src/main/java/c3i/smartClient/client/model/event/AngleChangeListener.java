package c3i.smartClient.client.model.event;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.Exportable;
import c3i.util.shared.events.ChangeListener;
import c3i.core.imageModel.shared.AngleKey;

@Export
@ExportClosure
public interface AngleChangeListener extends ChangeListener<AngleKey>,Exportable {

    void onChange(AngleKey newValue);

}
