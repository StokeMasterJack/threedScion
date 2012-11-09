package c3i.admin.shared.jpgGen;

import javax.annotation.concurrent.Immutable;
import java.io.Serializable;

@Immutable
public class ExecutorStatus implements Serializable {

    private static final long serialVersionUID = 3280211128090992275L;

    private /* final */ String name;
    private /* final */ boolean shutdown;
    private /* final */ boolean terminated;
    private /* final */ int activeCount;
    private /* final */ long taskCount;
    private /* final */ long completedTaskCount;

    public ExecutorStatus(String name, boolean shutdown, boolean terminated, int activeCount, long taskCount, long completedTaskCount) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name must not be empty");
        }
        this.name = name;
        this.shutdown = shutdown;
        this.terminated = terminated;
        this.activeCount = activeCount;
        this.taskCount = taskCount;
        this.completedTaskCount = completedTaskCount;
    }

    private ExecutorStatus() {
    }

    public String getName() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name must not be empty");
        }
        return name;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public int getActiveTaskCount() {
        return activeCount;
    }

    public long getTaskCount() {
        return taskCount;
    }

    public long getCompletedTaskCount() {
        return completedTaskCount;
    }
}
