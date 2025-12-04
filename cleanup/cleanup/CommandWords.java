import java.util.HashMap;
import java.util.Map;

public class CommandWords {
    private Map<String, String> validCommands;

    public CommandWords() {
        validCommands = new HashMap<>();
        validCommands.put("go", "Move to another room");
        validCommands.put("quit", "End the game");
        validCommands.put("help", "Show help");
        validCommands.put("eat", "Eat something");
        validCommands.put("search", "Search the room");
        validCommands.put("rummage", "Search you bag");
        validCommands.put("stash", "Stash an item in the current room");
        validCommands.put("grab", "Grab an item in the current room");
        validCommands.put("scan", "Fire Lidar burst");
        validCommands.put("wipe", "Clear the map");   
    }

    public boolean isCommand(String commandWord) {
        return validCommands.containsKey(commandWord);
    }

    public String returnAll() {
        String cmds ="";
        cmds+="Valid commands are: ";

        for (String command : validCommands.keySet()) {
            cmds+=command+"";
        }
        cmds+="\n";
        return cmds;
    }

}
