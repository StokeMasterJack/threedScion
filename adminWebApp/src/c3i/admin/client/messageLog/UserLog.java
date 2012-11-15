package c3i.admin.client.messageLog;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.gwt.client.events2.ValueChangeHandlers;

import java.util.ArrayList;
import java.util.Iterator;

public class UserLog implements Iterable<UserLog.LogMessage>, smartsoft.util.gwt.client.UserLog {

    private final ValueChangeHandlers valueChangeHandlers;
    private ArrayList<LogMessage> messages = new ArrayList<LogMessage>();

    private static UserLog INSTANCE;

    private UserLog() {
        this.valueChangeHandlers = new ValueChangeHandlers(this);
    }

    public static UserLog get() {
        if (INSTANCE == null) {
            INSTANCE = new UserLog();
        }
        return INSTANCE;
    }

    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<UserLog> messageLogValueChangeHandler) {
        return valueChangeHandlers.addValueChangeHandler(messageLogValueChangeHandler);
    }

    public void log(String msg) {
        messages.add(new LogMessage(System.currentTimeMillis(), msg));
        valueChangeHandlers.fire(this);
    }

    public static final class LogMessage {

        private final long timeStamp;
        private final String text;

        private LogMessage(long timeStamp, String text) {
            this.timeStamp = timeStamp;
            this.text = text;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public String getText() {
            return text;
        }
    }

    @Override
    public Iterator<LogMessage> iterator() {
        return messages.iterator();
    }

    public void clearLog() {
        messages.clear();
        valueChangeHandlers.fire(this);
    }

    @Override
    public String toString() {
        return messages.toString();
    }
}
