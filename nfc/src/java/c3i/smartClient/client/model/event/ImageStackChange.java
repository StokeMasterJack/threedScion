package c3i.smartClient.client.model.event;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.Exportable;
import c3i.util.shared.events.Topic1;
import c3i.smartClient.client.model.ImageStack;

public class ImageStackChange extends Topic1<ImageStackChange.Listener, ImageStack> {

    @Override
    public void add(ImageStackChange.Listener listener) {
        super.add(listener);
    }

    @Override
    protected void send(Listener listener, ImageStack imageStack) {
        listener.onChange(imageStack);
    }

    @Override
    public void fire(ImageStack newValue) {
        super.fire(newValue);
    }

    @Export
    @ExportClosure
    public interface Listener extends Exportable {
        void onChange(ImageStack newValue);
    }
}
