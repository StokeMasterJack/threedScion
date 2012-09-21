package c3i.smartClient.client.model;

import c3i.util.shared.events.ChangeListener;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;

@ExportClosure
public interface ImageStackChangeListener extends ChangeListener<ImageStack> {

    @Export
    @Override
    void onChange(ImageStack newValue);
}
