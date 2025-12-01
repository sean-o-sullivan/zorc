import java.util.ArrayList;

public class GameActions {

    public static void printHelp(Parser parser) {
        System.out.println("You are lost. You are so terribly alone. You wander around the university.");
        System.out.print("Your command words are: ");
        parser.showCommands();
    }

    public static void search(Inventor target) { 
        // using simple name check as per your snippet
        String type = target.getClass().getSimpleName(); 
        if (type.equals("Room")) System.out.println("You search the room…");
        else if (type.equals("Character")) System.out.println("You rummage in your bag…");

        ArrayList<Item> contents = target.getInventory();
        if (contents.isEmpty()) {
            System.out.println("  You find only a speck of dust!");
            return;
        }

        System.out.println("  You find:");
        for (Item item : contents) {
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

        for (Item item : player.getCurrentRoom().getInventory()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                toGrab = item;
                break;
            }
        }

        if (toGrab == null) {
            System.out.println("Alas! You don’t have that item!");
            return;
        }

        player.addItem(toGrab);
        player.getCurrentRoom().remItem(toGrab);
        System.out.printf("You grab %s in %s.\n", toGrab.getName(), player.getCurrentRoom().getDescription());
    }


    public static void stash(Command command, Character player) {
        if (!command.hasSecondWord()) {
            System.out.println("Stash what?");
            return;
        }

        String itemName = command.getSecondWord();
        Item toStash = null;

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
        System.out.printf("You stash %s in %s.\n", toStash.getName(), player.getCurrentRoom().getDescription());
    }


    public static void goRoom(Command command, Character player) {
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

}