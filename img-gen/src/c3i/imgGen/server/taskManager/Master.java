package c3i.imgGen.server.taskManager;

import c3i.core.common.shared.SeriesId;
import c3i.core.threedModel.shared.RootTreeId;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.imageModel.shared.BaseImage;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.Profile;
import c3i.imageModel.shared.RawBaseImage;
import c3i.imageModel.shared.Slice2;
import c3i.imgGen.server.JpgSet;
import c3i.imgGen.server.singleJpg.BaseImageGenerator;
import c3i.imgGen.shared.ExecutorStatus;
import c3i.imgGen.shared.JobId;
import c3i.imgGen.shared.JobSpec;
import c3i.imgGen.shared.JobStatus;
import c3i.imgGen.shared.JobStatusItem;
import c3i.imgGen.shared.JpgState;
import c3i.imgGen.shared.JpgStateCounts;
import c3i.imgGen.shared.Stats;
import c3i.imgGen.shared.TerminalStatus;
import c3i.repo.server.Repos;
import c3i.repo.server.SeriesRepo;
import c3i.repo.server.rt.RtRepo;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import smartsoft.util.servlet.ExceptionRenderer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Master {

    private final JobId id;

    private final MyExecutors executors;

    //dependencies
    private final Repos repos;

    //master spec
    private final JobSpec jobSpec;
    private final SeriesId seriesId;
    private final Profile profile;

    private final KickOffTask masterTask;

    private final MonitorTask monitorTask;

    private final Stats stats = new Stats();

    private final ThreedModel threedModel;

    public Master(final Repos repos, JobSpec jobSpec, int threadCount, int priority) {
        Preconditions.checkNotNull(repos);
        Preconditions.checkNotNull(jobSpec);


        id = JobId.generateNew();

        executors = new MyExecutors(threadCount, priority);

        this.repos = repos;
        this.jobSpec = jobSpec;
        this.seriesId = jobSpec.getSeriesId();
        this.profile = jobSpec.getProfile();
        profile.getBaseImageType();

        threedModel = repos.getThreedModel(seriesId);

        stats.masterJobStartTime = id.getEnqueueTime();

        this.masterTask = new KickOffTask();
        this.monitorTask = new MonitorTask(masterTask);


        executors.masterExecutor.submit(masterTask);
        executors.monitorExecutor.submit(monitorTask);

//        executors.timerExecutor.scheduleAtFixedRate()


    } //end Master constructor

    public void printStatus() {
        JobStatus status = getStatus();
        status.printDetail();
    }

    public void awaitDoneDeep() throws ExecutionException, InterruptedException {
        masterTask.awaitDoneDeep();
    }

    /**
     * This is the KickOff task to kick of the jpg generation for one series-year-version-profile.
     * Just because this task finishes does not mean the entire job is finished
     */
    public final class KickOffTask extends MyFutureTask<CreateJpgSetsTask> {


        public KickOffTask() {
            super(new Callable<CreateJpgSetsTask>() {
                @Override
                public CreateJpgSetsTask call() throws Exception {

                    if (monitorTask.isCancelled()) throw new InterruptedException();

                    initDirsAndStartFile();

                    CreateJpgSetsTask createJpgSetsTask = new CreateJpgSetsTask(threedModel);
                    executors.createJpgSets.submit(createJpgSetsTask);

                    return createJpgSetsTask;
                }
            });
        }

        public JobStatus getStatus() throws InterruptedException, ExecutionException {

            if (monitorTask.isCancelled()) {
                return JobStatus.createCanceled();
            } else if (isDone()) {
                CreateJpgSetsTask createJpgSetsTask = get();
                return createJpgSetsTask.getStatus();
            } else {
                return JobStatus.createNothingToReportYet();
            }


        }


        public void awaitDoneDeep() throws ExecutionException, InterruptedException {
            CreateJpgSetsTask createJpgSetsTask = get();
            if (createJpgSetsTask != null) {
                createJpgSetsTask.awaitDoneDeep();
                log.info("createJpgSetsTask complete!");
            }
        }


    }


    public final class MonitorTask extends MyFutureTask<TerminalStatus> {

        public MonitorTask(final KickOffTask masterTask) {
            super(new Callable<TerminalStatus>() {
                @Override
                public TerminalStatus call() throws Exception {
                    try {
                        masterTask.awaitDoneDeep();
                        return TerminalStatus.Complete;
                    } catch (ExecutionException e) {
                        log.log(Level.SEVERE, "ExecutionException", e.getCause());
                        return TerminalStatus.exception(e);
                    } catch (InterruptedException e) {
                        return TerminalStatus.Cancelled;
                    } catch (Throwable e) {
                        log.log(Level.SEVERE, "Unexpected exception", e);
                        return TerminalStatus.exception(e);
                    }
                }
            });

        }

        @Override
        protected void done() {
            super.done();

            stats.masterJobEndTime = System.currentTimeMillis();


            TerminalStatus terminalStatus;
            if (isCancelled()) {
                terminalStatus = TerminalStatus.Cancelled;
            } else {
                try {
                    terminalStatus = get();
                } catch (InterruptedException e) {
                    terminalStatus = TerminalStatus.Cancelled;
                } catch (Throwable e) {
                    terminalStatus = TerminalStatus.exception(e);
                }
            }

            if (terminalStatus.equals(TerminalStatus.Complete)) {
                writeCompletedFile(stats.masterJobEndTime);
            }

            stats.finalStatus = terminalStatus;

            log.info("Jog Job terminated. TerminalStatus[" + terminalStatus + "]");
            shutdownNow();
        }
    }

    private void writeCompletedFile(long timeMillis) {
        SeriesRepo seriesRepo = repos.getSeriesRepo(this.seriesId.getSeriesKey());
        RootTreeId rootTreeId = seriesId.getRootTreeId();
        RtRepo rtRepo = seriesRepo.getRtRepo();
        File versionWidthDir = rtRepo.getVersionWidthDir(rootTreeId, profile);
        File completedFile = new File(versionWidthDir, "completed.txt");
        try {
            Files.write(timeMillis + "", completedFile, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public JobStatus getStatus() {

        JobStatus retVal;
        if (isCancelled()) {
            retVal = JobStatus.createCanceled();
        } else {
            try {
                retVal = masterTask.getStatus();
            } catch (InterruptedException e) {
                retVal = JobStatus.createCanceled();
            } catch (ExecutionException e) {
                e.printStackTrace();
                String serializedStackTrace = ExceptionRenderer.render(e);
                retVal = JobStatus.createExceptionStatus(serializedStackTrace);
            }
        }


        return retVal;
    }


    private void initDirsAndStartFile() {
        createJpgWidthDir();
        createGenVersionDirs();
    }


    public void cancel() {
        monitorTask.cancel(true);
    }

    public boolean isCancelled() {
        return monitorTask.isCancelled();
    }

    /**
     *
     * Creates a list of jpgSet for each slice (view-angle)
     * Input:   a ThreedModel
     * Output:  JpgSet for each slice
     */
    private final class CreateJpgSetsTask extends MyFutureTask<ImmutableList<CreateJpgSetTask>> {

        private final ThreedModel threedModel;

        private CreateJpgSetsTask(final ThreedModel threedModel) {
            super(new Callable<ImmutableList<CreateJpgSetTask>>() {

                @Override
                public ImmutableList<CreateJpgSetTask> call() throws Exception {


                    log.info("Creating JpgSets");
                    final ImmutableList.Builder<CreateJpgSetTask> builder = new ImmutableList.Builder<CreateJpgSetTask>();

                    List<ImView> views = threedModel.getViews();
                    for (ImView view : views) {
                        int angleCount = view.getAngleCount();
                        for (int a = 1; a <= angleCount; a++) {
                            if (monitorTask.isCancelled()) throw new InterruptedException();
                            CreateJpgSetTask task = new CreateJpgSetTask(new Slice2(view, a));
                            executors.createJpgSet.submit(task);
                            builder.add(task);
                        }
                    }


                    return builder.build();
                }
            });

            this.threedModel = threedModel;

        }

        public int getSliceCount() {
            return threedModel.getSliceCount();
        }


        public JobStatus getStatus() throws InterruptedException, ExecutionException {
            if (!isDone()) {
                return JobStatus.createNothingToReportYet();
            }

            int sliceCount = getSliceCount();
            int slicesComplete = 0;

            Integer jpgCount = 0;

            JpgStateCounts.Builder jpgStateBuilder = new JpgStateCounts.Builder();

            ImmutableList<CreateJpgSetTask> createJpgSetTasks = get();

            for (CreateJpgSetTask createJpgSetTask : createJpgSetTasks) {

                if (monitorTask.isCancelled()) return JobStatus.createCanceled();

                if (createJpgSetTask.isDone()) {
                    slicesComplete++;
                    ProcessJpgSetTask processJpgSetTask = createJpgSetTask.get();
                    int jpgCountForSlice = processJpgSetTask.getJpgCountForSlice();
                    jpgCount += jpgCountForSlice;
                    JpgStateCounts jpgStates = processJpgSetTask.getJpgStates();
                    jpgStateBuilder.count(jpgStates);
                }
            }


            JpgStateCounts jpgStateCounts = jpgStateBuilder.build();


            return JobStatus.createInProcessOrComplete(sliceCount, slicesComplete, jpgCount, jpgStateCounts);


        }

        public void awaitDoneDeep() throws ExecutionException, InterruptedException {
            ImmutableList<CreateJpgSetTask> createJpgSetTasks = get();
            if (createJpgSetTasks != null) {
                for (CreateJpgSetTask createJpgSetTask : createJpgSetTasks) {
                    createJpgSetTask.awaitDoneDeep();
                }
            }
        }


    }


    private final class CreateJpgSetTask extends MyFutureTask<ProcessJpgSetTask> {

        private CreateJpgSetTask(final Slice2 slice) {
            super(new Callable<ProcessJpgSetTask>() {
                @Override
                public ProcessJpgSetTask call() throws Exception {

                    if (monitorTask.isCancelled()) throw new InterruptedException();

                    log.info("Start: jpgSetAction: " + slice);
                    JpgSetAction jpgSetAction = new JpgSetAction(repos, new JpgSet.JpgSetKey(seriesId, slice.getViewName(), slice.getAngle()));
                    JpgSet jpgSet = jpgSetAction.getJpgSet();
                    log.info("Complete: jpgSetAction: " + slice + "  jpgCount: " + jpgSet.size());

                    ProcessJpgSetTask task1 = new ProcessJpgSetTask(slice, jpgSet);
                    executors.processJpgSet1.submit(task1);


                    return task1;
                }
            });


        }


        public void awaitDoneDeep() throws ExecutionException, InterruptedException {
            ProcessJpgSetTask task = get();
            if (task != null) {
                task.awaitDoneDeep();
            }
        }
    }


    private final class ProcessJpgSetTask extends MyFutureTask<ImmutableList<JpgTask>> {

        private final int jpgCountForSlice;

        private ProcessJpgSetTask(final Slice2 slice, final JpgSet jpgSet) {

            super(new Callable<ImmutableList<JpgTask>>() {
                @Override
                public ImmutableList<JpgTask> call() throws Exception {

                    final ImmutableList.Builder<JpgTask> builder = new ImmutableList.Builder<JpgTask>();

                    for (final RawBaseImage fingerprint : jpgSet.getJpgSpecs()) {
                        if (monitorTask.isCancelled()) throw new InterruptedException();


                        JpgTask jpgTask = new JpgTask(slice, fingerprint);
                        executors.jpg.submit(jpgTask);
                        builder.add(jpgTask);


                    }

                    ImmutableList<JpgTask> jpgFutureTasks = builder.build();
                    return jpgFutureTasks;
                }
            });

            int jpgSetSize = jpgSet.size();
            this.jpgCountForSlice = jpgSet.size();

        }


        private int getJpgCountForSlice() {
            return jpgCountForSlice;
        }

        private JpgStateCounts getJpgStates() throws InterruptedException, ExecutionException {
            if (!isDone()) {
                return null;
            }

            if (masterTask.isCancelled()) throw new InterruptedException();

            ImmutableList<JpgTask> jpgTasks = get();

            JpgStateCounts.Builder b = new JpgStateCounts.Builder();

            for (JpgTask jpgTask : jpgTasks) {
                if (jpgTask.isDone()) {
                    JpgState jpgState = jpgTask.get();
                    b.count(jpgState);
                }

            }

            return b.build();
        }


        public void awaitDoneDeep() throws ExecutionException, InterruptedException {
            ImmutableList<JpgTask> jpgTasks = get();
            if (jpgTasks != null) {
                for (JpgTask jpgTask : jpgTasks) {
                    jpgTask.awaitDoneDeep();
                }
            }
        }
    }


    public Stats getStats() {
        return stats;
    }

    private final class JpgTask extends MyFutureTask<JpgState> {

        private JpgTask(final Slice2 slice, final RawBaseImage fingerprint) {
            super(new Callable<JpgState>() {
                @Override
                public JpgState call() throws Exception {

                    if (monitorTask.isCancelled()) throw new InterruptedException();

                    BaseImage baseImage = new BaseImage(profile, slice, fingerprint);

                    BaseImageGenerator jpgGeneratorPureJava = new BaseImageGenerator(repos, baseImage);
                    jpgGeneratorPureJava.generate(stats);

                    return JpgState.COMPLETE;
                }


            });
        }

        public void awaitDoneDeep() throws ExecutionException, InterruptedException {
            get();
        }
    }

    public KickOffTask getFuture() {
        return masterTask;
    }

    private void createGenVersionDirs() {
        repos.createGenVersionDirs(seriesId, profile);
    }

    private void createJpgWidthDir() {
        SeriesRepo seriesRepo = repos.getSeriesRepo(seriesId.getSeriesKey());
        File jpgDirForSize = seriesRepo.getRtRepo().getJpgDirForSize(profile);
        try {
            Files.createParentDirs(jpgDirForSize);
            jpgDirForSize.mkdir();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JobId getId() {
        return id;
    }


    private static Logger log = Logger.getLogger("c3i");

    public SeriesId getSeriesId() {
        return seriesId;
    }

    public Profile getProfile() {
        return profile;
    }


    @Override
    public String toString() {
        return id + " " + seriesId + " " + profile.getKey();
    }

    void shutdownNow() {
        executors.shutdownNow();
    }

    public boolean isShutdown() {
        return executors.isShutdown();
    }

    public boolean isTerminated() {
        return executors.isTerminated();
    }

    public void awaitTermination() throws InterruptedException {
        executors.awaitTermination();
    }

    private static class MyFutureTask<V> extends FutureTask<V> {


        private MyFutureTask(Callable<V> vCallable) {
            super(vCallable);
        }


        public String getName() {
            return getClass().getSimpleName();
        }

        public V getIgnoreExceptions() {
            try {
                return get();
            } catch (Exception e) {
                return null;
            }
        }

    }

    private static class MyThreadPoolExecutor<T extends FutureTask> extends ThreadPoolExecutor {

        private final Class<T> id;

        private MyThreadPoolExecutor(Class<T> id, int nThreads) {
            this(id, nThreads, Thread.NORM_PRIORITY);

        }

        private MyThreadPoolExecutor(Class<T> id, int nThreads, int priority) {
            super(nThreads,
                    nThreads,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(), new DaveThreadFactory(priority));
            this.id = id;
        }

        public Class getId() {
            return id;
        }

        public String getName() {
            return id.getSimpleName();
        }

        public T getOnlyDoneTask() {
            return (T) getQueue().element();
        }

    }

    static class DaveThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        private final int priority;

        DaveThreadFactory(int priority) {
            this.priority = priority;
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != priority)
                t.setPriority(priority);
            return t;
        }
    }


    public ImmutableList<ExecutorStatus> getExecutorStatuses() {
        return executors.getStatuses();
    }

    private static class MyExecutors {

        public final MyThreadPoolExecutor<MonitorTask> monitorExecutor = new MyThreadPoolExecutor<MonitorTask>(MonitorTask.class, 1);
        public final MyThreadPoolExecutor<KickOffTask> masterExecutor = new MyThreadPoolExecutor<KickOffTask>(KickOffTask.class, 1);
        public final MyThreadPoolExecutor createJpgSets = new MyThreadPoolExecutor(CreateJpgSetsTask.class, 1);
        public final MyThreadPoolExecutor createJpgSet = new MyThreadPoolExecutor(CreateJpgSetTask.class, 1);
        public final MyThreadPoolExecutor processJpgSet1 = new MyThreadPoolExecutor(ProcessJpgSetTask.class, 1);
        public final MyThreadPoolExecutor jpg;


        private final ImmutableList<MyThreadPoolExecutor> executors;


        private MyExecutors(final int threadCount, int priority) {

            jpg = new MyThreadPoolExecutor(JpgTask.class, threadCount, priority);


            executors = new ImmutableList.Builder<MyThreadPoolExecutor>()
                    .add(monitorExecutor)
                    .add(masterExecutor)
                    .add(createJpgSets)
                    .add(createJpgSet)
                    .add(processJpgSet1)
                    .add(jpg)
                    .build();


        }


        private static ExecutorStatus getExecutorStatus(MyThreadPoolExecutor e) {
            return new ExecutorStatus(
                    e.getName(),
                    e.isShutdown(),
                    e.isTerminated(),
                    e.getActiveCount(),
                    e.getTaskCount(),
                    e.getCompletedTaskCount());
        }

        public ImmutableList<ExecutorStatus> getStatuses() {
            ImmutableList.Builder<ExecutorStatus> b = new ImmutableList.Builder<ExecutorStatus>();

            for (MyThreadPoolExecutor executor : executors) {
                b.add(getExecutorStatus(executor));
            }

            return b.build();
        }


        public boolean isShutdown() {
            return masterExecutor.isShutdown();
        }

        public synchronized boolean isTerminated() {
            for (ThreadPoolExecutor executor : executors) {
                if (!executor.isTerminated()) return false;
            }
            return true;
        }

        public void awaitTermination() throws InterruptedException {
            for (ThreadPoolExecutor executor : executors) {
                executor.awaitTermination(30, TimeUnit.DAYS);
            }
        }


        public void shutdownNow() {
            for (ThreadPoolExecutor executor : executors) {
                executor.shutdownNow();
            }
        }


    }

    public JobSpec getJobSpec() {
        return jobSpec;
    }

    public JobStatusItem getJobStatusItem() {
        return new JobStatusItem(getJobSpec(), id, getStatus());
    }

}
