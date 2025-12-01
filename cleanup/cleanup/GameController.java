import javax.swing.*;
import java.awt.event.*;
import java.nio.file.*;
import java.util.*;
import java.io.IOException;

public class GameController {

    public static List<String[]> all_maps = new ArrayList<>();
    private static Parser parser = new Parser(); // Static so we can use in main

    public static void main(String[] args) {



        // index number of the item on the map corresponds to the index position of said item in the rooms inventory
        // need item inventory showing
        // need soundeffects
        // need unit tests !?
        // need to integrate the dialogue panel

        // need to make sure that the view keeps refreshing by itself. 

        



        // Load Maps
        try { 
            loadMaps();
        } catch (Exception e) { 
            e.printStackTrace(); 
        }

        // Initialize Model
        GameModel model = new GameModel();
        if (!all_maps.isEmpty()) model.setMap(all_maps.get(0));
        // model.startGame(); // define this in model if needed, otherwise skip

        // Initialize View
        GameView.UIContext ui = GameView.createGameUI(model);




        // Listeners
        // ===============================


        // we could initialise some fancy threads here. 


        // ===============================


    }
        // Key Listeners
        ui.gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_UP:    model.kUp = true; break;
                    case KeyEvent.VK_DOWN:  model.kDown = true; break;
                    case KeyEvent.VK_LEFT:  model.kLeft = true; break;
                    case KeyEvent.VK_RIGHT: model.kRight = true; break;
                    case KeyEvent.VK_SPACE: model.kScan = true; break;
                    case KeyEvent.VK_E:     model.interactWithItem(true); break;
                    case KeyEvent.VK_R:     model.interactWithItem(false); break;
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_UP:    model.kUp = false; break;
                    case KeyEvent.VK_DOWN:  model.kDown = false; break;
                    case KeyEvent.VK_LEFT:  model.kLeft = false; break;
                    case KeyEvent.VK_RIGHT: model.kRight = false; break;
                }
            }
        });

        // Mouse Focus Listener
        ui.gamePanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                ui.gamePanel.requestFocusInWindow();
            }
        });

        // Button Listeners
        ui.btnPickUp.addActionListener(e -> {
            model.interactWithItem(true);
            ui.gamePanel.requestFocusInWindow();
        });

        ui.btnDrop.addActionListener(e -> {
            model.interactWithItem(false);
            ui.gamePanel.requestFocusInWindow();
        });

        // Text Input Listener 
        ui.dialogPanel.addInputListener(e -> {
            // 1. Get text from the new panel
            String text = ui.dialogPanel.getInputText(); 
            ui.dialogPanel.clearInput();
            
            // 2. Process command
            Command cmd = parser.getCommand(text); 
            processCommand(cmd, model); 

            // 3. Echo result to the text area
            ui.dialogPanel.appendText("> " + text); 
            
            ui.gamePanel.requestFocusInWindow();
        });


        // Game loop
        // ===============================

        ui.frame.setVisible(true);
        ui.gamePanel.requestFocusInWindow();

        new javax.swing.Timer(16, e -> {
            
            // Update the characters current position and orientation
            model.updatePhysics();

            // Fire lidar if space is pressed
            if (model.kScan) {
                ui.gamePanel.fireLidar();
            }

            // Wipe dots from your imaginary memory if x is pressed
            if (model.kWipe) {
                ui.gamePanel.wipeMap();
            }

            // Repaint the scene
            ui.gamePanel.repaint();

        }).start();

    }




    // Helpers
    // ===============================

    public static void loadMaps() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("maps.json"));
        List<String> currentMap = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("\"") && !line.contains(":")) {
                currentMap.add(line.replace("\"", "").replace(",", ""));
            } else if (line.startsWith("]")) {
                all_maps.add(currentMap.toArray(new String[0]));
                currentMap.clear();
            }
        }
    }

    // Clean Command Processor using the Static Class we made earlier
    private static void processCommand(Command command, GameModel model) {
        String word = command.getCommandWord();
        if (word == null) return;

        switch (word) {
            case "help":    GameActions.printHelp(parser); break;
            case "go":      GameActions.goRoom(command, model.player); break;
            case "search":  GameActions.search(model.player.getCurrentRoom()); break;
            case "rummage": GameActions.search(model.player); break;
            case "stash":   GameActions.stash(command, model.player); break;
            case "grab":    GameActions.grab(command, model.player); break;
            case "quit":    System.exit(0); break;
            default:        System.out.println("Unknown command."); break;
        }
    }
}