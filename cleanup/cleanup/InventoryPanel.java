import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


public class InventoryPanel extends JPanel {
    private JTextArea displayArea;
    private JTextArea heading;
    private JTextField inputField;

    Font timesNewRomanFont = new Font("Times New Roman", Font.BOLD, 14);
    Font timesNewRomanFont2 = new Font("Times New Roman", Font.ITALIC, 14);
  
    Font helveticaCool = new Font("Helvetica", Font.PLAIN, 13);


    public InventoryPanel(GameModel givenModel) {
        setLayout(new BorderLayout());

        // Display Area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(displayArea);


        // Heading
        heading = new JTextArea();
        heading.setEditable(false);
        heading.setLineWrap(true);
        heading.setWrapStyleWord(true);
        heading.setFont(helveticaCool);
        heading.append("Backpack");
        heading.setAlignmentX(SwingConstants.CENTER); // Centers the text

        add(heading, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateInventory(GameModel model) {
        displayArea.setText("");  // Clear first
        for (Item item : model.player.getInventory()){
            
            displayArea.setFont(timesNewRomanFont);
            String text = item.getName() + ": " + item.getDescription();
            appendText(text);
            appendText("_______");

        }
    }

    public void updateDropdown(GameModel model, JComboBox<String> dropdown) {
        dropdown.removeAllItems();
        for (Item item : model.player.getInventory()) {
            dropdown.addItem(item.getName());
        }
    }

    public void appendText(String text) {
        displayArea.append(text + "\n");
        // This line handles the auto-scrolling
        displayArea.setCaretPosition(displayArea.getDocument().getLength());
    }

    public void clearInput() {
        inputField.setText("");
    }

    public void addInputListener(ActionListener listener) {
        inputField.addActionListener(listener);
    }


}