package c3i.smartClient.client.model.event;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportClosure;
import org.timepedia.exporter.client.Exportable;
import c3i.util.shared.events.Topic1;
import c3i.core.imageModel.shared.CoreImageStack;

public class CoreStackChange extends Topic1<CoreStackChange.Listener, CoreImageStack> {

    public CoreStackChange() {
        super("CoreStackChangeTopic");
    }

    @Override
    public void add(CoreStackChange.Listener listener) {
        super.add(listener);
    }

    @Override
    protected void send(Listener listener, CoreImageStack imageStack) {
        listener.onChange(imageStack);
    }

    @Override
    public void fire(CoreImageStack newValue) {
        super.fire(newValue);
    }

    @Export
    @ExportClosure
    public interface Listener extends Exportable {
        void onChange(CoreImageStack newValue);
    }
}
