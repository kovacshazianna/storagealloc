package hu.kovacshazianna.service.strategy;

import hu.kovacshazianna.service.StorageManager.DataBlock;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;

/**
 * Interface for allocation strategies.
 *
 * @author Anna_Kovacshazi
 */
public interface AllocationStrategy {

    /**
     * Returns the start index in the storage where the blocks can be allocated.
     * @param numBlocksRequired required number of blocks
     * @param orderedBlocks list of {@DataBlocks} ordered by their order in the storage
     * @param mapper data block and storage map with start and end index pairs
     * @return the start index of the storage where numBlocksRequired number of data can be allocated
     */
    int getFreeSpaceStartIndexFor(int numBlocksRequired, List<DataBlock> orderedBlocks, Map<DataBlock, Pair<Integer, Integer>> mapper);
}
