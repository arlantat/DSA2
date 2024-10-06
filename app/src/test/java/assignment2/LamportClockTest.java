package assignment2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LamportClockTest {

    private LamportClock clock;

    @BeforeEach
    public void setUp() {
        clock = new LamportClock();
    }

    @Test
    public void testInitialization() {
        assertEquals(0, clock.getTime(), "Initial time should be 0");
    }

    @Test
    public void testGetTime() {
        clock.increment();
        assertEquals(1, clock.getTime(), "Time should be 1 after one increment");
    }

    @Test
    public void testIncrement() {
        clock.increment();
        clock.increment();
        assertEquals(2, clock.getTime(), "Time should be 2 after two increments");
    }

    @Test
    public void testUpdateWithLowerTime() {
        clock.increment();
        clock.update(0);
        assertEquals(2, clock.getTime(), "Time should be 2 after updating with lower time");
    }

    @Test
    public void testUpdateWithHigherTime() {
        clock.update(5);
        assertEquals(6, clock.getTime(), "Time should be 6 after updating with higher time");
    }

    @Test
    public void testConcurrentUpdates() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                clock.increment();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                clock.update(i);
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        assertTrue(clock.getTime() >= 1000, "Time should be at least 1000 after concurrent updates");
    }
}