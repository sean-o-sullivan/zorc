import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameActions {

    public static void printHelp(Parser parser) {
        System.out.println("You are lost. You are so terribly alone. You wander around the university.");
        System.out.print("Your command words are: ");
        parser.showCommands();
    }

    public static void search(Inventor target) { 
        String type = target.getClass().getSimpleName(); 
        if (type.equals("Room")) System.out.println("You search the room…");
        else if (type.equals("Character")) System.out.println("You rummage in your bag…");

        // FIX: Combine items from both List (Player) and Map (Room) into one list for display
        Storage<Item> storage = target.getInventory();
        List<Item> allItems = new ArrayList<>(storage.getList());
        allItems.addAll(storage.getMap().values());

        if (allItems.isEmpty()) {
            System.out.println("  You find only a speck of dust!");
            return;
        }

        System.out.println("  You find:");
        for (Item item : allItems) {
            System.out.println("    " + item.getName() + " — " + item.getDescription());
        }
    }

    public static void grab(Command command, Character player) {
        if (!command.hasSecondWord()) {
            System.out.println("Grab what?");
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
            System.out.println("Alas! You don’t have that item!");
            return;
        }

        player.addItem(toGrab); // Adds to Player's List
        
        // Remove from Room's Map using the slot ID
        player.getCurrentRoom().getInventory().remove(slotId);
        
        // Note: In the text-based version, this leaves a 'ghost' number on the map
        // because we aren't updating the string array here, but it works for inventory logic.
        
        System.out.printf("You grab %s in %s.\n", toGrab.getName(), player.getCurrentRoom().getDescription());
    }


    public static void stash(Command command, Character player) {
        if (!command.hasSecondWord()) {
            System.out.println("Stash what?");
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
            System.out.println("You don’t have that item!");
            return;
        }

        // Add to Room (will assign a new ID 1-9)
        player.getCurrentRoom().addItemToRoom(toStash);
        
        // Remove from Player
        player.remItem(toStash);
        
        System.out.printf("You stash %s in %s.\n", toStash.getName(), player.getCurrentRoom().getDescription());
    }


    public static void goRoom(Command command, Character player) {
        if (!command.hasSecondWord()) {
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();
        // Uses the legacy exits map for text commands
        Room nextRoom = player.getCurrentRoom().getExit(direction);

        if (nextRoom == null) {
            // Fallback: Check the new doorTargets map if the legacy one is empty
            if(player.getCurrentRoom().doorTargets.containsKey(direction)) {
                // This requires access to the full room list to find the room object by ID
                // For now, simpler text commands might fail if not using N/S/E/W specifically
                System.out.println("The door is locked or does not exist.");
            } else {
                System.out.println("There is no door!");
            }
        } else {
            player.setCurrentRoom(nextRoom);
            System.out.println(player.getCurrentRoom().getLongDescription());
        }
    }
}