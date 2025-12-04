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
        SoundManager.init();
        
        try {
            loadWorldFromJson("game-info.json");
            
            // Remove the randomizer. We trust the JSON.
            // randomizeDoors(); 
            
            // CLEAN UP THE MAP: Turn visual 'N' into '#' if no logical connection exists
            pruneInvalidDoors(); 
            
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

    // NEW METHOD: Ensures the visual map matches the logical connections
    private void pruneInvalidDoors() {
        for (Room r : rooms.values()) {
            String[] map = r.getMap();
            if (map == null) continue;
            
            boolean roomModified = false;
            for (int y = 0; y < map.length; y++) {
                char[] row = map[y].toCharArray();
                boolean rowModified = false;
                
                for (int x = 0; x < row.length; x++) {
                    char c = row[x];
                    // If visual map has a door...
                    if ("NSEW".indexOf(c) != -1) {
                        // ...but logic says no door exists...
                        if (!r.doorTargets.containsKey(String.valueOf(c))) {
                            row[x] = '#'; // Brick it up
                            rowModified = true;
                        }
                    }
                }
                
                if (rowModified) {
                    map[y] = new String(row);
                    roomModified = true;
                }
            }
            if (roomModified) r.setMap(map);
        }
    }

    
    // Naive implementation: Randomly links doors 
    private void randomizeDoors() {
        List<Integer> ids = new ArrayList<>();
        for(String s : rooms.keySet()) ids.add(Integer.parseInt(s));
        
        for(Room r : rooms.values()) {
            Collections.shuffle(ids);
            // Assign N, S, E, W to random IDs
            r.doorTargets.put("N", ids.get(0));
            r.doorTargets.put("S", ids.get(1));
            r.doorTargets.put("E", ids.get(2));
            r.doorTargets.put("W", ids.get(3));
            
            // Also map the lowercase versions so when we unlock them, the link remains
            r.doorTargets.put("n", ids.get(0));
            r.doorTargets.put("s", ids.get(1));
            r.doorTargets.put("e", ids.get(2));
            r.doorTargets.put("w", ids.get(3));
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

    // --- REWRITTEN INTERACTION LOGIC ---
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
        
        // 3. UNLOCK DOOR (N, S, E, W)
        else if (pickup && "NSEW".indexOf(targetChar) != -1) {
            // Check essential manna (3 items)
            if (player.getInventory().getList().size() >= 3) {
                // Change N -> n
                // FIX: Explicitly call java.lang.Character
                char openChar = java.lang.Character.toLowerCase(targetChar);
                modifyMap(cx, cy, openChar);
                
                SoundManager.triggerSfx(3); // Unlock Sound
                if (dialogArea != null) Typewriter.type(dialogArea, "Gate Unlocked! The path is clear.");
                
            } else {
                SoundManager.triggerSfx(2); // Error/Drop sound used as deny
                if (dialogArea != null) Typewriter.type(dialogArea, "Locked. You need 3 tokens to open this.");
            }
        }
    }

    private boolean isValidMove(double x, double y) {
        int ix = (int)x;
        int iy = (int)y;
        if (iy < 0 || iy >= currentRoomMap.length) return false;
        if (ix < 0 || ix >= currentRoomMap[0].length()) return false;
        char tile = currentRoomMap[iy].charAt(ix);
        return tile != '#';
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
                if (tile != '#' && "NSEW".indexOf(tile) == -1) {
                    
                    // Check for Portal Entry (Lowercase n, s, e, w)
                    if ("nsew".indexOf(tile) != -1) {
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
    
    // Moves player to the next room
    private void transitionRoom(String dir) {
        Room current = player.getCurrentRoom();
        if(current.doorTargets.containsKey(dir)) {
            int nextId = current.doorTargets.get(dir);
            Room nextRoom = rooms.get(String.valueOf(nextId));
            
            if(nextRoom != null) {
                player.setCurrentRoom(nextRoom);
                // CRITICAL: Update the map pointer to the new room's map
                this.currentRoomMap = nextRoom.getMap();
                
                // Reset Position to center to avoid getting stuck in wall/door
                player.setPx(5.0); 
                player.setPy(5.0);
                
                SoundManager.triggerSfx(4); // Whoosh
                
                announceRoom();
 
                // Win Condition Check (Example: Room 6 is exit)
                if(nextId == 6) {
                    SoundManager.playVictoryMusic();
                    if (dialogArea != null) Typewriter.type(dialogArea, "VICTORY! You have escaped.");
                    gameState = 1; // End state
                }
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



}