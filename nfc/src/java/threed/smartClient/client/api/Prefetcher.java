package threed.smartClient.client.api;

import com.google.gwt.core.client.Scheduler;
import smartsoft.util.lang.shared.Path;

import java.util.List;

public class Prefetcher {

    private List<Path> urlsToPrefetch;

    public Prefetcher(List<Path> urlsToPrefetch) {
        this.urlsToPrefetch = urlsToPrefetch;
        PrefetchNextCommand cmd = new PrefetchNextCommand();
        Scheduler.get().scheduleIncremental(cmd);
    }

    private class PrefetchNextCommand implements Scheduler.RepeatingCommand {

        @Override
        public boolean execute() {
            Path urlToPrefetch = urlsToPrefetch.remove(0);
            boolean keepGoing;
            if (urlsToPrefetch.isEmpty()) {
                keepGoing = false;
            } else {
                keepGoing = true;
            }
            Image.maybeCacheImage(urlToPrefetch);
            return keepGoing;
        }
    }

}