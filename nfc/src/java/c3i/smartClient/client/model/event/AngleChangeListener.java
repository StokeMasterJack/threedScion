package c3i.smartClient.client.model.event;

import c3i.imageModel.shared.AngleKey;
import c3i.util.shared.events.ChangeListener;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.Exportable;

@Export
@ExportClosure
public interface AngleChangeListener extends ChangeListener<AngleKey>, Exportable {

    void onChange(AngleKey newValue);

}
