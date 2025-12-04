import javax.sound.sampled.*;
import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;

public class SoundManager {

    // --- SFX BUFFER THREAD ---
    // Buffer of size 1 as requested
    private static ArrayBlockingQueue<Integer> sfxBuffer = new ArrayBlockingQueue<>(1);
    
    // --- MUSIC THREAD ---
    private static Clip musicClip;
    private static boolean musicRunning = true;

    public static void init() {
        startSfxThread();
        startMusicThread();
    }

    // Call this to queue a sound. 
    // 1 = Pickup, 2 = Drop, 3 = Unlock, 4 = Walk
    public static void triggerSfx(int soundId) {
        // offer returns false if full, ignoring the extra inputs as per "buffer of size 1" logic
        sfxBuffer.offer(soundId); 
    }

    public static void playVictoryMusic() {
        stopMusic(); // Stop current
        playLoopingMusic("victorious.wav"); // Play new
    }

    private static void startSfxThread() {
        new Thread(() -> {
            while (true) {
                try {
                    // This blocks until an integer is available
                    int id = sfxBuffer.take(); 
                    
                    String filename = "";
                    switch(id) {
                        case 1: filename = "pickup.wav"; break;
                        case 2: filename = "drop.wav"; break;
                        case 3: filename = "unlock.wav"; break; // The portal opening sound
                        case 4: filename = "whoosh.wav"; break; // Room transition
                    }
                    
                    if (!filename.isEmpty()) playSoundOnce(filename);
                    
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    private static void startMusicThread() {
        new Thread(() -> {
            playLoopingMusic("background.wav");
        }).start();
    }

    private static void playLoopingMusic(String filepath) {
        try {
            File f = new File(filepath);
            if (!f.exists()) return;
            
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioIn);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
        } catch (Exception e) {
            System.out.println("Music Error: " + e.getMessage());
        }
    }

    public static void stopMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
            musicClip.close();
        }
    }

    // Helper for the SFX thread
    private static void playSoundOnce(String filepath) {
        try {
            File f = new File(filepath);
            if (!f.exists()) return;
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
            // Wait for clip to finish or it gets cut off by GC
            Thread.sleep(clip.getMicrosecondLength() / 1000); 
        } catch (Exception e) { }
    }
}