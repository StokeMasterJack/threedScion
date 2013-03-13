package c3i.util.shared.events;

import org.timepedia.exporter.client.Exportable;

public interface ChangeListener<VT> extends Exportable {

    void onChange(VT newValue);
}