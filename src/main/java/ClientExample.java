import hu.kovacshazianna.service.InMemoryStorageManagerImpl;
import hu.kovacshazianna.service.StorageManager;
import hu.kovacshazianna.service.StorageManager.DataBlock;
import hu.kovacshazianna.service.strategy.SimpleDefragmentationStrategy;
import hu.kovacshazianna.service.strategy.SmallestSpaceAllocationStrategy;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Example for client.
 *
 * @author Anna_Kovacshazi
 */
public class ClientExample {

    public static void main(String[] args) {
        StorageManager manager = new InMemoryStorageManagerImpl(new SmallestSpaceAllocationStrategy(), new SimpleDefragmentationStrategy());

        IntStream.rangeClosed(10, 30).forEach(i1 -> {
            IntStream.rangeClosed(1, 20).forEach(i2 -> {
                startThread(manager, i1 * 1000);
                try {
                    Thread.currentThread().sleep(ThreadLocalRandom.current().nextInt(0, 200));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private static void startThread(StorageManager manager, int numRequiredBlocks) {
        (new Thread() {
            public void run() {
                DataBlock dataBlock = manager.allocate(numRequiredBlocks);
                //dataBlock.write(new byte[] {0, 1, 2, 3, 4});
                //dataBlock.read();
                try {
                    Thread.currentThread().sleep(ThreadLocalRandom.current().nextInt(0, 500));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                manager.release(dataBlock);
            }
        }).start();
    }
}
