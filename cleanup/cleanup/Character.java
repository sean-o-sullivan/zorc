public class Character extends Inventor implements Jsonable {
    private String name;
    private Room currentRoom;
    private double px = 2.5, py = 2.5;
    private double angle = 0.0;

    public Character(String name, Room startingRoom) {
        this.name = name;
        this.currentRoom = startingRoom;
    }


    public double getPx() {
        return px;
    }

    public void setPx(double px) {
        this.px = px;
    }

    public double getPy() {
        return py;
    }

    public void setPy(double py) {
        this.py = py;
    }

    public double getAngle() {
        return angle;
    }
    
    public void setAngle(double angle) {
        this.angle = angle;
    }

    public String getName() {
        return name;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public void move(String direction) {
        Room nextRoom = currentRoom.getExit(direction);
        if (nextRoom != null) {
            currentRoom = nextRoom;
            System.out.println("You moved to: " + currentRoom.getDescription());
        } else {
            System.out.println("You can't go that way!");
        }
    }


    @Override
    public String toJson() {
        // 1. Get Room ID (0 if null)
        int roomId = (currentRoom != null) ? currentRoom.getId() : 0;

        // 2. Build Inventory JSON Array
        StringBuilder invSb = new StringBuilder();
        invSb.append("[");
        for (int i = 0; i < inventory.size(); i++) {
            invSb.append(inventory.get(i).toJson());
            if (i < inventory.size() - 1) invSb.append(", ");
        }
        invSb.append("]");

        // 3. Return Full JSON Object
        return String.format(
            "{\"name\": \"%s\", \"px\": %.2f, \"py\": %.2f, \"angle\": %.2f, \"currentRoomId\": %d, \"inventory\": %s}", 
            name, px, py, angle, roomId, invSb.toString()
        );
    }

    @Override
    public void fromJson(String json) {
        this.name = JsonParser.getValue(json, "name");
        this.px = Double.parseDouble(JsonParser.getValue(json, "px"));
        this.py = Double.parseDouble(JsonParser.getValue(json, "py"));
        this.angle = Double.parseDouble(JsonParser.getValue(json, "angle"));
        
        // Note: Room linking happens in Controller via this ID
        // We can store it temporarily or access it via a helper method in Controller
        
        // Load Inventory
        this.getInventory().clear(); // Clear existing items first!
        String invArray = JsonParser.getArrayContent(json, "inventory");
        
        if (!invArray.isEmpty()) {
            // Split objects by "}, {"
            String[] itemStrings = invArray.split("}, \\{");
            
            for (String itemStr : itemStrings) {
                // Fix braces lost during split
                if (!itemStr.startsWith("{")) itemStr = "{" + itemStr;
                if (!itemStr.endsWith("}")) itemStr = itemStr + "}";
                
                Item newItem = new Item("", ""); // Dummy item
                newItem.fromJson(itemStr);       // Populate item
                this.addItem(newItem);           // Add to Inventor list
            }
        }
    }

    // Helper for the Controller to grab the ID during load
    public int getSavedRoomId(String json) {
        String idStr = JsonParser.getValue(json, "currentRoomId");
        return idStr.isEmpty() ? 0 : Integer.parseInt(idStr);
    }

    
}
