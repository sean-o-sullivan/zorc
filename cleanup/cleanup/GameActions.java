import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameActions {

    private static javax.swing.JTextArea dialogArea;
    private static String todo ="";

    public static void setDialogArea(javax.swing.JTextArea area) {
        dialogArea = area;
    }

    // Helper to redirect output to UI
    private static void println(String text) {
        if (dialogArea != null) Typewriter.type(dialogArea, text);
        else println(text);
    }


    public static void printHelp(Parser parser) {
        println("Your command words are: ");
        todo =parser.showAll();
        println(todo);
    }

    public static void search(Inventor target) { 
        String type = target.getClass().getSimpleName(); 
        if (type.equals("Room")) println("You search the room…  \n You find...");
        else if (type.equals("Character")) println("You rummage in your bag… \n You find...");

        Storage<Item> storage = target.getInventory();
        List<Item> allItems = new ArrayList<>(storage.getList());
        allItems.addAll(storage.getMap().values());

        if (allItems.isEmpty()) {

            println("   only a speck of dust!");
            return;
        } else{

            for (Item i : allItems){
                String desc = "\t"+i.getName()+": "+i.getDescription();
                println(desc);
            }
        }
    }

    public static void grab(Command command, Character player) {
        if (!command.hasSecondWord()) {
            println("Grab what?");
            return;
        }

        String itemName = command.getSecondWord();
        Item toGrab = null;
        int slotId = -1;

        // FIX: Room inventory is a Map<Integer, Item>. Iterate values to find name.
        Map<Integer, Item> roomItems = player.getCurrentRoom().getInventory().getMap();
        
        for (Map.Entry<Integer, Item> entry : roomItems.entrySet()) {
            if (entry.getValue().getName().equalsIgnoreCase(itemName)) {
                toGrab = entry.getValue();
                slotId = entry.getKey(); // We need the ID to remove it properly
                break;
            }
        }

        if (toGrab == null) {
            println("Alas! You don’t have that item!");
            return;
        }

        player.addItem(toGrab); // Adds to Player's List
        
        // Remove from Room's Map using the slot ID
        player.getCurrentRoom().getInventory().remove(slotId);
        
        // Note: In the text-based version, this leaves a 'ghost' number on the map
        // because we aren't updating the string array here, but it works for inventory logic.
        
        System.out.printf("You grab %s in %s.\n", toGrab.getName(), player.getCurrentRoom().getDescription());
    }


    public static void scan(GameModel model) {
        println("Initiating single Lidar sweep...");
        model.kScan = true;
        
        // Turn off after 2 seconds
        new javax.swing.Timer(2000, e -> {
             model.kScan = false;
             ((javax.swing.Timer)e.getSource()).stop();
             println("Scan complete.");
        }).start();
    }

    public static void wipe(GameModel model) {
        println("Purging visual cache...");
        model.kWipe = true;
        
        // Turn off quickly (just enough to clear the array)
        new javax.swing.Timer(100, e -> {
             model.kWipe = false;
             ((javax.swing.Timer)e.getSource()).stop();
        }).start();
    }


    public static void stash(Command command, Character player) {
        if (!command.hasSecondWord()) {
            println("Stash what?");
            return;
        }

        String itemName = command.getSecondWord();
        Item toStash = null;

        // FIX: Player inventory is a List.
        for (Item item : player.getInventory().getList()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                toStash = item;
                break;
            }
        }

        if (toStash == null) {
            println("You don’t have that item!");
            return;
        }

        // Add to Room (will assign a new ID 1-9)
        player.getCurrentRoom().addItemToRoom(toStash);
        
        // Remove from Player
        player.remItem(toStash);
        
        System.out.printf("You stash %s in %s.\n", toStash.getName(), player.getCurrentRoom().getDescription());
    }

    public static void handleMove(Command command, GameModel model) {
        if (!command.hasSecondWord()) {
            println("Go where? (forward, back, left, right)");
            return;
        }

        String dir = command.getSecondWord().toLowerCase();

        // Map common synonyms
        if (dir.equals("north") || dir.equals("n")) dir = "forward";
        if (dir.equals("south") || dir.equals("s")) dir = "back";
        if (dir.equals("west") || dir.equals("w"))  dir = "left";
        if (dir.equals("east") || dir.equals("e"))  dir = "right";

        // Validate
        if (dir.equals("forward") || dir.equals("back") || dir.equals("left") || dir.equals("right")) {
            model.textCommandMove(dir);
        } else {
            println("I don't know how to go '" + dir + "'. Try forward, back, left, or right.");
        }
    }


}