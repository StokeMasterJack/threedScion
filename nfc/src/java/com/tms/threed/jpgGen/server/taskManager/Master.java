package com.tms.threed.jpgGen.server.taskManager;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.tms.threed.jpgGen.shared.JobStatus;
import com.tms.threed.jpgGen.shared.JobStatusItem;
import com.tms.threed.jpgGen.server.singleJpg.JpgGeneratorPureJava;
import com.tms.threed.jpgGen.shared.*;
import com.tms.threed.repoService.server.JpgKey;
import com.tms.threed.repoService.server.Repos;
import com.tms.threed.repoService.server.SeriesRepo;
import com.tms.threed.repoService.server.rt.RtRepo;
import com.tms.threed.threedCore.threedModel.shared.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import smartsoft.util.servlet.ExceptionRenderer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.concurrent.*;

public class Master {

    private final JobId id;

    private final MyExecutors executors;

    //dependencies
    private final Repos repos;

    //master spec
    private final JobSpec jobSpec;
    private transient final SeriesId seriesId;
    private transient final JpgWidth jpgWidth;

    private final MasterTask masterTask;

    private final MonitorTask monitorTask;

    private final Stats stats = new Stats();

    public Master(final Repos repos, JobSpec jobSpec, int threadCount) {
        Preconditions.checkNotNull(repos);
        Preconditions.checkNotNull(jobSpec);
        
        
        id = JobId.generateNew();

        executors = new MyExecutors(threadCount);

        this.repos = repos;
        this.jobSpec = jobSpec;
        this.seriesId = jobSpec.getSeriesId();
        this.jpgWidth = jobSpec.getJpgWidth();

        stats.masterJobStartTime = id.getEnqueueTime();

        this.masterTask = new MasterTask();
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

    public void printStatusBrief() {
        JobStatus status = getStatus();
        status.printBrief();
    }

    public final class MasterTask extends MyFutureTask<CreateJpgSetsTask> {

        public MasterTask() {
            super(new Callable<CreateJpgSetsTask>() {
                @Override
                public CreateJpgSetsTask call() throws Exception {

                    if (monitorTask.isCancelled()) throw new InterruptedException();
                    ThreedModel threedModel = repos.getThreedModel(seriesId);

                    final ImmutableList<Slice> slices = new ImmutableList.Builder<Slice>()
                            .addAll(threedModel.getSlices())
                            .build();

                    initDirsAndStartFile();

                    CreateJpgSetsTask createJpgSetsTask = new CreateJpgSetsTask(threedModel, slices);
                    executors.createJpgSets.submit(createJpgSetsTask);

                    return createJpgSetsTask;
                }
            });
        }

        public JobStatus getStatus() throws InterruptedException, ExecutionException {

            if (Master.this.isCancelled()) {
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
            }
        }


    }


    public final class MonitorTask extends MyFutureTask<TerminalStatus> {

        public MonitorTask(final MasterTask masterTask) {
            super(new Callable<TerminalStatus>() {
                @Override
                public TerminalStatus call() throws Exception {
                    try {
                        masterTask.awaitDoneDeep();
                        return TerminalStatus.Complete;
                    } catch (ExecutionException e) {
                        log.error("ExecutionException", e.getCause());
                        return TerminalStatus.Exception;
                    } catch (InterruptedException e) {
                        return TerminalStatus.Cancelled;
                    } catch (Throwable e) {
                        log.error("Unexpected exception", e);
                        return TerminalStatus.Exception;
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
                    terminalStatus = TerminalStatus.Exception;
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
        File versionWidthDir = rtRepo.getVersionWidthDir(rootTreeId, jpgWidth);
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

    private final class CreateJpgSetsTask extends MyFutureTask<ImmutableList<CreateJpgSetTask>> {

        private final ThreedModel threedModel;

        private CreateJpgSetsTask(final ThreedModel threedModel, final ImmutableList<Slice> slices) {
            super(new Callable<ImmutableList<CreateJpgSetTask>>() {

                @Override
                public ImmutableList<CreateJpgSetTask> call() throws Exception {

                    final ImmutableList.Builder<CreateJpgSetTask> builder = new ImmutableList.Builder<CreateJpgSetTask>();

                    for (Slice slice : slices) {
                        if (monitorTask.isCancelled()) throw new InterruptedException();
                        CreateJpgSetTask task = new CreateJpgSetTask(slice);
                        executors.createJpgSet.submit(task);
                        builder.add(task);
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
                    ProcessJpgSetTaskPair pair = createJpgSetTask.get();
                    int jpgCountForSlice = pair.getTask1().getJpgCountForSlice();
                    jpgCount += jpgCountForSlice;
                    JpgStateCounts jpgStates = pair.getTask1().getJpgStates();
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


    private final class CreateJpgSetTask extends MyFutureTask<ProcessJpgSetTaskPair> {

        private CreateJpgSetTask(final Slice slice) {
            super(new Callable<ProcessJpgSetTaskPair>() {
                @Override
                public ProcessJpgSetTaskPair call() throws Exception {
                    if (monitorTask.isCancelled()) throw new InterruptedException();
                    JpgSetAction childJob1 = new JpgSetAction(repos, seriesId, slice, jpgWidth);
                    final Set<String> jpgSet = childJob1.readOrCreateJpgSet();

                    ProcessJpgSetTask1 task1 = new ProcessJpgSetTask1(jpgSet);
                    executors.processJpgSet1.submit(task1);

                    ProcessJpgSetTask2 task2 = new ProcessJpgSetTask2(slice, jpgSet);
                    executors.processJpgSet2.submit(task2);

                    return new ProcessJpgSetTaskPair(task1, task2);
                }
            });


        }

        public void awaitDoneDeep() throws ExecutionException, InterruptedException {
            ProcessJpgSetTaskPair pair = get();
            if (pair != null) {
                pair.getTask1().awaitDoneDeep();
                pair.getTask2().awaitDoneDeep();
            }
        }
    }

    private final class ProcessJpgSetTaskPair {

        private final ProcessJpgSetTask1 task1;
        private final ProcessJpgSetTask2 task2;

        private ProcessJpgSetTaskPair(ProcessJpgSetTask1 task1, ProcessJpgSetTask2 task2) {
            this.task1 = task1;
            this.task2 = task2;
        }

        public ProcessJpgSetTask1 getTask1() {
            return task1;
        }

        public ProcessJpgSetTask2 getTask2() {
            return task2;
        }
    }


    private final class ProcessJpgSetTask1 extends MyFutureTask<ImmutableList<JpgTask>> {

        private final int jpgCountForSlice;

        private ProcessJpgSetTask1(final Set<String> jpgSet) {

            super(new Callable<ImmutableList<JpgTask>>() {
                @Override
                public ImmutableList<JpgTask> call() throws Exception {

                    final ImmutableList.Builder<JpgTask> builder = new ImmutableList.Builder<JpgTask>();

                    for (final String fingerprint : jpgSet) {
                        if (monitorTask.isCancelled()) throw new InterruptedException();
                        JpgTask jpgTask = new JpgTask(fingerprint);
                        executors.jpg.submit(jpgTask);
                        builder.add(jpgTask);


                    }

                    ImmutableList<JpgTask> jpgFutureTasks = builder.build();
                    return jpgFutureTasks;
                }
            });

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


    private final class ProcessJpgSetTask2 extends MyFutureTask<Void> {

        private ProcessJpgSetTask2(final Slice slice, final Set<String> jpgSet) {
            super(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    JpgSetAction a = new JpgSetAction(repos, seriesId, slice, jpgWidth);
                    a.writeJpgSetIfNotExists(jpgSet);
                    return null;
                }
            });

        }

        public void awaitDoneDeep() throws ExecutionException, InterruptedException {
            get();
        }
    }

    public Stats getStats() {
        return stats;
    }

    private final class JpgTask extends MyFutureTask<JpgState> {

        private JpgTask(final String fingerprint) {
            super(new Callable<JpgState>() {
                @Override
                public JpgState call() throws Exception {

                    if (monitorTask.isCancelled()) throw new InterruptedException();

                    JpgKey jpgKey = new JpgKey(seriesId.getSeriesKey(), jpgWidth, fingerprint);
                    JpgGeneratorPureJava jpgGeneratorPureJava = new JpgGeneratorPureJava(repos, jpgKey);
                    jpgGeneratorPureJava.generate(stats);

//                    if(fingerprint.equals("cae39a7-180eda8")) throw new RuntimeException();

                    return JpgState.COMPLETE;
                }


            });
        }

        public void awaitDoneDeep() throws ExecutionException, InterruptedException {
            get();
        }
    }

    public MasterTask getFuture() {
        return masterTask;
    }

    private void createGenVersionDirs() {
        repos.createGenVersionDirs(seriesId, jpgWidth);
    }

    private void createJpgWidthDir() {
        SeriesRepo seriesRepo = repos.getSeriesRepo(seriesId.getSeriesKey());
        File jpgDirForSize = seriesRepo.getRtRepo().getJpgDirForSize(jpgWidth);
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


    private static Log log = LogFactory.getLog(Master.class);

    public SeriesId getSeriesId() {
        return seriesId;
    }

    public JpgWidth getJpgWidth() {
        return jpgWidth;
    }


    @Override
    public String toString() {
        return id + " " + seriesId + " " + jpgWidth;
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
            super(nThreads,
                    nThreads,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
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


    public ImmutableList<ExecutorStatus> getExecutorStatuses() {
        return executors.getStatuses();
    }

    private static class MyExecutors {

        public final MyThreadPoolExecutor<MonitorTask> monitorExecutor = new MyThreadPoolExecutor<MonitorTask>(MonitorTask.class, 1);
        public final MyThreadPoolExecutor<MasterTask> masterExecutor = new MyThreadPoolExecutor<MasterTask>(MasterTask.class, 1);
        public final MyThreadPoolExecutor createJpgSets = new MyThreadPoolExecutor(CreateJpgSetsTask.class, 1);
        public final MyThreadPoolExecutor createJpgSet = new MyThreadPoolExecutor(CreateJpgSetTask.class, 1);
        public final MyThreadPoolExecutor processJpgSet1 = new MyThreadPoolExecutor(ProcessJpgSetTask1.class, 1);
        public final MyThreadPoolExecutor processJpgSet2 = new MyThreadPoolExecutor(ProcessJpgSetTask2.class, 1);
        public final MyThreadPoolExecutor jpg;


        private final ImmutableList<MyThreadPoolExecutor> executors;


        private MyExecutors(final int threadCount) {
            jpg = new MyThreadPoolExecutor(JpgTask.class, threadCount);


            executors = new ImmutableList.Builder<MyThreadPoolExecutor>()
                    .add(monitorExecutor)
                    .add(masterExecutor)
                    .add(createJpgSets)
                    .add(createJpgSet)
                    .add(processJpgSet1)
                    .add(processJpgSet2)
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
