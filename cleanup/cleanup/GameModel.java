import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameModel {

    private Map<String, Room> rooms = new HashMap<>();
    private List<Item> items = new LinkedList<>();
    private List<Character> characters = new LinkedList<>();

    public Character player = new Character("Character", rooms.get("outside"));
    public String[] currentRoomMap;
    public int gameState = 0;

    public boolean kUp, kDown, kLeft, kRight, kScan, kWipe;



    private void createRooms() {
        Room outside = new Room("outside the main entrance of the university");
        Room theatre = new Room("in a lecture theatre");
        Room pub = new Room("in the campus pub");
        Room lab = new Room("in a computing lab");
        Room office = new Room("in the computing admin office");
        Room shop = new Room("in the campus shop");
        Room library = new Room("in the library full of dusty books");
        Room quad = new Room("in the main quad surrounded by tall trees");

        // Connect rooms
        outside.setExit("east", theatre);
        theatre.setExit("west", outside);
        outside.setExit("south", pub);
        pub.setExit("north", outside);
        lab.setExit("west", office);
        office.setExit("east", lab);
        outside.setExit("north", library);
        library.setExit("south", outside);
        library.setExit("east", quad);
        quad.setExit("west", library);

        // Store in map
        rooms.put("outside", outside);
        rooms.put("theatre", theatre);
        rooms.put("pub", pub);
        rooms.put("lab", lab);
        rooms.put("office", office);
        rooms.put("shop", shop);
        rooms.put("library", library);
        rooms.put("quad", quad);

        // Hacky shuffle of the layout
        randomizeExits();
    }

    private void createItems() {
        int id = 100;

        Item usb = new Item("USB-Drive", "A small 16GB USB stick with a mysterious label.");
        usb.setId(id++);
        Item key = new Item("Office-Key", "A small brass key with an engraving 'Admin'.");
        key.setId(id++);
        Item map = new Item("Campus-Map", "A folded paper map showing building locations.");
        map.setId(id++);
        Item mug = new Item("Coffee-Mug", "A ceramic mug with 'World’s Best TA' printed on it.");
        mug.setId(id++);
        Item notebook = new Item("Notebook", "Filled with scribbled formulas and doodles.");
        notebook.setId(id++);
        Item sandwich = new Item("Half-eaten-Sandwich", "Someone didn’t finish their lunch.");
        sandwich.setId(id++);
        Item flashlight = new Item("Flashlight", "Useful for exploring dark rooms.");
        flashlight.setId(id++);
        Item coin = new Item("Old-Coin", "A tarnished coin from an old student fair.");
        coin.setId(id++);

        items.addAll(List.of(usb, key, map, mug, notebook, sandwich, flashlight, coin));

        // Add them to room inventories
        rooms.get("lab").addItem(usb);
        rooms.get("office").addItem(key);
        rooms.get("outside").addItem(map);
        rooms.get("pub").addItem(mug);
        rooms.get("theatre").addItem(notebook);
        rooms.get("pub").addItem(sandwich);
        rooms.get("library").addItem(flashlight);
        rooms.get("quad").addItem(coin);
    }


    private void randomizeExits() {
        // Take all rooms, shuffle them
        List<Room> list = new ArrayList<>(rooms.values());
        Collections.shuffle(list);

        int n = list.size();
        for (int i = 0; i < n; i++) {
            Room a = list.get(i);
            Room b = list.get((i + 1) % n); // neighbor for east/west
            Room c = list.get((i + 2) % n); // neighbor for north/south

            // Overwrite all four directions across the ring so we nuke the old layout
            a.setExit("east", b);
            b.setExit("west", a);

            a.setExit("north", c);
            c.setExit("south", a);
        }
    }


    private void createNPCs() {
        Character librarian = new Character("Librarian", rooms.get("library"));
        Character bartender = new Character("Bartender", rooms.get("pub"));
        Character technician = new Character("Technician", rooms.get("lab"));

        // Give some items to characters`
        librarian.addItem(new Item("Reading Glasses", "Round glasses with smudged lenses."));
        bartender.addItem(new Item("Bar Rag", "Soaked with beer and soda water."));
        technician.addItem(new Item("Screwdriver", "A flat-head tool used for repairs."));

        characters.addAll(List.of(player, librarian, bartender, technician));
    }

    // public Character getNPC() {
    //     return characters.get(0);
    // }



// for counting how long a player took for each level
            // if (gameState==0){
            //     start = System.nanoTime();
            //     gameState=1;
            // }
            // duration = System.nanoTime() - start;



    public void startGame(){

        createRooms();
        createItems();
        createNPCs();

    }


    
        
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


    public void interactWithItem(boolean pickup) {
        int checkX = (int)(player.getPx() + Math.cos(player.getAngle()) * 1.0);
        int checkY = (int)(player.getPy() + Math.sin(player.getAngle()) * 1.0);

        if (checkY < 0 || checkY >= currentRoomMap.length) return;
        if (checkX < 0 || checkX >= currentRoomMap[0].length()) return;

        char targetChar = currentRoomMap[checkY].charAt(checkX);
        boolean isItem = (targetChar >= '1' && targetChar <= '9');
        boolean isEmpty = (targetChar == '.');

        if (pickup && isItem) {
            System.out.println("Picked up item: " + targetChar);
            modifyMap(checkX, checkY, '.');
        } 
        else if (!pickup && isEmpty) {
            System.out.println("Dropped item");
            // Hardcoded dropping '1' for MVP simplicity
            modifyMap(checkX, checkY, '1'); 
        }
    
    }


    private boolean isValidMove(double x, double y) {
        int ix = (int)x;
        int iy = (int)y;

        // Bounds check
        if (iy < 0 || iy >= currentRoomMap.length) return false;
        if (ix < 0 || ix >= currentRoomMap[0].length()) return false;

        char tile = currentRoomMap[iy].charAt(ix);
        
        return tile != '#';
    }

    // Physics Step
    public void updatePhysics() {

        // rotation
        if (kLeft)  player.setAngle(player.getAngle() - 0.07);
        if (kRight) player.setAngle(player.getAngle() + 0.07); 

        double dx = Math.cos(player.getAngle()) * 0.1;
        double dy = Math.sin(player.getAngle()) * 0.1;

        // translation
        if (kUp) { 
            double nextX = player.getPx() + dx;
            double nextY = player.getPy() + dy;
            
            // Only move if valid
            if (isValidMove(nextX, nextY)) {
                player.setPx(nextX);
                player.setPy(nextY);
            }
        }
        
        if (kDown) { 
            double nextX = player.getPx() - dx;
            double nextY = player.getPy() - dy;
            
            // Only move if valid
            if (isValidMove(nextX, nextY)) {
                player.setPx(nextX);
                player.setPy(nextY);
            }
        } 
    }

        
}