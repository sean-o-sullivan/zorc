package sourceFilesPlusAssets;

import java.util.List;

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


    @Override
    public String toJson() {


        int roomId = (currentRoom != null) ? currentRoom.getId() : 0;

        // Build Inventory 
        StringBuilder invSb = new StringBuilder();
        invSb.append("[");
        
        // Access the internal list via getList()
        List<Item> items = inventory.getList();
        
        for (int i = 0; i < items.size(); i++) {
            invSb.append(items.get(i).toJson());
            if (i < items.size() - 1) invSb.append(", ");
        }
        invSb.append("]");

        // Return Full JSON Object
        return String.format(
            "{\"name\": \"%s\", \"px\": %.2f, \"py\": %.2f, \"angle\": %.2f, \"currentRoomId\": %d, \"inventory\": %s}", 
            name, px, py, angle, roomId, invSb.toString()
        );
    }

    @Override
    public void fromJson(String json) {
        this.name = JsonParser.getValue(json, "name");
        
        String pxStr = JsonParser.getValue(json, "px");
        String pyStr = JsonParser.getValue(json, "py");
        String angleStr = JsonParser.getValue(json, "angle");
        
        if(!pxStr.isEmpty()) this.px = Double.parseDouble(pxStr);
        if(!pyStr.isEmpty()) this.py = Double.parseDouble(pyStr);
        if(!angleStr.isEmpty()) this.angle = Double.parseDouble(angleStr);
        
        // Note: Room linking happens in Controller via this ID
        this.getInventory().getList().clear(); 
        
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