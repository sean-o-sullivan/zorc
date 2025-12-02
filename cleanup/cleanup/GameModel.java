// for counting how long a player took for each level
            // if (gameState==0){
            //     start = System.nanoTime();
            //     gameState=1;
            // }
            // duration = System.nanoTime() - start;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GameModel {

    private Map<String, Room> rooms = new HashMap<>();

    // Player starts null, initialized in startGame
    public Character player; 
    public String[] currentRoomMap;
    public int gameState = 0;

    public boolean kUp, kDown, kLeft, kRight, kScan, kWipe;

    public Map<String, Room> getRooms() { return rooms; }

    public void startGame() {
        // Load everything from the JSON source
        try {
            loadWorldFromJson("game-info.json");
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback if load fails
            Room fallback = new Room("Emergency Bunker");
            fallback.setId(0);
            rooms.put("fallback", fallback);
            player = new Character("Survivor", fallback);
        }
    }

private void loadWorldFromJson(String filename) throws IOException {
    String content = new String(Files.readAllBytes(Paths.get(filename)));
    
    // 1. Parse Rooms Array
    String roomsArrayStr = JsonParser.getArrayContent(content, "ROOMS");
    
    if (roomsArrayStr.isEmpty()) {
        System.err.println("ERROR: Could not find ROOMS array in JSON");
        return;
    }
    
    // Better splitting: handle nested objects and arrays properly
    List<String> roomBlocks = new ArrayList<>();
    int braceCount = 0;
    int bracketCount = 0;
    boolean inString = false;
    StringBuilder currentBlock = new StringBuilder();
    
    for (int i = 0; i < roomsArrayStr.length(); i++) {
        char c = roomsArrayStr.charAt(i);
        
        // Handle escape sequences
        if (c == '\\' && i + 1 < roomsArrayStr.length()) {
            currentBlock.append(c);
            i++;
            currentBlock.append(roomsArrayStr.charAt(i));
            continue;
        }
        
        // Track if we're inside a string
        if (c == '"') {
            inString = !inString;
        }
        
        // Only count braces/brackets outside strings
        if (!inString) {
            if (c == '{') {
                if (braceCount == 0) {
                    // Starting a new top-level object
                    currentBlock = new StringBuilder();
                }
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0 && currentBlock.length() > 0) {
                    // Completed a top-level object
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
    
    System.out.println("DEBUG: Found " + roomBlocks.size() + " room blocks");

    for (int idx = 0; idx < roomBlocks.size(); idx++) {
        String cleanBlock = roomBlocks.get(idx);
        
        System.out.println("DEBUG: Processing room block " + idx);
        
        // Extract Room Details with safety checks
        String idStr = JsonParser.getValue(cleanBlock, "id");
        if (idStr.isEmpty()) {
            System.err.println("ERROR: Could not extract 'id' from room block " + idx);
            System.err.println("Block preview: " + cleanBlock.substring(0, Math.min(200, cleanBlock.length())));
            continue;
        }
        
        int id = Integer.parseInt(idStr);
        String name = JsonParser.getValue(cleanBlock, "name");
        String desc = JsonParser.getValue(cleanBlock, "description");
        
        System.out.println("DEBUG: Room " + id + ": " + name);

        // Create Room
        Room newRoom = new Room(desc);
        newRoom.setId(id);
        rooms.put(String.valueOf(id), newRoom);
        
        // Extract and store the map for this room
        String mapArrayStr = JsonParser.getArrayContent(cleanBlock, "map");
        if (!mapArrayStr.isEmpty()) {
            // Split map lines - they're quoted strings separated by commas
            List<String> mapLines = new ArrayList<>();
            Pattern mapLinePattern = Pattern.compile("\"([^\"]+)\"");
            java.util.regex.Matcher mapMatcher = mapLinePattern.matcher(mapArrayStr);
            
            while (mapMatcher.find()) {
                mapLines.add(mapMatcher.group(1));
            }
            
            if (!mapLines.isEmpty()) {
                String[] mapArray = mapLines.toArray(new String[0]);
                // Store the map somehow - we need to associate it with the room
                // For now, if this is room 0, set it as current map
                if (id == 0) {
                    currentRoomMap = mapArray;
                    System.out.println("DEBUG: Loaded map for room 0, size: " + mapArray.length + " x " + mapArray[0].length());
                }
            }
        } 
        // Extract Items for this Room
        String itemsArrayStr = JsonParser.getArrayContent(cleanBlock, "items");
        if (!itemsArrayStr.isEmpty()) {
            List<String> itemBlocks = new ArrayList<>();
            int iBraceCount = 0;
            boolean iInString = false;
            StringBuilder iBlock = new StringBuilder();
            
            for (int i = 0; i < itemsArrayStr.length(); i++) {
                char c = itemsArrayStr.charAt(i);
                
                if (c == '\\' && i + 1 < itemsArrayStr.length()) {
                    iBlock.append(c);
                    i++;
                    iBlock.append(itemsArrayStr.charAt(i));
                    continue;
                }
                
                if (c == '"') iInString = !iInString;
                
                if (!iInString) {
                    if (c == '{') {
                        if (iBraceCount == 0) iBlock = new StringBuilder();
                        iBraceCount++;
                    } else if (c == '}') {
                        iBraceCount--;
                    }
                }
                
                iBlock.append(c);
                
                if (!iInString && iBraceCount == 0 && iBlock.length() > 0) {
                    String block = iBlock.toString().trim();
                    if (block.startsWith("{") && block.endsWith("}")) {
                        itemBlocks.add(block);
                    }
                    iBlock = new StringBuilder();
                }
            }
            
            for (String itemBlock : itemBlocks) {
                String iName = JsonParser.getValue(itemBlock, "name");
                String iDesc = JsonParser.getValue(itemBlock, "desc");
                Item newItem = new Item(iName, iDesc);
                newRoom.addItem(newItem);
            }
        }
    }
    
    System.out.println("DEBUG: Loaded " + rooms.size() + " rooms total");
    
    // Create the player in the starting room (room 0)
    Room startRoom = rooms.get("0");
    if (startRoom != null) {
        player = new Character("Character", startRoom);
        
        // Set initial position from PLAYER section in JSON
        String playerJson = JsonParser.getObjectContent(content, "PLAYER");
        if (!playerJson.isEmpty()) {
            String xStr = JsonParser.getValue(playerJson, "x");
            String yStr = JsonParser.getValue(playerJson, "y");
            String angleStr = JsonParser.getValue(playerJson, "angle");
            
            if (!xStr.isEmpty()) player.setPx(Double.parseDouble(xStr));
            if (!yStr.isEmpty()) player.setPy(Double.parseDouble(yStr));
            if (!angleStr.isEmpty()) player.setAngle(Double.parseDouble(angleStr));
        }
        
        System.out.println("DEBUG: Player created at (" + player.getPx() + ", " + player.getPy() + ")");
    } else {
        System.err.println("ERROR: Could not find starting room (id=0)");
    }
}


// for counting how long a player took for each level
            // if (gameState==0){
            //     start = System.nanoTime();
            //     gameState=1;
            // }
            // duration = System.nanoTime() - start;



    
        
    private void modifyMap(int x, int y, char newChar) {
        char[] row = currentRoomMap[y].toCharArray();
        row[x] = newChar;
        currentRoomMap[y] = new String(row);
    }

    public void setMap(String[] map) {
        this.currentRoomMap = map;
    }

    public String[] getCurrentMap() {
        return currentRoomMap;
    }

    public int countItems() {
        int count = 0;
        for(String s : currentRoomMap) {
            for(char c : s.toCharArray()) if((c>='1'&&c<='9')) count++;
        }
        return count;
    }


    public void interactWithItem(boolean pickup, int targetIndex) {
        int checkX = (int)(player.getPx() + Math.cos(player.getAngle()) * 1.0);
        int checkY = (int)(player.getPy() + Math.sin(player.getAngle()) * 1.0);

        if (checkY < 0 || checkY >= currentRoomMap.length) return;
        if (checkX < 0 || checkX >= currentRoomMap[0].length()) return;

        char targetChar = currentRoomMap[checkY].charAt(checkX);
        boolean isItem = (targetChar >= '1' && targetChar <= '9');
        boolean isEmpty = (targetChar == '.');

        ArrayList<Item> currentRoomInventory = player.getCurrentRoom().getInventory();
        ArrayList<Item> playerInventory = player.getInventory();

        if (pickup && isItem) {
            System.out.println("Picked up item: " + targetChar);
            
            // we need to query the current room, find what item has index position 2 in its inventory and then remove it from its inventory and add it to the players.
            // we need to get the current room we are in based on its name.   // how are we keeping track of the current room
            // can we get what room the player is currently in? yes.

            for (Item item : currentRoomInventory){   // to validate that a room actually has a populated inventory 
                System.out.printf(" %s",item.toString());
            }
            System.out.println();
            
            
            if (targetIndex >= 0 && targetIndex < currentRoomInventory.size()) {

                System.out.println("Item found at index " + targetIndex + ": " + currentRoomInventory.get(targetIndex));
                player.addItem(currentRoomInventory.get(targetIndex));
                player.getCurrentRoom().remItem(currentRoomInventory.get(targetIndex));
                modifyMap(checkX, checkY, '.');

            } else {
                System.out.println("Index " + targetIndex + " is out of bounds.");
            }
        

        }  else if (!pickup && isEmpty) {

            // if I want to put down an item I need a way of specifying what item name I want to put down. So I need to select the item I want to drop from my inventory, and that passes its name. 
            System.out.println("Dropped item");

            player.getCurrentRoom().addItem(playerInventory.get(targetIndex));
            player.remItem(playerInventory.get(targetIndex));

            char dropSpot = (char) ((currentRoomInventory.size() - 1) + '0');  // this converts the int to a Character and then to a char

            modifyMap(checkX, checkY, dropSpot);

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

    public void updatePhysics() {
        if (kLeft)  player.setAngle(player.getAngle() - 0.07);
        if (kRight) player.setAngle(player.getAngle() + 0.07); 

        double dx = Math.cos(player.getAngle()) * 0.1;
        double dy = Math.sin(player.getAngle()) * 0.1;

        if (kUp) { 
            double nextX = player.getPx() + dx;
            double nextY = player.getPy() + dy;
            if (isValidMove(nextX, nextY)) {
                player.setPx(nextX);
                player.setPy(nextY);
            }
        }
        
        if (kDown) { 
            double nextX = player.getPx() - dx;
            double nextY = player.getPy() - dy;
            if (isValidMove(nextX, nextY)) {
                player.setPx(nextX);
                player.setPy(nextY);
            }
        } 
    }

}