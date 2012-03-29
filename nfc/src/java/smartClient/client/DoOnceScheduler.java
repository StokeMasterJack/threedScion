package smartClient.client;

import com.google.gwt.core.client.Scheduler;

public class DoOnceScheduler {

    private boolean scheduled;

    public void schedule(final Scheduler.ScheduledCommand command) {
        if (!scheduled) {
            Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    command.execute();
                    scheduled = false;
                }
            });
            scheduled = true;
        }
    }


}
