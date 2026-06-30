import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class ThreadPoolBenchmark {

    private static final int TASK_COUNT = 10000;
    private static final int FIXED_POOL_SIZE = 100;

    public static void main(String[] args) {
        System.out.println("Starting ThreadPool benchmark...");
        System.out.println("Total tasks to submit: " + TASK_COUNT);
        System.out.println("----------------------------------------");

        testFixedThreadPool();
        testCachedThreadPool();
        testVirtualThreads();
    }

    private static void testFixedThreadPool() {
        System.out.println("Testing FixedThreadPool (" + FIXED_POOL_SIZE + " threads)...");
        // фиксированный пул потоков
        ExecutorService executor = Executors.newFixedThreadPool(FIXED_POOL_SIZE);
        runBenchmark(executor);
    }

    private static void testCachedThreadPool() {
        System.out.println("Testing CachedThreadPool...");
        // кэшированный пул потоков
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            runBenchmark(executor);
        } catch (OutOfMemoryError e) {
            System.err.println("Warning: OutOfMemoryError caught in CachedThreadPool. Too many OS threads created.");
        }
    }

    private static void testVirtualThreads() {
        System.out.println("Testing Virtual Threads...");
        // виртуальные потоки
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        runBenchmark(executor);
    }

    private static void runBenchmark(ExecutorService executor) {
        // создаем защелку
        CountDownLatch latch = new CountDownLatch(TASK_COUNT);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < TASK_COUNT; i++) {
            executor.submit(() -> {
                try {
                    // симулируем задержку
                    long sleepTime = ThreadLocalRandom.current().nextLong(100, 501);
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            // ждем завершения потоков
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("Execution time: " + duration + " ms\n");
        executor.shutdownNow();
    }
}
