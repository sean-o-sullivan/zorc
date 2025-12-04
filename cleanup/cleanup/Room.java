import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Room extends Inventor implements Jsonable {

    private int id;
    private String description;
    
    // Mapping keys (1-9) to Items.
    private String[] mapLayout; 
    
    // Stores which Room ID a door leads to. Key="N", Value=targetRoomId
    public Map<String, Integer> doorTargets = new HashMap<>(); 

    public Room(String description) {
        this.description = description;
    }

    public void setMap(String[] map) { this.mapLayout = map; }
    public String[] getMap() { return mapLayout; }

    // --- ID MANAGEMENT ---

    // Finds the next free number (1-9) to put an item on the map
    public int getNextFreeSlot() {
        for(int i=1; i<=9; i++) {
            if(!inventory.getMap().containsKey(i)) return i;
        }
        return -1; // Full
    }

    // THE MISSING METHOD: Finds a slot and adds the item
    public int addItemToRoom(Item item) {
        int slot = getNextFreeSlot();
        if (slot != -1) {
            addItemToSlot(slot, item);
        }
        return slot;
    }

    // Wrappers to interact with Storage Map
    public void addItemToSlot(int slot, Item item) { inventory.put(slot, item); }
    public void removeItemFromSlot(int slot) { inventory.remove(slot); }
    public Item getItemFromSlot(int slot) { return inventory.get(slot); }
    
    // ------------------------

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescription() { return description; }

    public String getLongDescription() {
        return "You are " + description;
    }

    
    @Override
    public String toJson() {
        // 1. Save Inventory (Map Based)
        StringBuilder invSb = new StringBuilder();
        invSb.append("[");
        int count = 0;
        for (Map.Entry<Integer, Item> entry : inventory.getMap().entrySet()) {
            String itemJson = entry.getValue().toJson();
            invSb.append(String.format("{\"slot\": %d, \"item\": %s}", entry.getKey(), itemJson));
            if (count < inventory.getMap().size() - 1) invSb.append(", ");
            count++;
        }
        invSb.append("]");

        // 2. Save Map Layout (Essential for persistence!)
        StringBuilder mapSb = new StringBuilder();
        mapSb.append("[");
        if (mapLayout != null) {
            for(int i=0; i<mapLayout.length; i++) {
                mapSb.append("\"").append(mapLayout[i]).append("\"");
                if(i < mapLayout.length-1) mapSb.append(",");
            }
        }
        mapSb.append("]");
        
        // 3. Save Connections (Essential so we don't re-randomize on load)
        StringBuilder doorSb = new StringBuilder();
        doorSb.append("{");
        int dCount = 0;
        for(Map.Entry<String, Integer> e : doorTargets.entrySet()) {
            doorSb.append("\"").append(e.getKey()).append("\": ").append(e.getValue());
            if(dCount < doorTargets.size()-1) doorSb.append(", ");
            dCount++;
        }
        doorSb.append("}");

        return String.format(
            "{\"id\": %d, \"name\": \"Room\", \"description\": \"%s\", \"inventory\": %s, \"map\": %s, \"doors\": %s}", 
            id, description, invSb.toString(), mapSb.toString(), doorSb.toString()
        );
    }

    public Room getExit(String direction){ return null; }

    @Override
    public void fromJson(String json) {
        this.id = Integer.parseInt(JsonParser.getValue(json, "id"));
        this.description = JsonParser.getValue(json, "description");
        
        // Load Inventory
        this.getInventory().getMap().clear();
        String invArray = JsonParser.getArrayContent(json, "inventory");
        if(!invArray.isEmpty()) {
            String[] wrappers = invArray.split("}, \\{"); 
            for(String w : wrappers) {
                if(!w.startsWith("{")) w = "{" + w;
                if(!w.endsWith("}")) w = w + "}";
                int slot = Integer.parseInt(JsonParser.getValue(w, "slot"));
                String itemContent = JsonParser.getObjectContent(w, "item");
                Item item = new Item("","");
                item.fromJson(itemContent);
                this.addItemToSlot(slot, item);
            }
        }

        // Load Map Layout
        String mapArrayStr = JsonParser.getArrayContent(json, "map");
        if (!mapArrayStr.isEmpty()) {
            ArrayList<String> mapLines = new ArrayList<>();
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("\"([^\"]+)\"").matcher(mapArrayStr);
            while (m.find()) mapLines.add(m.group(1));
            this.mapLayout = mapLines.toArray(new String[0]);
        }
        
        // Load Connections
        String doorStr = JsonParser.getObjectContent(json, "doors"); 
        if(!doorStr.isEmpty()) {
            String[] dirs = {"N", "S", "E", "W", "n", "s", "e", "w"};
            for(String d : dirs) {
                String val = JsonParser.getValue(doorStr, d);
                if(!val.isEmpty()) doorTargets.put(d, Integer.parseInt(val));
            }
        }
    }


}