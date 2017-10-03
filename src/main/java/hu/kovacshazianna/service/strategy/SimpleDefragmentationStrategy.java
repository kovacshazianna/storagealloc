package hu.kovacshazianna.service.strategy;

import hu.kovacshazianna.service.StorageManager;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Simple defragmentaion strategy.
 *
 * @author Anna_Kovacshazi
 */
public class SimpleDefragmentationStrategy implements DefragmentationStrategy {
    @Override
    public void defragment(List<StorageManager.DataBlock> orderedBlocks, Map<StorageManager.DataBlock, Pair<Integer, Integer>> mapper, byte[][] allStorage) {
        //TODO Implement defragmentation
        //Started implementing something which simply shifts the blocks in the allStorage, but it is not finished.
        //TODO Change the orderedBlocks and mapper too.
        /*for (int i = 0; i < orderedBlocks.size() - 1; i++) {
            StorageManager.DataBlock next = orderedBlocks.get(i + 1);
            Pair<Integer, Integer> currentPair = mapper.get(orderedBlocks.get(i));
            Pair<Integer, Integer> nextPair = mapper.get(next);
            int freeSpace = nextPair.getKey() - currentPair.getValue();
            if(freeSpace > 1) {
                int currentEndIndex = currentPair.getValue();
                int nextLength = nextPair.getValue() - nextPair.getKey() + 1;
                mapper.put(next, new Pair<>(currentEndIndex + 1, currentEndIndex + nextLength));
                byte[][] storageBlock = Arrays.copyOfRange(allStorage, currentEndIndex + 1, currentEndIndex + freeSpace + nextLength - 1);
                for (int j = 0; j < storageBlock.length; j++) {
                    if (j <= nextLength) {
                        allStorage[currentEndIndex + 1] = storageBlock[freeSpace + j];
                    }
                }
            }
        }*/
    }
}
