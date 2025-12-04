// Save as GameTimer.java
public class GameTimer extends Thread {
    private boolean running = true;
    public double seconds = 0;

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(100);
                seconds+=0.1;
            } catch (InterruptedException e) { }
        }
    }
    public void stopTimer() { running = false; }
}