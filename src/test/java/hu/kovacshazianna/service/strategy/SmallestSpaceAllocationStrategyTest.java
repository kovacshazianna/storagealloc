package hu.kovacshazianna.service.strategy;

import com.google.common.collect.ImmutableMap;
import hu.kovacshazianna.service.InMemoryStorageManagerImpl;
import hu.kovacshazianna.service.StorageManager.DataBlock;
import javafx.util.Pair;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit test for {@link SmallestSpaceAllocationStrategy}.
 *
 * @author Anna_Kovacshazi
 */
public class SmallestSpaceAllocationStrategyTest {

    @Mock
    private InMemoryStorageManagerImpl storageManager;

    private DataBlock first;
    private DataBlock second;
    private DataBlock third;
    private DataBlock fourth;
    private DataBlock fifth;
    private List<DataBlock> orderedBlocks;
    private Map<DataBlock, Pair<Integer, Integer>> mapper;

    private SmallestSpaceAllocationStrategy strategy;

    @BeforeMethod
    public void init() {
        initMocks(this);
        strategy = new SmallestSpaceAllocationStrategy();

        first = new DataBlock(storageManager);
        second = new DataBlock(storageManager);
        third = new DataBlock(storageManager);
        fourth = new DataBlock(storageManager);
        fifth = new DataBlock(storageManager);

        orderedBlocks = Arrays.asList(first, second, third, fourth, fifth);
        mapper = ImmutableMap.of(
                //3, 8, 6, 9
                first, new Pair(2, 10),
                second, new Pair(13, 15),
                third, new Pair(23, 27),
                fourth, new Pair(33, 33),
                fifth, new Pair(34, 43)
        );
    }

    @Test
    public void shouldReturnMaxSpaceNumberWhenStorageIsEmpty() {
        assertThat(strategy.getFreeSpaceStartIndexFor(2, Collections.emptyList(), Collections.emptyMap()), is(0));
    }

    @Test
    public void shouldReturnSmallestSpaceWhenThereIsOnlyOneBlock() {
        DataBlock block = new DataBlock(storageManager);
        assertThat(strategy.getFreeSpaceStartIndexFor(2, Arrays.asList(block), ImmutableMap.of(block, new Pair<>(10, 20))), is(0));
        assertThat(strategy.getFreeSpaceStartIndexFor(20, Arrays.asList(block), ImmutableMap.of(block, new Pair<>(10, 20))), is(21));
    }

    @Test
    public void shouldReturnStartIndexWhenSmallestSpaceIsAtTheBeginningOfTheStorage() {
        assertThat(strategy.getFreeSpaceStartIndexFor(2, orderedBlocks, mapper), is(0));
    }

    @Test
    public void shouldReturnStartIndexWhenSmallestSpaceIsBetweenTwoBlcoks() {
        assertThat(strategy.getFreeSpaceStartIndexFor(5, orderedBlocks, mapper), is(28));
    }

    @Test
    public void shouldReturnStartIndexWhenSmallestSpaceIsAtTheEndOfTheStorage() {
        assertThat(strategy.getFreeSpaceStartIndexFor(100, orderedBlocks, mapper), is(44));
    }

    @Test
    public void shouldReturnDefaultStartIndexIfNotEnoughSpace() {
        assertThat(strategy.getFreeSpaceStartIndexFor(999998, orderedBlocks, mapper), is(-1));
    }
}
