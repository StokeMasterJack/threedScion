package smartClient.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.Image;
import smartsoft.util.lang.shared.Path;

import java.util.HashSet;
import java.util.List;

public class Prefetcher {

    private final PrefetchStrategy strategy;

    private static HashSet<Path> prefetchedUrls = new HashSet<Path>();

    public Prefetcher(PrefetchStrategy strategy) {
        this.strategy = strategy;
    }

    public void prefetch() {
        PrefetchNextCommand cmd = new PrefetchNextCommand(strategy);
        Scheduler.get().scheduleIncremental(cmd);
    }

    private class PrefetchNextCommand implements Scheduler.RepeatingCommand {

        private final PrefetchStrategy strategy;
        private List<Path> urlsToPrefetch;

        private PrefetchNextCommand(PrefetchStrategy strategy) {
            this.strategy = strategy;
        }

        @Override public boolean execute() {

            if (urlsToPrefetch == null) {
                this.urlsToPrefetch = strategy.getPrefetchUrls();
                return true;
            }


            Path urlToPrefetch = urlsToPrefetch.remove(0);

            boolean keepGoing;
            if (urlsToPrefetch.isEmpty()) {
                keepGoing = false;
            } else {
                keepGoing = true;
            }

            if (prefetchedUrls.contains(urlToPrefetch)) return keepGoing;

//            System.out.println("prefetching [" + urlToPrefetch + "]");

            Image.prefetch(urlToPrefetch.toString());
            prefetchedUrls.add(urlToPrefetch);

            return keepGoing;
        }
    }

}