import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ReaderTest {
    private final int STOPS = 13;
    private Reader reader;

    @Before
    public void setUp() throws Exception {
        reader = new Reader("orders_1_test");
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void getStops() throws IOException {
        assertEquals(STOPS, reader.getStops());
    }

    @Test
    public void getCoordinates() {
        float[][] coordinates = reader.getCoordinates();
        assertEquals(STOPS, coordinates.length);
        assertEquals(2, coordinates[0].length);
    }

    @Test
    public void testThrowsFileNotFoundException() throws IOException {
        exception.expect(FileNotFoundException.class);
        Reader reader = new Reader("");
    }
}