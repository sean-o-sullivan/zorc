import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.JTextArea; 

public class GameModel {

    private Map<String, Room> rooms = new HashMap<>();

    public String currentSeed = "SEED: N/A";

    public Character player; 
    public String[] currentRoomMap; // This is just a pointer to the Room's map
    public int gameState = 0;

    public boolean kUp, kDown, kLeft, kRight, kScan, kWipe;
    
    // Typewriter target
    private JTextArea dialogArea; 
    
    // Thread for time
    public GameTimer timer;

    public Map<String, Room> getRooms() { return rooms; }
    
    public void setDialogArea(JTextArea area) { this.dialogArea = area; }

public void startGame() {
    
        SoundManager.playGameMusic();
        
        try {
            loadWorldFromJson("game-info.json");
            
            // Remove the randomizer. We trust the JSON.
            generateLinearPath(); 
                        
            // Start Timer Thread
            timer = new GameTimer(); 
            timer.start();
            SoundManager.triggerSfx(5);

            if (dialogArea != null) {
                Typewriter.type(dialogArea, "System Booting ..... \n\nINITIALIZING LIDAR... [OK] \n LOADING MAP DATA... ░░░░░░ 20%\n ... ▓▓▓▒▒░ 45%\nERROR: SECTOR 7 CORRUPTED >> 0xFA82 // ｱｲｳｴｵ\nRETRYING... ⣾⣽⣻⢿⡿⣟\nCONNECTION ESTABLISHED.\n\nLidar guidance system Online.\n\n Hello there! \n I am your lidar guidance system.... \n\nor L for short!\n\n We seem to have woken up in a liminal space... \n\n ... ... ...\n\n The space does have shape! \n\n We can map it! \n\s\s\s >Press SPACE to fire dots. \n\n And it seems that we can look around... \n\s\s\s Press ARROW_KEYS to navigate\n\n I sense there are in a series of interconnected rooms! \n\n Find 3 tokens to open portals. [ Ξ ] ... The portals appear dark on our lidar\n\n If the strain gets too large we can forget the dots \n\s\s\s >Press Q to forget dots\n\n And remember - \n\t move quickly!");
            
                try { 
                        // This ensures the thread timing is consistent
                        Thread.sleep(1);   
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }     

                Typewriter.type(dialogArea, " \n\n\n\n\n\n\n"); 
            }
            
            
            announceRoom();

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadWorldFromJson(String filename) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filename)));
        
        // 1. Parse ROOMS Array
        String roomsArrayStr = JsonParser.getArrayContent(content, "ROOMS");
        
        if (roomsArrayStr.isEmpty()) {
            System.err.println("ERROR: Could not find ROOMS array in JSON");
            return;
        }
        
        List<String> roomBlocks = new ArrayList<>();
        int braceCount = 0;
        int bracketCount = 0;
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
                        roomBlocks.add(currentBlock.toString().trim());
                        currentBlock = new StringBuilder();
                        continue;
                    }
                } else if (c == '[') {
                    bracketCount++;
                } else if (c == ']') {
                    bracketCount--;
                }
            }
            
            if (braceCount > 0) {
                currentBlock.append(c);
            }
        }
        
        // 2. Process each Room Block
        for (String cleanBlock : roomBlocks) {
            String idStr = JsonParser.getValue(cleanBlock, "id");
            if (idStr.isEmpty()) continue;
            


            int id = Integer.parseInt(idStr);
            String desc = JsonParser.getValue(cleanBlock, "description");
            String name = JsonParser.getValue(cleanBlock, "name"); 


            Room newRoom = new Room(desc);
            newRoom.setId(id);
            newRoom.setName(name);
            rooms.put(String.valueOf(id), newRoom);


            String mapArrayStr = JsonParser.getArrayContent(cleanBlock, "map");
            if (!mapArrayStr.isEmpty()) {
                List<String> mapLines = new ArrayList<>();
                java.util.regex.Matcher mapMatcher = Pattern.compile("\"([^\"]+)\"").matcher(mapArrayStr);
                while (mapMatcher.find()) {
                    mapLines.add(mapMatcher.group(1));
                }
                if (!mapLines.isEmpty()) {
                    newRoom.setMap(mapLines.toArray(new String[0]));
                }
            } 

            String itemsArrayStr = JsonParser.getArrayContent(cleanBlock, "items");
            if (!itemsArrayStr.isEmpty()) {
                List<String> itemBlocks = new ArrayList<>();
                int iBraceCount = 0;
                boolean iInString = false;
                StringBuilder iBlock = new StringBuilder();
                
                for (int i = 0; i < itemsArrayStr.length(); i++) {
                    char c = itemsArrayStr.charAt(i);
                    if (c == '\\' && i + 1 < itemsArrayStr.length()) {
                        iBlock.append(c).append(itemsArrayStr.charAt(++i));
                        continue;
                    }
                    if (c == '"') iInString = !iInString;
                    if (!iInString) {
                        if (c == '{') {
                            if (iBraceCount == 0) iBlock = new StringBuilder();
                            iBraceCount++;
                        } else if (c == '}') iBraceCount--;
                    }
                    iBlock.append(c);
                    if (!iInString && iBraceCount == 0 && iBlock.length() > 0) {
                        String block = iBlock.toString().trim();
                        if (block.startsWith("{") && block.endsWith("}")) itemBlocks.add(block);
                        iBlock = new StringBuilder();
                    }
                }
                
                for (String itemBlock : itemBlocks) {
                    String iName = JsonParser.getValue(itemBlock, "name");
                    String iDesc = JsonParser.getValue(itemBlock, "desc");
                    Item newItem = new Item(iName, iDesc);
                    newRoom.addItemToRoom(newItem);
                }
            }
        }
        
        // 3. Create Player and Link Pointers
        Room startRoom = rooms.get("0");
        if (startRoom != null) {
            player = new Character("Character", startRoom);
            
            String playerJson = JsonParser.getObjectContent(content, "PLAYER");
            if (!playerJson.isEmpty()) {
                String xStr = JsonParser.getValue(playerJson, "x");
                String yStr = JsonParser.getValue(playerJson, "y");
                String angleStr = JsonParser.getValue(playerJson, "angle");
                
                if (!xStr.isEmpty()) player.setPx(Double.parseDouble(xStr));
                if (!yStr.isEmpty()) player.setPy(Double.parseDouble(yStr));
                if (!angleStr.isEmpty()) player.setAngle(Double.parseDouble(angleStr));
            }
            
            this.currentRoomMap = startRoom.getMap();
            System.out.println("DEBUG: Player created at (" + player.getPx() + ", " + player.getPy() + ")");
        } else {
            System.err.println("ERROR: Could not find starting room (id=0)");
        }
    }

    private void modifyMap(int x, int y, char newChar) {
        char[] row = currentRoomMap[y].toCharArray();
        row[x] = newChar;
        currentRoomMap[y] = new String(row);
    }

    public void setMap(String[] map) {
        this.currentRoomMap = map;
        if(player != null && player.getCurrentRoom() != null) {
            player.getCurrentRoom().setMap(map);
        }
    }

    public String[] getCurrentMap() { return currentRoomMap; }

    public int countItems() {
        int count = 0;
        if(currentRoomMap == null) return 0;
        for(String s : currentRoomMap) {
            for(char c : s.toCharArray()) if((c>='1'&&c<='9')) count++;
        }
        return count;
    }


    public void interactWithItem(boolean pickup, int targetIndex) {
        int cx = (int)(player.getPx() + Math.cos(player.getAngle()));
        int cy = (int)(player.getPy() + Math.sin(player.getAngle()));

        if (cy < 0 || cy >= currentRoomMap.length) return;
        if (cx < 0 || cx >= currentRoomMap[0].length()) return;

        char targetChar = currentRoomMap[cy].charAt(cx);
        Room r = player.getCurrentRoom();

        // FIX: Explicitly call java.lang.Character to avoid name collision
        if (pickup && java.lang.Character.isDigit(targetChar) && targetChar != '0') {
            int slot = targetChar - '0';
            Item item = r.getItemFromSlot(slot);
            
            if (item != null) {
                player.addItem(item);
                r.removeItemFromSlot(slot);
                modifyMap(cx, cy, '.'); // Remove from map
                
                SoundManager.triggerSfx(1); // Pickup Sound
                if (dialogArea != null) Typewriter.type(dialogArea, "Picked up: " + item.getName());
            }
        } 
        
        // 2. DROP ITEM (.)
        else if (!pickup && targetChar == '.') {
            if(targetIndex < 0 || targetIndex >= player.getInventory().getList().size()) return;
            
            Item toDrop = player.getInventory().getList().get(targetIndex);
            int newSlot = r.getNextFreeSlot();
            
            if(newSlot != -1) {
                r.addItemToSlot(newSlot, toDrop);
                player.remItem(toDrop);
                
                char newChar = (char)(newSlot + '0');
                modifyMap(cx, cy, newChar);
                
                SoundManager.triggerSfx(2); // Drop Sound
                if (dialogArea != null) Typewriter.type(dialogArea, "Dropped: " + toDrop.getName());
            } else {
                if (dialogArea != null) Typewriter.type(dialogArea, "No space here!");
            }
        }
        
        // 3. UNLOCK DOOR (i, o)
        else if (pickup && "io".indexOf(targetChar) != -1) {
            // Check essential manna (3 items)
            if (player.getInventory().getList().size() >= 3) {
                
                // PAY THE TOLL: Remove 3 items
                // We remove the first 3 items in the list
                for(int i=0; i<3; i++) {
                    if (!player.getInventory().getList().isEmpty()) {
                        player.getInventory().getList().remove(0);
                    }
                }

                // Change lower -> Upper (i->I, o->O)
                char openChar = java.lang.Character.toUpperCase(targetChar);
                modifyMap(cx, cy, openChar);
                
                SoundManager.triggerSfx(3); // Unlock Sound
                if (dialogArea != null) Typewriter.type(dialogArea, "Tokens consumed. The portal destabilizes and opens!");
                
            } else {
                SoundManager.triggerSfx(2); // Error sound
                if (dialogArea != null) Typewriter.type(dialogArea, "Locked. You need 3 tokens to stabilise this portal.");
            }
        }
    }


    // --- REWRITTEN PHYSICS & ROOM TRANSITION ---
    public void updatePhysics() {
        if (kLeft)  player.setAngle(player.getAngle() - 0.07);
        if (kRight) player.setAngle(player.getAngle() + 0.07); 

        double dx = Math.cos(player.getAngle()) * 0.1;
        double dy = Math.sin(player.getAngle()) * 0.1;
        
        double nextX = player.getPx();
        double nextY = player.getPy();

        if (kUp) { 
            nextX += dx; nextY += dy;
        } else if (kDown) {
            nextX -= dx; nextY -= dy;
        }

        if (kUp || kDown) {
            int ix = (int)nextX;
            int iy = (int)nextY;

            // Boundary Check
            if (iy >= 0 && iy < currentRoomMap.length && ix >= 0 && ix < currentRoomMap[0].length()) {
               
               
               
                char tile = currentRoomMap[iy].charAt(ix);

                // Standard Collision
                if (tile != '#' && tile != 'i' && tile != 'o') {
                    
                        // Check for OPEN Portals (I, O)
                        if (tile == 'I' || tile == 'O') {
                            transitionRoom(String.valueOf(tile));
                        } else {
                            // Just walking
                            player.setPx(nextX);
                            player.setPy(nextY);
                        }
                

                }
            }
        }
    }
    
