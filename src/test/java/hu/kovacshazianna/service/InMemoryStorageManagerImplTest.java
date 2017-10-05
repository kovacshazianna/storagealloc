package hu.kovacshazianna.service;

import hu.kovacshazianna.service.StorageManager.DataBlock;
import hu.kovacshazianna.service.exception.StorageFullException;
import hu.kovacshazianna.service.strategy.AllocationStrategy;
import hu.kovacshazianna.service.strategy.DefragmentationStrategy;
import javafx.util.Pair;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.IntStream;

import static hu.kovacshazianna.service.InMemoryStorageManagerImpl.MAX_BLOCK_NUMBER;
import static hu.kovacshazianna.service.InMemoryStorageManagerImpl.MAX_BYTE_NUMBER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit test for {@link InMemoryStorageManagerImpl}.
 *
 * @author Anna_Kovacshazi
 */
public class InMemoryStorageManagerImplTest {

    @Mock
    private AllocationStrategy allocationStrategy;
    @Mock
    private DefragmentationStrategy defragmentationStrategy;

    private InMemoryStorageManagerImpl storageManager;

    @BeforeMethod(alwaysRun = true)
    public void init() {
        initMocks(this);
        storageManager = new InMemoryStorageManagerImpl(allocationStrategy, defragmentationStrategy);
    }

    @Test
    public void shouldAllocateWhenThereIsEnoughSpaceInALine() {
        Map<DataBlock, Pair<Integer, Integer>> mapper = new HashMap<>();
        List<DataBlock> orderedBlocks = new ArrayList<>();

        //numBlocksRequired = 50005
        when(allocationStrategy.getFreeSpaceStartIndexFor(50005, orderedBlocks, mapper)).thenReturn(10);
        storageManager.allocate(50005);
        assertThat(storageManager.getFreeBlockNumberAtTheEndOfTheStorage(), is(49985));
    }

    @Test
    public void shouldAllocateWhenThereIsEnoughSpaceInTheStorageInAll() {
        byte[][] allStorage = new byte[MAX_BLOCK_NUMBER][MAX_BYTE_NUMBER];
        Map<DataBlock, Pair<Integer, Integer>> mapper = new HashMap<>();
        List<DataBlock> orderedBlocks = new ArrayList<>();

        //numBlocksRequired = 50000
        when(allocationStrategy.getFreeSpaceStartIndexFor(50000, orderedBlocks, mapper)).thenReturn(2);
        putDataInto(mapper, orderedBlocks, 50000, 2);
        IntStream.rangeClosed(2, 50001).forEach(index -> allStorage[index] = new byte[] {});

        //numBlocksRequired = 49995
        when(allocationStrategy.getFreeSpaceStartIndexFor(49995, orderedBlocks, mapper)).thenReturn(50003);
        putDataInto(mapper, orderedBlocks, 49995, 50003);
        IntStream.rangeClosed(50003, 99997).forEach(index -> allStorage[index] = new byte[] {});

        when(allocationStrategy.getFreeSpaceStartIndexFor(eq(5), argThat(listMatcher(orderedBlocks)), argThat(mapMatcher(mapper)))).thenReturn(-1);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] args = invocationOnMock.getArguments();
                //orderedBlocks
                ((List)args[0]).clear();
                //mapper
                ((Map)args[1]).clear();
                //allStorage
                Arrays.fill(((byte[][])args[2]), null);
                return null;
            }
        }).when(defragmentationStrategy).defragment(argThat(listMatcher(orderedBlocks)), argThat(mapMatcher(mapper)), eq(allStorage));

        storageManager.allocate(5);
        assertThat(storageManager.getFreeBlockNumberAtTheEndOfTheStorage(), is(MAX_BLOCK_NUMBER - 5));
    }

    @Test
    public void shouldAllocateThrowExceptionWhenThereIsNotEnoughSpace() {
        Map<DataBlock, Pair<Integer, Integer>> mapper = new HashMap<>();
        List<DataBlock> orderedBlocks = new ArrayList<>();

        //numBlocksRequired = 50001
        when(allocationStrategy.getFreeSpaceStartIndexFor(50001, orderedBlocks, mapper)).thenReturn(0);
        putDataInto(mapper, orderedBlocks, 50001, 0);

        //numBlocksRequired = 49994
        when(allocationStrategy.getFreeSpaceStartIndexFor(49994, orderedBlocks, mapper)).thenReturn(50001);
        putDataInto(mapper, orderedBlocks, 49994, 50001);

        //numBlocksRequired = 10
        when(allocationStrategy.getFreeSpaceStartIndexFor(eq(10), argThat(listMatcher(orderedBlocks)), argThat(mapMatcher(mapper)))).thenReturn(-1);

        try {
            storageManager.allocate(10);
        } catch (StorageFullException ex) {
            assertThat(ex.getMessage(), is("Not enough storage! Required 10240 bytes, actual 5120 bytes"));
        }
    }

    @Test
    public void shouldRelease() {
        Map<DataBlock, Pair<Integer, Integer>> mapper = new HashMap<>();
        List<DataBlock> orderedBlocks = new ArrayList<>();

        //numBlocksRequired = 10
        when(allocationStrategy.getFreeSpaceStartIndexFor(10, orderedBlocks, mapper)).thenReturn(1);
        DataBlock dataBlock = storageManager.allocate(10);

        assertThat(storageManager.release(dataBlock), is(true));
    }

    @Test
    public void shouldReleaseReturnFalseWhenNothingToRelease() {
        assertThat(storageManager.release(new DataBlock(storageManager)), is(false));
    }

    private void putDataInto(Map<DataBlock, Pair<Integer, Integer>> mapper, List<DataBlock> orderedBlocks, int numBlocksRequired, int start) {
        DataBlock dataBlock = storageManager.allocate(numBlocksRequired);
        mapper.put(dataBlock, new Pair<>(start, start + numBlocksRequired - 1));
        orderedBlocks.add(dataBlock);
    }

    private ArgumentMatcher<List<DataBlock>> listMatcher(List<DataBlock> actual) {
        return new ArgumentMatcher<List<DataBlock>>() {
            @Override
            public boolean matches(Object o) {
                List<DataBlock> expected = (List<DataBlock>) o;
                return expected.containsAll(actual) && actual.containsAll(expected);
            }
        };
    }

    private ArgumentMatcher<Map<DataBlock, Pair<Integer, Integer>>> mapMatcher(Map<DataBlock, Pair<Integer, Integer>> actual) {
        return new ArgumentMatcher<Map<DataBlock, Pair<Integer, Integer>>>() {
            @Override
            public boolean matches(Object o) {
                Map<DataBlock, Pair<Integer, Integer>> expected = (Map<DataBlock, Pair<Integer, Integer>>) o;
                return expected.entrySet().containsAll(actual.entrySet())
                    && expected.keySet().containsAll(actual.keySet())
                    && actual.entrySet().containsAll(expected.entrySet())
                    && actual.keySet().containsAll(expected.keySet());
            }
        };
    }
}
