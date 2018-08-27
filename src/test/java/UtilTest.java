import org.junit.Test;

import static org.junit.Assert.*;

public class UtilTest {

    /**
     * Assert it returns a random integer on the interval [0, Max-1]
     */
    @Test
    public void randomInt() {
        for (int i = 0; i < 100; i++) {
            int randomInt = Util.randomInt(i);
            assertTrue(randomInt <= i);
        }
    }
}