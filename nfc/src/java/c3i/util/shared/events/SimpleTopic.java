package c3i.util.shared.events;

public class SimpleTopic extends Topic0<SimpleListener> {

    @Override
    protected void send(SimpleListener listener) {
        listener.onEvent();
    }

}
