import javax.swing.*;

public class Typewriter {

    // The shared lock object. All threads must ask for this key before typing.
    private static final Object lock = new Object();

    public static void type(JTextArea area, String text) {
        if (area == null) return;

        new Thread(() -> {
            
            synchronized (lock) {
                try {

                    SwingUtilities.invokeLater(() -> {
                        if (area.getText().length() > 0) {
                            area.append("\n");
                        }
                    });

                    for (char c : text.toCharArray()) {
                        SwingUtilities.invokeLater(() -> {
                            area.append(String.valueOf(c));

                            area.setCaretPosition(area.getDocument().getLength()); // autoscroll at bottom
                        });

                        Thread.sleep(30); 
                    }

                    SwingUtilities.invokeLater(() -> area.append("\n"));

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } // release lock
        }).start();
    }
}