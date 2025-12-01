import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class Room extends Inventor implements Serializable{
    private String description;
    private String floorPlan;
    private Map<String, Room> exits; // Map direction to neighboring Room

    
    public Room(String description) {
        this.description = description;
        exits = new HashMap<>();
    }

    public String getDescription() {
        return description;
    }

    public void setExit(String direction, Room neighbor) {
        exits.put(direction, neighbor);
    }

    public Room getExit(String direction) {
        return exits.get(direction);
    }

    public String getExitString() {
        StringBuilder sb = new StringBuilder();
        for (String direction : exits.keySet()) {
            sb.append(direction).append(" ");
        }
        return sb.toString().trim();
    }

    public String getLongDescription() {
        return "You are " + description + ".\nExits: " + getExitString();
    }
}
