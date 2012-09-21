package c3i.scratch;

import javax.annotation.concurrent.Immutable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Foo {


    private static final int THREAD_COUNT = 3;
    public final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

    public void test1() throws Exception {

        Future<ResultOfSomeLongComputation> future = executor.submit(new SomeLongComputation(23, 42));

        ResultOfSomeLongComputation result = future.get(); //blocks until computation is complete

    }

    @Immutable
    static class ResultOfSomeLongComputation {

        private final int output1;
        private final int output2;

        ResultOfSomeLongComputation(int output1, int output2) {
            this.output1 = output1;
            this.output2 = output2;
        }

        public int getOutput2() {
            return output2;
        }

        public int getOutput1() {
            return output1;
        }
    }

    static class SomeLongComputation implements Callable<ResultOfSomeLongComputation> {

        private final int input1;
        private final int input2;

        SomeLongComputation(int input1, int input2) {
            this.input1 = input1;
            this.input2 = input2;
        }

        @Override
        public ResultOfSomeLongComputation call() throws Exception {
            //do long computation
            return new ResultOfSomeLongComputation(10, 10);
        }
    }
}
