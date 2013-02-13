package c3i.smartClient.client.model.event;

import c3i.smartClient.client.model.ImageStack;
import c3i.util.shared.events.Topic1;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.Exportable;

public class ImageStackChange extends Topic1<ImageStackChange.Listener, ImageStack> {
    public ImageStackChange() {
        super("ImageStackChangeTopic");
    }

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
