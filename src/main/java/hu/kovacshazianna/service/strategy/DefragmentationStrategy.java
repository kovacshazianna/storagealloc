package hu.kovacshazianna.service.strategy;

import hu.kovacshazianna.service.StorageManager;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;

/**
 * Interface for defragmentation strategies.
 *
 * @author Anna_Kovacshazi
 */
public interface DefragmentationStrategy {

    /**
     * Defragments the storage.
     * @param orderedBlocks ordered list of {@DataBlocks} in the storage
     * @param mapper data block and storage map with start and end index pairs
     * @param allStorage storage to be defragmented
     */
    void defragment(List<StorageManager.DataBlock> orderedBlocks, Map<StorageManager.DataBlock, Pair<Integer, Integer>> mapper, byte[][] allStorage);
}
