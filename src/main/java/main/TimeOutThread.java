package main;

public class TimeOutThread extends Thread {

    long timeOut;

    public TimeOutThread(long timeOut) {
        this.timeOut = timeOut;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(timeOut);
        } catch (InterruptedException ignored) {
            return;
        }
        if (!isInterrupted()) {
            System.out.println("Algorithm timed out after " + timeOut + " ms");
            System.exit(-1);
        }
    }
}
