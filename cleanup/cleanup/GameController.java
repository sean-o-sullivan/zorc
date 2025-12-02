import javax.swing.*;
import java.awt.event.*;
import java.nio.file.*;
import java.util.*;
import java.io.IOException;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;



public class GameController {

    public static List<String[]> all_maps = new ArrayList<>();

    private static Parser parser = new Parser(); // Static so we can use in main
        
    private static GameView.UIContext ui;
    private static GameModel model;
    private static boolean isPaused = true; // Start paused/at menu

    private static int selectedIndex = 0;

        // index number of the item on the map corresponds to the index position of said item in the rooms inventory
        // need item inventory showing
        // need soundeffects
        // need unit tests !?
        // need to integrate the dialogue panel

        // need to make sure that the view keeps refreshing by itself. 

        // we need saving functionality, to json.                            


        // Load Maps

        // i would rather more demonstration of my skills than unnecessarily adding npcs. 
            // there be no need for npcs. lets try jazz this stuff up with sounds.
    

            // custom cursor icon?


//             // LAN MULTIPLAYER, how long it takes you to complete the levels, thats shared, and perhaps synchronisation.

//             lock.wait()

//             private static final Object lock = new Object()`

//             // ve could play the sonuds on a different thread when they need to be played 
// // !!!!!!
// // you have to own the lock in orfer 
// // do we want a shop
// eeeeeee 


// we need an internal crafting setup that is polymorphic. 


// lock.notifyAll();  -> need to make things like these. 

/// use a do while!!!!!

            // when you progress to the next level you choose what to make from the items you have, like what upgrade?
            



    public static void main(String[] args) {
        try { loadMaps(); } catch (Exception e) { e.printStackTrace(); }

        model = new GameModel();
        
        // --- DEFINE ACTIONS ---
        

        ActionListener actionNewGame = e -> {

            model.startGame(); 
            
            if (!all_maps.isEmpty()) {
                model.setMap(all_maps.get(0));
            }
                        
            isPaused = false;
            ui.cardLayout.show(ui.mainContainer, "GAME");
            ui.gamePanel.requestFocusInWindow();
        };

        

        ActionListener actionLoadGame = e -> {
            String filename = StartScreen.showLoadDialog(ui.frame);
            if (filename != null) {
                // 1. Initialize the base world first so objects exist
                model.startGame(); 
                
                // 2. Load the save data on top of it
                loadGame(model, filename); 
                
                // 3. Update the visual map based on the loaded room ID
                int currentRoomId = model.player.getCurrentRoom().getId();
                if (currentRoomId < all_maps.size()) {
                    model.setMap(all_maps.get(currentRoomId));
                }

                isPaused = false;
                ui.cardLayout.show(ui.mainContainer, "GAME");
                ui.gamePanel.requestFocusInWindow();
            }
        };


        ActionListener actionSaveGame = e -> {
            String filename = ui.pauseScreen.getSaveFileName();
            saveGame(model, filename); // Update your saveGame to take filename arg
            // Optional: Auto resume after save?
            // isPaused = false;
            // ui.cardLayout.show(ui.mainContainer, "GAME");
            // ui.gamePanel.requestFocusInWindow();
        };

        ActionListener actionResume = e -> {
            isPaused = false;
            ui.cardLayout.show(ui.mainContainer, "GAME");
            ui.gamePanel.requestFocusInWindow();
        };

        ActionListener actionQuitToMenu = e -> {
            isPaused = true;
            ui.cardLayout.show(ui.mainContainer, "MENU");
        };

        // Initialize UI with actions
        ui = GameView.createGameUI(model, actionNewGame, actionLoadGame, actionSaveGame, actionResume, actionQuitToMenu);

        // Listeners
        // ===============================


        // we could initialise some fancy threads here. 


        // ===============================



        // Key Listeners
        ui.gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    isPaused = true;
                    ui.cardLayout.show(ui.mainContainer, "PAUSE");
                }

