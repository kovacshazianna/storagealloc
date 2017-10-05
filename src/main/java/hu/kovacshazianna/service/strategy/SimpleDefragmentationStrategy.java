package hu.kovacshazianna.service.strategy;

import hu.kovacshazianna.service.StorageManager.DataBlock;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.copyOfRange;

/**
 * Simple defragmentaion strategy which shifts the allocated parts next to each other.
 *
 * @author Anna_Kovacshazi
 */
public class SimpleDefragmentationStrategy implements DefragmentationStrategy {
    @Override
    public void defragment(List<DataBlock> orderedBlocks, Map<DataBlock, Pair<Integer, Integer>> mapper, byte[][] allStorage) {
        //Modifies the mapper and the allStorage
        //orderedBlocks remain unchanged since this will not change the order of the blocks
        //Shifts all the data blocks in allStorage right after to the previous data block
        //Note that it just copies the data but will not delete it from it's original posizion
        //since it is not a problem because the original position will not be referenced by the mapper
        //or will be overriden when it is needed
        //TODO Algorithm could be improved: if there is space before the first block than this will keep that space
        //and the {@link InMemoryStorageManagerImpl} supposes that after the defragmentation we have all the free space at the end of the storage
        for (int i = 0; i < orderedBlocks.size() - 1; i++) {
            DataBlock next = orderedBlocks.get(i + 1);
            Pair<Integer, Integer> currentPair = mapper.get(orderedBlocks.get(i));
            Pair<Integer, Integer> nextPair = mapper.get(next);
            int freeSpace = nextPair.getKey() - currentPair.getValue() - 1;
            if(freeSpace >= 1) {
                int currentEndIndex = currentPair.getValue();
                int nextLength = nextPair.getValue() - nextPair.getKey() + 1;
                mapper.put(next, new Pair<>(currentEndIndex + 1, currentEndIndex + nextLength));
                byte[][] storageBlock = copyOfRange(allStorage, currentEndIndex + 1, currentEndIndex + freeSpace + nextLength + 1);
                for (int j = 0; j < storageBlock.length; j++) {
                    if (j < nextLength) {
                        allStorage[currentEndIndex + j + 1] = storageBlock[freeSpace + j];
                    }
                }
            }
        }
    }
}
