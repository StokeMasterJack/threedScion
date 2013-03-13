package c3i.util.shared.events;

public interface SourcedListener<P> {
    void onEvent(P publisher);
}
