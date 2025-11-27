import java.util.ArrayList;

public abstract class Inventor {
    
    protected ArrayList<Item> inventory = new ArrayList<Item>(); // ?

    public void addItem(Item item){
        inventory.add(item);
    }

    public void remItem(Item item){
        inventory.remove(item);
    }

    public ArrayList<Item> getInventory() {
        return inventory;
    }

    // Abstract method that must be implemented by subclasses
    // public abstract void (Player player);

}