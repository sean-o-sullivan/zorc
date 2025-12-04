import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class InventoryPanel extends JPanel {
    private JTextArea displayArea;
    private JTextArea heading;
    private JTextField inputField;

    Font timesNewRomanFont = new Font("Times New Roman", Font.BOLD, 14);
    Font helveticaCool = new Font("Helvetica", Font.PLAIN, 13);

    public InventoryPanel(GameModel givenModel) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GREEN)); // Left border line

        // Display Area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);
        displayArea.setBackground(Color.BLACK);
        displayArea.setForeground(Color.GREEN);
        
        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(null);

        // Heading
        heading = new JTextArea();
        heading.setEditable(false);
        heading.setLineWrap(true);
        heading.setWrapStyleWord(true);
        heading.setFont(new Font("Monospaced", Font.BOLD, 16));
        heading.setBackground(Color.BLACK);
        heading.setForeground(Color.GREEN);
        heading.append("Backpack");
        heading.setAlignmentX(SwingConstants.CENTER); 
        heading.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        add(heading, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateInventory(GameModel model) {
        displayArea.setText(""); 
        
        // FIX: Added .getList() to iterate correctly
        for (Item item : model.player.getInventory().getList()){
            displayArea.setFont(timesNewRomanFont);
            String text = item.getName() + ": " + item.getDescription();
            appendText(text);
            appendText("_______");
        }
    }

    public void updateDropdown(GameModel model, JComboBox<String> dropdown) {
        dropdown.removeAllItems();
        // FIX: Added .getList() here too
        for (Item item : model.player.getInventory().getList()) {
            dropdown.addItem(item.getName());
        }
    }

    public void appendText(String text) {
        displayArea.append(text + "\n");
        displayArea.setCaretPosition(displayArea.getDocument().getLength());
    }

    public void clearInput() {
        if(inputField != null) inputField.setText("");
    }

    public void addInputListener(ActionListener listener) {
        if(inputField != null) inputField.addActionListener(listener);
    }
}