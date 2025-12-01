import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GameDialogPanel extends JPanel {
    private JTextArea displayArea;
    private JTextField inputField;

    public GameDialogPanel() {
        setLayout(new BorderLayout());

        // Display Area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);
        
        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Input Area
        inputField = new JTextField();

        add(scrollPane, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);
    }

    public void appendText(String text) {
        displayArea.append(text + "\n");
        // This line handles the auto-scrolling
        displayArea.setCaretPosition(displayArea.getDocument().getLength());
    }

    public String getInputText() {
        return inputField.getText();
    }

    public void clearInput() {
        inputField.setText("");
    }

    public void addInputListener(ActionListener listener) {
        inputField.addActionListener(listener);
    }
}