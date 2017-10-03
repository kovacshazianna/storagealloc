package hu.kovacshazianna.service;

import hu.kovacshazianna.service.exception.StorageFullException;
import hu.kovacshazianna.service.strategy.AllocationStrategy;
import hu.kovacshazianna.service.strategy.DefragmentationStrategy;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;

/**
 * Implementation for handling storage related operations.
 *
 * @author Anna_Kovacshazi
 */
public class InMemoryStorageManagerImpl extends DelegatableDataBlockIO implements StorageManager {

    private static final Object GUARD = new Object();

    public static final int MAX_BLOCK_NUMBER = 100000;
    public static final int MAX_BYTE_NUMBER = 1024;

    private byte[][] allStorage = new byte[MAX_BLOCK_NUMBER][MAX_BYTE_NUMBER];
    private Map<DataBlock, Pair<Integer, Integer>> mapper = new HashMap<>(); //DataBlock - startIndex, endIndex
    private List<DataBlock> orderedBlocks = new ArrayList<>(); //ordered list of datablocks

    private final AllocationStrategy allocationStrategy;
    private final DefragmentationStrategy defragmentationStrategy;

    public InMemoryStorageManagerImpl(AllocationStrategy allocationStrategy, DefragmentationStrategy defragmentationStrategy) {
        this.allocationStrategy = requireNonNull(allocationStrategy);
        this.defragmentationStrategy = requireNonNull(defragmentationStrategy);
    }

    @Override
    public DataBlock allocate(int numBlocksRequired) throws StorageFullException {
        synchronized (GUARD) {
            DataBlock dataBlock = null;
            int startIndex = allocationStrategy.getFreeSpaceStartIndexFor(numBlocksRequired, orderedBlocks, mapper);
            if (startIndex >= 0) {
                dataBlock = allocateStorage(startIndex, numBlocksRequired);
            } else {
                int sumFreeSpace = getFreeSpaceSum();
                if (sumFreeSpace >= numBlocksRequired) {
                    defragmentationStrategy.defragment(orderedBlocks, mapper, allStorage);
                    dataBlock = allocateStorage(allocationStrategy.getFreeSpaceStartIndexFor(numBlocksRequired, orderedBlocks, mapper), numBlocksRequired);
                } else {
                    throw new StorageFullException(numBlocksRequired, sumFreeSpace);
                }
            }
            return dataBlock;
        }
    }

    @Override
    public boolean release(DataBlock dataBlock) {
        synchronized (GUARD) {
            Pair<Integer, Integer> indexes = mapper.remove(dataBlock);
            Optional.ofNullable(indexes).ifPresent(pair -> {
                IntStream.rangeClosed(pair.getKey(), pair.getValue())
                    .forEach(index -> allStorage[index] = null);
                orderedBlocks.remove(dataBlock);
            });
            return indexes != null;
        }
    }

    public int getFreeBlockNumberAtTheEnd() {
        synchronized (GUARD) {
            return MAX_BLOCK_NUMBER - mapper.get(orderedBlocks.get(orderedBlocks.size() - 1)).getValue() - 1;
        }
    }

    @Override
    boolean write(DataBlock dataBlock, byte[] data) {
        //TODO
        //Could be used System.arrayCopy in a loop to split the input array into MAX_BYTE_NUMBER big chunks
        //and put them into the allStorage array.
        return false;
    }

    @Override
    byte[] read(DataBlock dataBlock) {
        //TODO
        //Could be used a ByteArrayOutputStream and write the arrays from allStorage into it
        //and use the ByteArrayOutputStream toByteArray() method when returning the byte array.
        return new byte[0];
    }

    private DataBlock allocateStorage(int startIndex, int numBlocksRequired) {
        int endIndex = startIndex + numBlocksRequired - 1;
        DataBlock dataBlock = new DataBlock(this);
        mapper.put(dataBlock, new Pair<>(startIndex, endIndex));
        IntStream.rangeClosed(startIndex, endIndex).forEach(index -> allStorage[index] = new byte[] {});
        orderedBlocks.add(dataBlock);
        reorderBlocks();
        return dataBlock;
    }

    private void reorderBlocks() {
        orderedBlocks = orderedBlocks.stream()
                .sorted((o1, o2) -> mapper.get(o1).getKey() - mapper.get(o2).getKey())
                .collect(Collectors.toList());
    }

    private int getFreeSpaceSum() {
        int count = 0;
        for (int i = 0; i < orderedBlocks.size() - 1; i++) {
            DataBlock current = orderedBlocks.get(i);
            DataBlock next = orderedBlocks.get(i + 1);
            count += mapper.get(next).getKey() - mapper.get(current).getValue() - 1;
            //checks the space in the beginning
            if (i == 0) {
                count += mapper.get(current).getKey();
            }
            //checks the remaining space at the end
            if (i == orderedBlocks.size() - 2) {
                count += MAX_BLOCK_NUMBER - mapper.get(next).getValue() - 1;
            }
        }
        return count;
    }
}
