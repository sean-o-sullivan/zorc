import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GameDialogPanel extends JPanel {
    private JTextArea displayArea;
    private JTextField inputField;

public GameDialogPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GREEN)); // Right border line

        // Display Area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);
        displayArea.setBackground(Color.BLACK);
        displayArea.setForeground(Color.GREEN);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        displayArea.setCaretColor(Color.GREEN);
        
        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(null); // Remove default border
        scrollPane.getVerticalScrollBar().setBackground(Color.BLACK);

        // Input Area
        inputField = new JTextField();
        inputField.setBackground(Color.BLACK);
        inputField.setForeground(Color.GREEN);
        inputField.setCaretColor(Color.GREEN);
        inputField.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GREEN));
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 13));

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

    public JTextArea getDisplayArea() { return displayArea; }

    public void addInputListener(ActionListener listener) {
        inputField.addActionListener(listener);
    }
}