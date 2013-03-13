package c3i.util.shared.events;

public class SourcedTopic<P> extends Topic1<SourcedListener<P>, P> {

    public SourcedTopic(String name) {
        super(name);
    }

    @Override
    protected void send(SourcedListener<P> listener, P arg) {
        listener.onEvent(arg);
    }
}
