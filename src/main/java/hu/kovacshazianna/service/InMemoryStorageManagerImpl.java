package hu.kovacshazianna.service;

import com.google.common.annotations.VisibleForTesting;
import hu.kovacshazianna.service.exception.StorageFullException;
import hu.kovacshazianna.service.strategy.AllocationStrategy;
import hu.kovacshazianna.service.strategy.DefragmentationStrategy;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;

/**
 * In memory implementation for handling storage related operations.
 *
 * @author Anna_Kovacshazi
 */
public class InMemoryStorageManagerImpl extends DelegatableDataBlockIO implements StorageManager {

    private static final Object GUARD = new Object();

    public static final int MAX_BLOCK_NUMBER = 100000;
    public static final int MAX_BYTE_NUMBER = 1024;

    private byte[][] allStorage = new byte[MAX_BLOCK_NUMBER][MAX_BYTE_NUMBER];
    private Map<DataBlock, Pair<Integer, Integer>> mapper = new HashMap<>(); //DataBlock - startIndex, endIndex in the allStorage
    private List<DataBlock> orderedBlocks = new ArrayList<>(); //ordered list of datablocks (order in the allStorage)

    private final AllocationStrategy allocationStrategy;
    private final DefragmentationStrategy defragmentationStrategy;

    public InMemoryStorageManagerImpl(AllocationStrategy allocationStrategy, DefragmentationStrategy defragmentationStrategy) {
        this.allocationStrategy = requireNonNull(allocationStrategy);
        this.defragmentationStrategy = requireNonNull(defragmentationStrategy);
    }

    @Override
    public DataBlock allocate(int numBlocksRequired) throws StorageFullException {
        synchronized (GUARD) {
            System.out.println("Required blocks: " + numBlocksRequired); //this could be a LOG.INFO but now it is enough to write it into the console
            DataBlock dataBlock;
            int startIndex = allocationStrategy.getFreeSpaceStartIndexFor(numBlocksRequired, orderedBlocks, mapper);
            if (startIndex >= 0) {
                System.out.println("Enough space for " + numBlocksRequired + " blocks");
                dataBlock = allocateStorage(startIndex, numBlocksRequired);
            } else {
                int sumFreeSpace = getFreeSpaceSum();
                if (sumFreeSpace >= numBlocksRequired) {
                    System.out.println("Enough total space, required: " + numBlocksRequired + ", free in total: " + sumFreeSpace + " ... defragmentation");
                    System.out.println("Number of free blocks at the end before fragmentation: " + getFreeBlockNumberAtTheEndOfTheStorage());
                    defragmentationStrategy.defragment(orderedBlocks, mapper, allStorage);
                    System.out.println("Number of free blocks at the end after fragmentation: " + getFreeBlockNumberAtTheEndOfTheStorage());
                    dataBlock = allocateStorage(allocationStrategy.getFreeSpaceStartIndexFor(numBlocksRequired, orderedBlocks, mapper), numBlocksRequired);
                } else {
                    throw new StorageFullException(numBlocksRequired * MAX_BYTE_NUMBER, sumFreeSpace * MAX_BYTE_NUMBER);
                }
            }
            System.out.println("Number of free blocks at the end: " + getFreeBlockNumberAtTheEndOfTheStorage());
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
                System.out.println("Release " + (indexes.getValue() - indexes.getKey() + 1) + " blocks");
            });
            return indexes != null;
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

    @VisibleForTesting
    int getFreeBlockNumberAtTheEndOfTheStorage() {
        return mapper.size() > 0 ?
            MAX_BLOCK_NUMBER - mapper.get(orderedBlocks.get(orderedBlocks.size() - 1)).getValue() - 1 : MAX_BLOCK_NUMBER;
    }

    private DataBlock allocateStorage(int startIndex, int numBlocksRequired) {
        int endIndex = startIndex + numBlocksRequired - 1;
        DataBlock dataBlock = new DataBlock(this);
        mapper.put(dataBlock, new Pair<>(startIndex, endIndex));
        orderedBlocks.add(dataBlock);
        reorderBlocks();
        IntStream.rangeClosed(startIndex, endIndex).forEach(index -> allStorage[index] = new byte[]{});
        return dataBlock;
    }

    private void reorderBlocks() {
        orderedBlocks = orderedBlocks.stream()
            .sorted((o1, o2) -> mapper.get(o1).getKey() - mapper.get(o2).getKey())
            .collect(Collectors.toList());
    }

    private int getFreeSpaceSum() {
        return MAX_BLOCK_NUMBER - orderedBlocks.stream()
            .mapToInt(element -> mapper.get(element).getValue() - mapper.get(element).getKey() + 1)
            .sum();
    }
}
