import java.util.HashMap;
import java.util.Map;

public class Room extends Inventor implements Jsonable{
   
    private int id;
    private String description;
    private Map<String, Room> exits; // Map direction to neighboring Room

    public Room(String description) {
        this.description = description;
        exits = new HashMap<>();
    }

    public String getLongDescription() {
        return "You are " + description + ".\nExits: " + getExitString();
    }

    public String getDescription() {
        return description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }



    

    // these arent used in the visualisation even though they kind of are in the thing
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




    @Override
    public String toJson() {
        StringBuilder invSb = new StringBuilder();
        invSb.append("[");
        for (int i = 0; i < inventory.size(); i++) {
            invSb.append(inventory.get(i).toJson());
            if (i < inventory.size() - 1) invSb.append(", ");
        }
        invSb.append("]");

        // Return Object
        return String.format("{\"id\": %d, \"inventory\": %s}", id, invSb.toString());
    }



    @Override
    public void fromJson(String json) {

        this.getInventory().clear();
        
        String invArray = JsonParser.getArrayContent(json, "inventory");
        
        if (!invArray.isEmpty()) {
            String[] itemStrings = invArray.split("}, \\{");
            
            for (String itemStr : itemStrings) {
                if (!itemStr.startsWith("{")) itemStr = "{" + itemStr;
                if (!itemStr.endsWith("}")) itemStr = itemStr + "}";
                
                Item newItem = new Item("", "");
                newItem.fromJson(itemStr);
                this.addItem(newItem);
            }
        }
    }

    

}
