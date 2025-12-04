package sourceFilesPlusAssets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PauseScreen extends JPanel {

    private JTextField saveNameField;
    private JButton btnSave;
    private JButton btnResume;
    private JButton btnQuit;

    public PauseScreen(ActionListener saveAction, ActionListener resumeAction, ActionListener quitAction) {
        setLayout(new GridBagLayout());
        setBackground(new Color(0, 0, 0, 200)); // Semi-transparent black

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lbl = new JLabel("PAUSED");
        lbl.setFont(new Font("Monospaced", Font.BOLD, 30));
        lbl.setForeground(Color.WHITE);
        gbc.gridy = 0;
        add(lbl, gbc);

        // Save Input
        saveNameField = new JTextField("savegame.json");
        gbc.gridy = 1;
        add(saveNameField, gbc);

        btnSave = new JButton("Save Game");
        btnSave.addActionListener(e -> {
            saveAction.actionPerformed(e); 
        });
        gbc.gridy = 2;
        add(btnSave, gbc);

        btnResume = new JButton("Resume");
        btnResume.addActionListener(resumeAction);
        gbc.gridy = 3;
        add(btnResume, gbc);

        btnQuit = new JButton("Quit to Menu");
        btnQuit.addActionListener(quitAction);
        gbc.gridy = 4;
        add(btnQuit, gbc);
    }

    public String getSaveFileName() {
        String txt = saveNameField.getText().trim();
        return txt.endsWith(".json") ? txt : txt + ".json";
    }
}