import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.awt.event.ActionListener;

public class StartScreen extends JPanel {

    private JButton btnNewGame;
    private JButton btnLoadGame;
    private JButton btnExit;

    public StartScreen(ActionListener newGameAction, ActionListener loadGameAction) {
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("LIDAR ZORK");
        title.setFont(new Font("Monospaced", Font.BOLD, 40));
        title.setForeground(Color.GREEN);
        gbc.gridy = 0;
        add(title, gbc);

        // Buttons
        btnNewGame = createStyledButton("New Game");
        btnNewGame.addActionListener(newGameAction);
        gbc.gridy = 1;
        add(btnNewGame, gbc);

        btnLoadGame = createStyledButton("Load Game");
        btnLoadGame.addActionListener(loadGameAction);
        gbc.gridy = 2;
        add(btnLoadGame, gbc);

        btnExit = createStyledButton("Exit");
        btnExit.addActionListener(e -> System.exit(0));
        gbc.gridy = 3;
        add(btnExit, gbc);
    }

    // Helper to list JSON files in a Dropdown
    public static String showLoadDialog(Component parent) {
        File dir = new File(".");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json") && !name.equals("maps.json"));
        
        if (files == null || files.length == 0) {
            JOptionPane.showMessageDialog(parent, "No save files found!");
            return null;
        }

        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getName();
        }

        String selected = (String) JOptionPane.showInputDialog(
            parent, 
            "Select a save file:", 
            "Load Game", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            fileNames, 
            fileNames[0]
        );

        return selected;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Monospaced", Font.PLAIN, 20));
        btn.setFocusPainted(false);
        return btn;
    }
}