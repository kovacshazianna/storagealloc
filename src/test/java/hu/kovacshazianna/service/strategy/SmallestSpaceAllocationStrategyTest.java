package hu.kovacshazianna.service.strategy;

import com.google.common.collect.ImmutableMap;
import hu.kovacshazianna.service.InMemoryStorageManagerImpl;
import hu.kovacshazianna.service.StorageManager.DataBlock;
import javafx.util.Pair;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
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

    private DataBlock FIRST;
    private DataBlock SECOND;
    private DataBlock THIRD;
    private DataBlock FOURTH;
    private DataBlock FIFTH;
    private List<DataBlock> ORDERED_BLOCKS;
    private Map<DataBlock, Pair<Integer, Integer>> MAPPER;

    private SmallestSpaceAllocationStrategy strategy;

    @BeforeMethod
    public void init() {
        initMocks(this);
        strategy = new SmallestSpaceAllocationStrategy();

        FIRST = new DataBlock(storageManager);
        SECOND = new DataBlock(storageManager);
        THIRD = new DataBlock(storageManager);
        FOURTH = new DataBlock(storageManager);
        FIFTH = new DataBlock(storageManager);

        ORDERED_BLOCKS = Arrays.asList(FIRST, SECOND, THIRD, FOURTH, FIFTH);
        MAPPER = ImmutableMap.of(
                //3, 8, 6, 9
                FIRST, new Pair(2, 10),
                SECOND, new Pair(13, 15),
                THIRD, new Pair(23, 27),
                FOURTH, new Pair(33, 33),
                FIFTH, new Pair(34, 43)
        );
    }

    @Test
    public void shouldReturnStartIndexWhenSmallestSpaceIsAtTheBeginningOfTheStorage() {
        assertThat(strategy.getFreeSpaceStartIndexFor(2, ORDERED_BLOCKS, MAPPER), is(0));
    }

    @Test
    public void shouldReturnStartIndexWhenSmallestSpaceIsBetweenTwoBlcoks() {
        assertThat(strategy.getFreeSpaceStartIndexFor(5, ORDERED_BLOCKS, MAPPER), is(28));
    }

    @Test
    public void shouldReturnStartIndexWhenSmallestSpaceIsAtTheEndOfTheStorage() {
        assertThat(strategy.getFreeSpaceStartIndexFor(100, ORDERED_BLOCKS, MAPPER), is(44));
    }

    @Test
    public void shouldReturnDefaultStartIndexIfNotEnoughSpace() {
        assertThat(strategy.getFreeSpaceStartIndexFor(999998, ORDERED_BLOCKS, MAPPER), is(-1));
    }
}
