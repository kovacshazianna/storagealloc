package hu.kovacshazianna.service.strategy;

import hu.kovacshazianna.service.InMemoryStorageManagerImpl;
import hu.kovacshazianna.service.StorageManager.DataBlock;
import javafx.util.Pair;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static hu.kovacshazianna.service.InMemoryStorageManagerImpl.MAX_BLOCK_NUMBER;
import static hu.kovacshazianna.service.InMemoryStorageManagerImpl.MAX_BYTE_NUMBER;
import static java.util.Arrays.asList;
import static java.util.Arrays.copyOfRange;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit test for {@link SimpleDefragmentationStrategy}.
 */
public class SimpleDefragmentationStrategyTest {

    @Mock
    private InMemoryStorageManagerImpl storageManager;

    private DataBlock first;
    private DataBlock second;
    private DataBlock third;
    private DataBlock fourth;
    private List<DataBlock> orderedBlocks;
    private Map<DataBlock, Pair<Integer, Integer>> mapper;
    private byte[][] allStorage = new byte[MAX_BLOCK_NUMBER][MAX_BYTE_NUMBER];

    private SimpleDefragmentationStrategy defragmentationStrategy;

    public SimpleDefragmentationStrategyTest() {
    }

    @BeforeMethod
    public void init() {
        initMocks(this);
        defragmentationStrategy = new SimpleDefragmentationStrategy();

        first = new DataBlock(storageManager);
        second = new DataBlock(storageManager);
        third = new DataBlock(storageManager);
        fourth = new DataBlock(storageManager);

        orderedBlocks = asList(first, second, third, fourth);

        mapper = new HashMap<>();
        mapper.put(first, new Pair(2, 4));
        mapper.put(second, new Pair(6, 7));
        mapper.put(third, new Pair(11, 11));
        mapper.put(fourth, new Pair(13, 14));

        allStorage[2] = new byte[] {2, 2};
        allStorage[3] = new byte[] {3, 3, 3};
        allStorage[4] = new byte[] {4};
        allStorage[6] = new byte[] {6, 6};
        allStorage[7] = new byte[] {7};
        allStorage[11] = new byte[] {1, 1, 1, 1};
        allStorage[13] = new byte[] {1, 3, 1, 3};
        allStorage[14] = new byte[] {1, 4, 1, 4};
    }

    @Test
    public void shouldDefragment() {
        defragmentationStrategy.defragment(orderedBlocks, mapper, allStorage);

        byte[][] expectedAllStorage = new byte[MAX_BLOCK_NUMBER][MAX_BYTE_NUMBER];
        expectedAllStorage[2] = new byte[] {2, 2};
        expectedAllStorage[3] = new byte[] {3, 3, 3};
        expectedAllStorage[4] = new byte[] {4};
        expectedAllStorage[5] = new byte[] {6, 6};
        expectedAllStorage[6] = new byte[] {7};
        expectedAllStorage[7] = new byte[] {1, 1, 1, 1};
        expectedAllStorage[8] = new byte[] {1, 3, 1, 3};
        expectedAllStorage[9] = new byte[] {1, 4, 1, 4};

        byte[][] actualSlice = copyOfRange(allStorage, 0, 10);
        byte[][] expectedSlice = copyOfRange(expectedAllStorage, 0, 10);
        //the defragmentation will not delete the old data at the end of allStorage
        //so it will remain unti it is not overridden with something else
        IntStream.rangeClosed(0, 9).forEach(index -> {
            assertThat(Arrays.equals(actualSlice[index], expectedSlice[index]), is(true));
        });

        Map<DataBlock, Pair<Integer, Integer>> expectedMapper = new HashMap<>();
        expectedMapper.put(first, new Pair(2, 4));
        expectedMapper.put(second, new Pair(5, 6));
        expectedMapper.put(third, new Pair(7, 7));
        expectedMapper.put(fourth, new Pair(8, 9));

        assertThat(mapper, is(expectedMapper));
    }
}
