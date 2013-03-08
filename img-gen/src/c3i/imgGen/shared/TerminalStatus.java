package c3i.imgGen.shared;

import java.io.Serializable;

public class TerminalStatus implements Serializable {

    private static final long serialVersionUID = 7036065349334801306L;

    private int id;
    private String name;
    private Throwable exception;

    public static TerminalStatus Complete = new TerminalStatus(0, "Complete", null);
    public static TerminalStatus Cancelled = new TerminalStatus(1, "Cancelled", null);

    private TerminalStatus(int id, String name, Throwable exception) {
        this.id = id;
        this.name = name;
        this.exception = exception;
    }

    private TerminalStatus() {
    }

    //    Complete,Cancelled,Exception

    public static TerminalStatus exception(Throwable exception) {
        return new TerminalStatus(3, "Exception", exception);
    }

    public boolean isException() {
        return exception != null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Throwable getException() {
        return exception;
    }

    @Override
    public String toString() {
        return name;
    }
}
