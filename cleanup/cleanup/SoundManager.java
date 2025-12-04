import javax.sound.sampled.*;
import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;

public class SoundManager {

    
    private static final String ASSET_PATH = "assets" + File.separator;
    
    // Define your music files here for easy changing
    private static final String MUSIC_MENU = "menu_theme.wav";
    private static final String MUSIC_GAME = "main_theme.wav";
    private static final String MUSIC_VICTORY = "game_end.wav";

    private static ArrayBlockingQueue<Integer> sfxBuffer = new ArrayBlockingQueue<>(1);
    private static Clip musicClip;

    public static void init() {
        startSfxThread();
        playMenuMusic(); // Start immediately with Menu music
    }

    // --- MUSIC SWITCHING METHODS ---

    public static void playMenuMusic() {
        changeMusic(MUSIC_MENU);
    }

    public static void playGameMusic() {
        changeMusic(MUSIC_GAME);
    }

    public static void playVictoryMusic() {
        changeMusic(MUSIC_VICTORY);
    }

    // Helper to stop old music and start new music safely
    private static void changeMusic(String filename) {
        // Run in a new thread so it doesn't freeze the UI while loading
        new Thread(() -> {
            stopMusic(); // Ensure previous track is stopped
            playLoopingMusic(filename);
        }).start();
    }

    public static void triggerSfx(int soundId) {
        sfxBuffer.offer(soundId); 
    }

    private static void startSfxThread() {
        new Thread(() -> {
            while (true) {
                try {
                    int id = sfxBuffer.take(); 
                    String filename = "";
                    switch(id) {
                        case 0: filename = "teleport.wav"; break;           
                        case 1: filename = "pickup.wav"; break;                 
                        case 2: filename = "drop.wav"; break;
                        case 3: filename = "unlock.wav"; break; 
                        case 4: filename = "whoosh.wav"; break; 
                        case 5: filename = "startGame.wav"; break;    
                    }
                    
                    if (!filename.isEmpty()) playSoundOnce(filename);
                    
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    private static void playLoopingMusic(String filename) {
        try {
            File f = new File(ASSET_PATH + filename);
            if (!f.exists()) {
                System.err.println("MUSIC MISSING: " + f.getAbsolutePath());
                return;
            }
            
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioIn);
            
            FloatControl gainControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-12.0f); // Volume down
            
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
            musicClip.close();
        }
    }

    private static void playSoundOnce(String filename) {
        try {
            File f = new File(ASSET_PATH + filename);
            if (!f.exists()) {
                System.err.println("SFX MISSING: " + f.getAbsolutePath());
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
            Thread.sleep(clip.getMicrosecondLength() / 1000); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}