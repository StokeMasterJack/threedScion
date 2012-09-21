package c3i.util.shared.events;

public class ExceptionTopic extends Topic1<ExceptionHandler, Throwable> {

    @Override
    public void fire(Throwable e) {
        super.fire(e);
    }

    @Override
    protected void send(ExceptionHandler listener, Throwable e) {
        listener.onException(e);
    }


}