                switch(e.getKeyCode()) {
                    case KeyEvent.VK_UP:    model.kUp = true; break;
                    case KeyEvent.VK_DOWN:  model.kDown = true; break;
                    case KeyEvent.VK_LEFT:  model.kLeft = true; break;
                    case KeyEvent.VK_RIGHT: model.kRight = true; break;
                    case KeyEvent.VK_SPACE: model.kScan = true; break;
                    case KeyEvent.VK_E:   model.interactWithItem(true, 0);  ui.inventoryPanel.updateInventory(model); ui.inventoryPanel.updateDropdown(model, ui.itemDropdown); break;
                    case KeyEvent.VK_R:     model.interactWithItem(false, 0);  ui.inventoryPanel.updateInventory(model); break;
                    case KeyEvent.VK_Q:     model.kWipe = true; break;
                }
                System.out.println(model.kScan);
            }

            @Override
            public void keyReleased(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    isPaused = false;
                    ui.cardLayout.show(ui.mainContainer, "GAME");
                }

                switch(e.getKeyCode()) {
                    case KeyEvent.VK_UP:    model.kUp = false; break;
                    case KeyEvent.VK_DOWN:  model.kDown = false; break;
                    case KeyEvent.VK_LEFT:  model.kLeft = false; break;
                    case KeyEvent.VK_RIGHT: model.kRight = false; break;
                    case KeyEvent.VK_Q:     model.kWipe = false; break;
                    case KeyEvent.VK_SPACE: model.kScan = false; break;


                }
            }
        });

        // Mouse Focus Listener
        ui.gamePanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                ui.gamePanel.requestFocusInWindow();
            }
        });

        ui.btnPickUp.addActionListener(e -> {
            model.interactWithItem(true, 0);
            ui.inventoryPanel.updateInventory(model);
            ui.inventoryPanel.updateDropdown(model, ui.itemDropdown);
            ui.gamePanel.requestFocusInWindow();
        });

        ui.btnDrop.addActionListener(e -> {
            selectedIndex = ui.itemDropdown.getSelectedIndex();
            if (selectedIndex >= 0) {
                model.interactWithItem(false, selectedIndex); // Modify this method
            }
            ui.inventoryPanel.updateInventory(model);
            ui.inventoryPanel.updateDropdown(model, ui.itemDropdown);
            ui.gamePanel.requestFocusInWindow();
        });


        // Text Input Listener 
        ui.dialogPanel.addInputListener(e -> {
            // Get text from the new panel
            String text = ui.dialogPanel.getInputText(); 
            ui.dialogPanel.clearInput();
            
            // Process command
            Command cmd = parser.getCommand(text); 
            processCommand(cmd, model); 

            // Echo result to the text area
            ui.dialogPanel.appendText("> " + text); 
            
            ui.gamePanel.requestFocusInWindow();
        });


        // Game loop
        // ===============================

        ui.frame.setVisible(true);

        // Show menu by default
        ui.cardLayout.show(ui.mainContainer, "MENU");
        ui.gamePanel.requestFocusInWindow();  //????

        new javax.swing.Timer(16, e -> {
                if (!isPaused) {
                    model.updatePhysics();
                    if (model.kScan) ui.gamePanel.fireLidar();
                    if (model.kWipe) ui.gamePanel.wipeMap();
                    ui.gamePanel.repaint();
                }
            }).start();
        }
        



    // Helpers
    // ===============================

