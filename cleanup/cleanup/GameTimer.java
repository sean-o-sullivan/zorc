// Save as GameTimer.java
public class GameTimer extends Thread {
    private boolean running = true;
    public float seconds = 0;

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(100);
                seconds+=100;
            } catch (InterruptedException e) { }
        }
    }
    public void stopTimer() { running = false; }
}