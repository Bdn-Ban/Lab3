import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class trspo_2 {

    // Функція для обчислення кроків Коллатца для одного числа
    private static long collatzSteps(long n) {
        long steps = 0;
        while (n != 1) {
            if ((n & 1) == 0) {
                n = n / 2;
            } else {
                n = 3 * n + 1;
            }
            steps++;
        }
        return steps;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        final int N = 10_000_000; // Верхня межа чисел
        final int numThreads = Runtime.getRuntime().availableProcessors(); // Кількість потоків
        final int chunkSize = 50_000; // Розмір підзадачі для воркерів

        System.out.println("Обчислення для N = " + N);
        System.out.println("Використовується " + numThreads + " потокiв");

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<long[]>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        // Розбиваємо числа на чанки і відправляємо у воркери
        for (int start = 1; start <= N; start += chunkSize) {
            final int s = start;
            final int e = Math.min(N + 1, start + chunkSize);
            Callable<long[]> task = () -> {
                long sumSteps = 0;
                long count = 0;
                for (int i = s; i < e; i++) {
                    sumSteps += collatzSteps(i);
                    count++;
                }
                return new long[]{count, sumSteps};
            };
            futures.add(executor.submit(task));
        }

        long totalCount = 0;
        long totalSteps = 0;

        // Агрегуємо результати по мірі готовності
        for (Future<long[]> f : futures) {
            long[] result = f.get();
            totalCount += result[0];
            totalSteps += result[1];
        }

        executor.shutdown();

        double averageSteps = (double) totalSteps / totalCount;
        long endTime = System.currentTimeMillis();
        System.out.println("Загальна кiлькiсть чисел: " + totalCount);
        System.out.println("Сумарна кiлькiсть крокiв: " + totalSteps);
        System.out.println("Середня кiлькiсть крокiв до 1: " + averageSteps);
        System.out.println("Час виконання: " + (endTime - startTime) / 1000.0 + " секунд");
    }
}