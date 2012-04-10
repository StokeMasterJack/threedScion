package threed.smartClient.client.util;

import com.google.gwt.core.client.Scheduler;

import java.util.HashSet;

/**
 * This class solves the problem of too many change events triggering too many (unnecessary) expensive operations
 */
public class DoOnceScheduler {

    private final HashSet<Scheduler.ScheduledCommand> scheduledCommands = new HashSet<Scheduler.ScheduledCommand>();

    public void maybeSchedule(final Scheduler.ScheduledCommand cmd) {
        if (!alreadyScheduled(cmd)) {
            schedule(cmd);
        }
    }

    private void schedule(final Scheduler.ScheduledCommand cmd) {
        Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                cmd.execute();
                scheduledCommands.remove(cmd);
            }
        });
    }

    private boolean alreadyScheduled(Scheduler.ScheduledCommand cmd) {
        return scheduledCommands.contains(cmd);
    }


}
