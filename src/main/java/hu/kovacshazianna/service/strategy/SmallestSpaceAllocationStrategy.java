package hu.kovacshazianna.service.strategy;

import hu.kovacshazianna.service.StorageManager.DataBlock;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;

import static hu.kovacshazianna.service.InMemoryStorageManagerImpl.MAX_BLOCK_NUMBER;

/**
 * Returns the smallest suitable place for the required number of blocks.
 *
 * @author Anna_Kovacshazi
 */
public class SmallestSpaceAllocationStrategy implements AllocationStrategy {

    @Override
    public int getFreeSpaceStartIndexFor(int numBlocksRequired, List<DataBlock> orderedBlocks, Map<DataBlock, Pair<Integer, Integer>> mapper) {
        int startIndex = -1;
        int blockSize = orderedBlocks.size();
        if (blockSize == 0) {
            startIndex = 0;
        } else {
            Pair<Integer, Integer> space = new Pair<>(startIndex, MAX_BLOCK_NUMBER);

            //checks the beginning before the first block
            Pair<Integer, Integer> first = mapper.get(orderedBlocks.get(0));
            Pair<Integer, Integer> actualSpace = new Pair<>(0, first.getKey());
            if (isActualSuitable(numBlocksRequired, actualSpace, space)) {
                space = actualSpace;
                startIndex = 0;
            }

            for (int i = 0; i < blockSize - 1; i++) {
                Pair<Integer, Integer> current = mapper.get(orderedBlocks.get(i));
                Pair<Integer, Integer> next = mapper.get(orderedBlocks.get(i + 1));
                actualSpace = new Pair<>(i, next.getKey() - current.getValue());

                if (isActualSuitable(numBlocksRequired, actualSpace, space)) {
                    space = actualSpace;
                    startIndex = current.getValue() + 1;
                }
            }

            //checks the remaining space at the end
            Pair<Integer, Integer> last = mapper.get(orderedBlocks.get(blockSize - 1));
            actualSpace = new Pair<>(blockSize - 2, MAX_BLOCK_NUMBER - last.getValue() - 1);
            if (isActualSuitable(numBlocksRequired, actualSpace, space)) {
                startIndex = last.getValue() + 1;
            }
        }
        return startIndex;
    }

    private boolean isActualSuitable(int numBlocksRequired, Pair<Integer, Integer> actualSpace, Pair<Integer, Integer> space) {
        return space.getValue() > actualSpace.getValue() && actualSpace.getValue() >= numBlocksRequired;
    }
}
