package sourceFilesPlusAssets;
import java.util.Map;
import java.util.ArrayList;

public class Room extends Inventor implements Jsonable {

    private int id;
    private String name; 
    private String description;
    
    // Mapping keys (1-9) to Items.
    private String[] mapLayout; 

    public Room(String description) {
        this.description = description;
    }


    private String mapSeed = ""; // Stores the global randomization string

    public String getMapSeed() { return mapSeed; }
    public void setMapSeed(String mapSeed) { this.mapSeed = mapSeed; }


    public void setMap(String[] map) { this.mapLayout = map; }
    public String[] getMap() { return mapLayout; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescription() { return description; }

    public String getLongDescription() {
        return "You are " + description;
    }

    // --- ID MANAGEMENT FOR ITEMS ---

    public int getNextFreeSlot() {
        for(int i=1; i<=9; i++) {
            if(!inventory.getMap().containsKey(i)) return i;
        }
        return -1; // Full
    }

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
    
    // JSON SAVING/LOADING 

    @Override
    public String toJson() {
        // 1. Save Inventory
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

        // 2. Save Map Layout (This now includes your I/O characters)
        StringBuilder mapSb = new StringBuilder();
        mapSb.append("[");
        if (mapLayout != null) {
            for(int i=0; i<mapLayout.length; i++) {
                mapSb.append("\"").append(mapLayout[i]).append("\"");
                if(i < mapLayout.length-1) mapSb.append(",");
            }
        }
        mapSb.append("]");
        
        return String.format(
            "{\"id\": %d, \"name\": \"%s\", \"description\": \"%s\", \"inventory\": %s, \"map\": %s, \"seed\": \"%s\"}", 
            id, name, description, invSb.toString(), mapSb.toString(), mapSeed
        );


    }

    @Override
    public void fromJson(String json) {
        this.id = Integer.parseInt(JsonParser.getValue(json, "id"));
        this.name = JsonParser.getValue(json, "name"); 
        this.description = JsonParser.getValue(json, "description");
        this.mapSeed = JsonParser.getValue(json, "seed");

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

        // we no longer loading doors here
    }
}