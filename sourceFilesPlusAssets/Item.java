

public class Item implements Jsonable{
    private String description;
    private String name;
    private int id;
    private boolean isVisible;

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
        this.isVisible = true;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
    

    @Override
    public String toString() {
        return name + " - " + description;
    }

    @Override
    public String toJson() {
        return String.format(
            "{\"id\": %d, \"name\": \"%s\", \"desc\": \"%s\", \"isVisible\": %b}", 
            id, name, description, isVisible
        );
    }

    @Override
    public void fromJson(String json) {
        this.id = Integer.parseInt(JsonParser.getValue(json, "id"));
        this.name = JsonParser.getValue(json, "name");
        this.description = JsonParser.getValue(json, "desc");
        this.isVisible = Boolean.parseBoolean(JsonParser.getValue(json, "isVisible"));
    }
}


