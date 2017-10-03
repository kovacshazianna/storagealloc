package hu.kovacshazianna.service.strategy;

import hu.kovacshazianna.service.StorageManager.DataBlock;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;

import static hu.kovacshazianna.service.InMemoryStorageManagerImpl.MAX_BLOCK_NUMBER;

/**
 * Returns the smallest suitable place for the given number of blocks.
 *
 * @author Anna_Kovacshazi
 */
public class SmallestSpaceAllocationStrategy implements AllocationStrategy {

    @Override
    public int getFreeSpaceStartIndexFor(int numBlocksRequired, List<DataBlock> orderedBlocks, Map<DataBlock, Pair<Integer, Integer>> mapper) {
        Pair<Integer, Integer> actualSpace;
        Pair<Integer, Integer> space = new Pair<>(-1, MAX_BLOCK_NUMBER);
        int startIndex = -1;

        //TODO thinking about refactor
        for (int i = 0; i < orderedBlocks.size() - 1; i++) {
            Pair<Integer, Integer> current = mapper.get(orderedBlocks.get(i));
            Pair<Integer, Integer> next = mapper.get(orderedBlocks.get(i + 1));

            actualSpace = new Pair<>(i, next.getKey() - current.getValue());
            if (isActualSuitable(numBlocksRequired, actualSpace, space)) {
                space = actualSpace;
                startIndex = getStartIndex(current);
            }

            //checks the beginning
            if (i == 0) {
                actualSpace = new Pair<>(i, current.getKey());
                if (isActualSuitable(numBlocksRequired, actualSpace, space)) {
                    space = actualSpace;
                    startIndex = 0;
                }
            }

            //checks the remaining space at the end
            if (i == orderedBlocks.size() - 2) {
                actualSpace = new Pair<>(i, MAX_BLOCK_NUMBER - next.getValue() - 1);
                if (isActualSuitable(numBlocksRequired, actualSpace, space)) {
                    space = actualSpace;
                    startIndex = getStartIndex(next);
                }
            }
        }
        return startIndex;
    }

    private boolean isActualSuitable(int numBlocksRequired, Pair<Integer, Integer> actualSpace, Pair<Integer, Integer> space) {
        return space.getValue() > actualSpace.getValue() && actualSpace.getValue() >= numBlocksRequired
                ? true : false;
    }

    private int getStartIndex(Pair<Integer, Integer> indexes) {
        return indexes.getValue() + 1;
    }
}
