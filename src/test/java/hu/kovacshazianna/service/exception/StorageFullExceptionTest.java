package hu.kovacshazianna.service.exception;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Unit test for {@link StorageFullException}.
 *
 * @author Anna_Kovacshazi
 */
public class StorageFullExceptionTest {

    private static final int required = 5;
    private static final int actual = 3;

    private StorageFullException exception;

    @BeforeMethod
    public void init() {
        exception = new StorageFullException(required, actual);
    }

    @Test
    public void shouldReturnMessage() {
        assertThat(exception.getMessage(), is("Not enough storage! Required " + required + " bytes, actual " + actual + " bytes"));
    }

}
