import javax.swing.*;
import java.awt.*;

public interface GameMain {


    public static void main(String[] args) {
        JFrame frame = new JFrame("MVC Game Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        // --- THE MVP IMPLEMENTATION ---
        
        // 1. Instantiate the View
        GameDialogPanel dialogPanel = new GameDialogPanel();

        GameView.DisplayGame gamePanel = new GameView.DisplayGame(level1);
        
        frame.add(gamePanel);
        frame.add(dialogPanel);
        frame.setSize(400, 400);
        frame.setVisible(true);

        // we need to the frame here. 
// 
        // 2. Instantiate Controller (Passes logic to the view)
        new GameController(dialogPanel);

        // 3. Add to your existing layout (Replacing your old button)
        // Assuming your button was in the CENTER or SOUTH, 
        // usually these dialogs go in the CENTER, EAST, or SOUTH.
        frame.add(dialogPanel, BorderLayout.CENTER);

        // ------------------------------

        frame.setVisible(true);
        
        // Example: The game sending a welcome message
        dialogPanel.appendText("Welcome to the game. Enter a command...");
    }
}

