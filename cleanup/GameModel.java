<<<<<<< HEAD
import java.util.*;


public class GameModel {

    private Map<String, Room> rooms = new HashMap<>();
    private List<Item> items = new ArrayList<>();
    private List<Character> characters = new ArrayList<>();

    private Parser parser = new Parser();
    private Character player;

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
    private void createCharacters() {
        Character playerChar = new Character("Player", rooms.get("outside"));
        Character librarian = new Character("Librarian", rooms.get("library"));
        Character bartender = new Character("Bartender", rooms.get("pub"));
        Character technician = new Character("Technician", rooms.get("lab"));

        // Give some items to characters`
        librarian.addItem(new Item("Reading Glasses", "Round glasses with smudged lenses."));
        bartender.addItem(new Item("Bar Rag", "Soaked with beer and soda water."));
        technician.addItem(new Item("Screwdriver", "A flat-head tool used for repairs."));

        characters.addAll(List.of(playerChar, librarian, bartender, technician));
    }

    public Character getPlayer() {
        return characters.get(0);
    }

    public void play() {
        createRooms();
        createItems();
        createCharacters();
        player = getPlayer();


        printWelcome();

        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing. Goodbye.");

        // broken save functionality
        ArrayList<Object> objecs = new ArrayList<Object>();
        objecs.add(characters);
        objecs.add(rooms);
        objecs.add(items);
        // IOControl.save(objecs);
    }

    
    private void printWelcome() {
        System.out.println();
        System.out.println("Welcome to the University adventure!");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(player.getCurrentRoom().getLongDescription());
    }

    private boolean processCommand(Command command) {
        String commandWord = command.getCommandWord();

        if (commandWord == null) {
            System.out.println("I don't understand your command...");
            return false;
        }

        switch (commandWord) {
            case "help":
                printHelp();
                break;
            case "go":
                goRoom(command);
                break;
            case "search":
                search(player.getCurrentRoom());            
                break;
            case "rummage":
                search(player);
                break;
            case "stash":
                stash(command);    
                break;
            case "grab":
                grab(command);
                break;
            case "quit":
                if (command.hasSecondWord()) {
                    System.out.println("Quit what?");
                    return false;
                } else {
                    return true; // signal to quit
                }
            default:
                System.out.println("I don't know what you mean...");
                break;
        }
        return false;
    }

    private void printHelp() {
        System.out.println("You are lost. You are alone. You wander around the university.");
        System.out.print("Your command words are: ");
        parser.showCommands();
    }

    private void search(Inventor Inventor) { 
        if (Inventor.getClass().getName()=="Room"){
        System.out.println("You search the room…");
        } else if(Inventor.getClass().getName()=="Character"){
        System.out.println("You rummage in your bag…");
        }

        ArrayList<Item> contents = Inventor.getInventory();
        if (contents.isEmpty()) {
            System.out.println("  You find only a speck of dust!");
            return;
        }

        System.out.println("  You find:");
        for (Item item : contents) {
            System.out.println("    " + item.getName() + " — " + item.getDescription());
        }
    }

    
    private void grab(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Grab what?");
            return;
        }

        String itemName = command.getSecondWord();
        Item toGrab = null;

        // Search player inventory for the item
        for (Item item : player.getCurrentRoom().getInventory()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                toGrab = item;
                break;
            }
        }

        if (toGrab == null) {
            System.out.println("You don’t have that item!");
            return;
        }

        player.addItem(toGrab);
        player.getCurrentRoom().remItem(toGrab);
        System.out.printf("You grab %s in %s.\n",
            toGrab.getName(), player.getCurrentRoom().getDescription());
    }

    private void stash(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Stash what?");
            return;
        }

        String itemName = command.getSecondWord();
        Item toStash = null;

        // Search player inventory for the item
        for (Item item : player.getInventory()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                toStash = item;
                break;
            }
        }

        if (toStash == null) {
            System.out.println("You don’t have that item!");
            return;
        }

        player.getCurrentRoom().addItem(toStash);
        player.remItem(toStash);
        System.out.printf("You stash %s in %s.\n",
                toStash.getName(), player.getCurrentRoom().getDescription());
    }

    private void goRoom(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();
        Room nextRoom = player.getCurrentRoom().getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        } else {
            player.setCurrentRoom(nextRoom);
            System.out.println(player.getCurrentRoom().getLongDescription());
        }
    }

    public static void main(String[] args) {
        ZorkULGame game = new ZorkULGame();
        game.play();
    }
=======
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

        // need to make snazzier names
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



            // todo later 
    // private void createNPCs() {
    //     Character librarian = new Character("Librarian", rooms.get("library"));
    //     Character bartender = new Character("Bartender", rooms.get("pub"));
    //     Character technician = new Character("Technician", rooms.get("lab"));

    //     // Give some items to characters`
    //     librarian.addItem(new Item("Reading Glasses", "Round glasses with smudged lenses."));
    //     bartender.addItem(new Item("Bar Rag", "Soaked with beer and soda water."));
    //     technician.addItem(new Item("Screwdriver", "A flat-head tool used for repairs."));

    //     characters.addAll(List.of(player, librarian, bartender, technician));
    // }

    // public Character getNPC() {
    //     return characters.get(0);
    // }




        // speedrun timer
    // for counting how long a player took for each level
                // if (gameState==0){
                //     start = System.nanoTime();
                //     gameState=1;
                // }
                // duration = System.nanoTime() - start;



    public void startGame(){

        createRooms();
        createItems();
        // createNPCs();

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


    // Physics Step
    public void updatePhysics() {

        double angle = player.getAngle();

        if (kLeft)  player.setAngle(angle - 0.07);
        if (kLeft)  player.setAngle(angle + 0.07);

        double dx = Math.cos(angle) * 0.1;
        double dy = Math.sin(angle) * 0.1;

        double px = player.getPx();
        double py = player.getPy();

        // Simple collision logic (checking bounds only for MVP)
        if (kUp) { player.setPx(px + dx);  player.setPy(py + dy);} // we are walking forward
        if (kDown) { player.setPx(px - dx);  player.setPy(py - dy);} // walking backwards
    }

        
>>>>>>> 72134ce (back from the dead)
}