// REWRITE the method to handle I/O logic
    private void transitionRoom(String type) {
        Room current = player.getCurrentRoom();
        int currentId = current.getId();
        int nextId = -1;

        if (type.equals("O")) {
            nextId = currentId + 1; // Go Forward
        } else if (type.equals("I")) {
            nextId = currentId - 1; // Go Back
        }

        // Safety check
        Room nextRoom = rooms.get(String.valueOf(nextId));
        
        if (nextRoom != null) {
            player.setCurrentRoom(nextRoom);
            this.currentRoomMap = nextRoom.getMap(); // Update visual pointer
            
            // Reset Position
            player.setPx(5.0); 
            player.setPy(5.0);
            
            SoundManager.triggerSfx(4); // Whoosh
            announceRoom();

            // Win Condition (Room 6 is the last room index)
            if (nextId == 6 && type.equals("O")) { 
                // Wait, if we walk INTO room 6 via 'O' from room 5, that's fine.
                // But usually you win by exiting the final room.
                // Let's say reaching Room 6 is the "Final Level" and exiting it ends game.
                // For now, let's keep it simple: If you enter Room 6, you win? 
                // Or maybe Room 6 has an exit 'O' that leads to id 7 (which doesn't exist)?
            }
            // Better Win Condition:
            if (nextId >= rooms.size()) {
                SoundManager.playVictoryMusic();
                if (dialogArea != null) Typewriter.type(dialogArea, "VICTORY! You have escaped.");
                gameState = 1;
            }
        }
    }


    private void announceRoom() {
        new Thread(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            if (dialogArea != null && player.getCurrentRoom() != null) {
                String text = "Current Room (" + player.getCurrentRoom().getName() + ")\n" + 
                              player.getCurrentRoom().getDescription();
                Typewriter.type(dialogArea, text);
            }
        }).start();
    }


    public void textCommandMove(String direction) {
        // Multipliers to simulate "3 button presses" worth of action
        double moveStep = 0.1 * 3.0; 
        double rotStep = 0.07 * 3.0;

        if (direction.equals("left")) {
            player.setAngle(player.getAngle() - rotStep);
            if (dialogArea != null) Typewriter.type(dialogArea, "You turn to the left.");
            return;
        }
        
        if (direction.equals("right")) {
            player.setAngle(player.getAngle() + rotStep);
            if (dialogArea != null) Typewriter.type(dialogArea, "You turn to the right.");
            return;
        }

        // Handle Forward/Back
        double dx = Math.cos(player.getAngle()) * moveStep;
        double dy = Math.sin(player.getAngle()) * moveStep;

        double nextX = player.getPx();
        double nextY = player.getPy();

        if (direction.equals("forward")) {
            nextX += dx; nextY += dy;
        } else if (direction.equals("back")) {
            nextX -= dx; nextY -= dy;
        }

        int ix = (int)nextX;
        int iy = (int)nextY;

        // Boundary Check
        if (iy >= 0 && iy < currentRoomMap.length && ix >= 0 && ix < currentRoomMap[0].length()) {
            char tile = currentRoomMap[iy].charAt(ix);

            if (tile == '#' || tile == 'i' || tile == 'o') {
                if (dialogArea != null) Typewriter.type(dialogArea, "Blocked. The path is closed.");
                SoundManager.triggerSfx(2); 
            } 
            // 2. Allow Open Portals
            else if (tile == 'I' || tile == 'O') {
                transitionRoom(String.valueOf(tile));
            }
            // 3. Allow Walking (Floor, Items, Legacy NSEW)
            else {
                player.setPx(nextX);
                player.setPy(nextY);
                if (dialogArea != null) Typewriter.type(dialogArea, "You step " + direction + ".");
            }

        }
    }


    // 0=N, 1=E, 2=S, 3=W
    private int getOpposite(int dir) { return (dir + 2) % 4; }

    private int getDirFromChar(char c) {
        switch(java.lang.Character.toUpperCase(c)) {
            case 'N': return 0;
            case 'E': return 1;
            case 'S': return 2;
            case 'W': return 3;
            default: return -1;
        }
    }

    
    private void generateLinearPath() {
        java.util.Random rand = new java.util.Random();
        StringBuilder globalSeed = new StringBuilder();
        int incomingDir = -1; // -1 means no entry (Start of game)

        // Iterate through rooms 0 to 6
        int maxRooms = rooms.size();

        for (int i = 0; i < maxRooms; i++) {
            Room room = rooms.get(String.valueOf(i));
            if (room == null) continue;

            String[] map = room.getMap();
            
            // 1. Find all available doors in the JSON map
            List<Integer> availableExits = new ArrayList<>();
            for(String row : map) {
                if(row.contains("N")) if(!availableExits.contains(0)) availableExits.add(0);
                if(row.contains("E")) if(!availableExits.contains(1)) availableExits.add(1);
                if(row.contains("S")) if(!availableExits.contains(2)) availableExits.add(2);
                if(row.contains("W")) if(!availableExits.contains(3)) availableExits.add(3);
            }

            // 2. Determine Entry (Must match previous room's exit)
            int entryDir = -1;
            if (incomingDir != -1) {
                entryDir = getOpposite(incomingDir);
                // The entry MUST be preserved, so remove it from the list of possible *Exits*
                availableExits.remove((Integer)entryDir); 
            }

            // 3. Pick an Exit (Randomly)
            int exitDir = -1;
            if (i < maxRooms - 1) { // If not the final room
                if (!availableExits.isEmpty()) {
                    exitDir = availableExits.get(rand.nextInt(availableExits.size()));
                    globalSeed.append(exitDir); // Add to seed string
                } else {
                    System.err.println("Map Generation Error: Dead End in Room " + i);
                }
            } else {
                globalSeed.append("X"); // End of game
            }

            // 4. Modify the Map Array (The "Baking" process)
            for (int y = 0; y < map.length; y++) {
                char[] row = map[y].toCharArray();
                for (int x = 0; x < row.length; x++) {
                    char c = java.lang.Character.toUpperCase(row[x]);
                    if ("NSEW".indexOf(c) != -1) {
                        int currentDir = getDirFromChar(c);
                        
                        if (currentDir == entryDir) {
                            row[x] = 'I'; // IN (Backwards)
                        } else if (currentDir == exitDir) {
                            row[x] = 'o'; // OUT (Forwards)
                        } else {
                            row[x] = '.'; // Remove unused door
                        }
                    }
                }
                map[y] = new String(row);
            }
            
            room.setMap(map);
            incomingDir = exitDir; // Set up for next room
        }

        // 5. Save the seed to every room so the HUD can always find it
        String finalSeed = "SEED: " + globalSeed.toString();
        for(Room r : rooms.values()) {
            r.setMapSeed(finalSeed);
        }
    }


}