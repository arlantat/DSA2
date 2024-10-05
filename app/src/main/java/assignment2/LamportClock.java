package assignment2;

public class LamportClock {
    private int time;

    public LamportClock() {
        this.time = 0;
    }

    public synchronized int getTime() {
        return time;
    }

    public synchronized void increment() {
        time++;
    }

    public synchronized void update(int receivedTime) {
        time = Math.max(time, receivedTime) + 1;
    }
}