public static void loadMaps() throws IOException {
    String content = new String(Files.readAllBytes(Paths.get("game-info.json")));
    
    // Get the ROOMS array
    String roomsArrayStr = JsonParser.getArrayContent(content, "ROOMS");
    
    if (roomsArrayStr.isEmpty()) {
        System.err.println("ERROR: Could not find ROOMS in game-info.json");
        return;
    }
    
    // Parse each room object to extract its map
    int braceCount = 0;
    boolean inString = false;
    StringBuilder currentBlock = new StringBuilder();
    
    for (int i = 0; i < roomsArrayStr.length(); i++) {
        char c = roomsArrayStr.charAt(i);
        
        if (c == '\\' && i + 1 < roomsArrayStr.length()) {
            currentBlock.append(c);
            i++;
            currentBlock.append(roomsArrayStr.charAt(i));
            continue;
        }
        
        if (c == '"') {
            inString = !inString;
        }
        
        if (!inString) {
            if (c == '{') {
                if (braceCount == 0) {
                    currentBlock = new StringBuilder();
                }
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0 && currentBlock.length() > 0) {
                    currentBlock.append(c);
                    String roomBlock = currentBlock.toString().trim();
                    
                    // Extract the map array from this room
                    String mapArrayStr = JsonParser.getArrayContent(roomBlock, "map");
                    if (!mapArrayStr.isEmpty()) {
                        List<String> mapLines = new ArrayList<>();
                        java.util.regex.Pattern mapLinePattern = java.util.regex.Pattern.compile("\"([^\"]+)\"");
                        java.util.regex.Matcher mapMatcher = mapLinePattern.matcher(mapArrayStr);
                        
                        while (mapMatcher.find()) {
                            mapLines.add(mapMatcher.group(1));
                        }
                        
                        if (!mapLines.isEmpty()) {
                            all_maps.add(mapLines.toArray(new String[0]));
                            System.out.println("DEBUG: Loaded map " + (all_maps.size() - 1) + " with " + mapLines.size() + " lines");
                        }
                    }
                    
                    currentBlock = new StringBuilder();
                    continue;
                }
            }
        }
        
        if (braceCount > 0) {
            currentBlock.append(c);
        }
    }
    
    System.out.println("DEBUG: Total maps loaded: " + all_maps.size());
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


// Updated to accept filename and iterate over Map values correctly
public static void saveGame(GameModel model, String filename) {
    try (FileWriter writer = new FileWriter(filename)) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        
        // 1. Save Player
        sb.append("  \"player\": ").append(model.player.toJson()).append(",\n");
        
        // 2. Save Rooms
        sb.append("  \"rooms\": [\n");
        
        // FIX: Iterate over Map values, not by index
        int count = 0;
        int totalRooms = model.getRooms().size();
        
        for (Room r : model.getRooms().values()) {
            sb.append("    ").append(r.toJson());
            
            // Append comma if not the last room
            if (count < totalRooms - 1) {
                sb.append(",");
            }
            sb.append("\n");
            count++;
        }
        
        sb.append("  ]\n");
        sb.append("}");
        
        writer.write(sb.toString());
        System.out.println("Game Saved Successfully to " + filename);
        
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Failed to save game.");
    }
}

// Updated to accept String filename parameter
public static void loadGame(GameModel model, String filename) {
    try {
        // FIX: Use the filename passed in, not hardcoded "savegame.json"
        String content = new String(Files.readAllBytes(Paths.get(filename)));
        
        // 1. Load Player
        String playerJson = JsonParser.getObjectContent(content, "player"); // Ensure you use JsonParser (or JsonParser if that's what you named it)
        model.player.fromJson(playerJson);
        
        // Re-link player to actual room object
        int savedRoomId = model.player.getSavedRoomId(playerJson);
        
        // Iterate over map values to find the room
        for(Room r : model.getRooms().values()) {
            if(r.getId() == savedRoomId) {
                model.player.setCurrentRoom(r);
                break;
            }
        }

        // 2. Load Rooms
        String roomsArray = JsonParser.getArrayContent(content, "rooms");
        if (!roomsArray.isEmpty()) {
            String[] roomStrings = roomsArray.split("\\}, \\s*\\{");

            for (String rStr : roomStrings) {
                String cleanStr = rStr.trim();
                if (!cleanStr.startsWith("{")) cleanStr = "{" + cleanStr;
                if (!cleanStr.endsWith("}")) cleanStr = cleanStr + "}";
                
                int rId = Integer.parseInt(JsonParser.getValue(cleanStr, "id"));
                
                for (Room r : model.getRooms().values()) {
                    if (r.getId() == rId) {
                        r.fromJson(cleanStr); 
                        break;
                    }
                }
            }
        }
        
        System.out.println("Game Loaded Successfully from " + filename);
        
    } catch (IOException e) {
        System.out.println("No save file found: " + filename);
    }
}


}




