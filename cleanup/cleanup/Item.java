import java.io.Serializable;


public class Item implements Serializable{
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
}
