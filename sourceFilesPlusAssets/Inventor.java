package sourceFilesPlusAssets;

public abstract class Inventor {

    protected Storage<Item> inventory = new Storage<>();

    public void addItem(Item item){ inventory.add(item); }
    public void remItem(Item item){ inventory.remove(item); }
    public Storage<Item> getInventory() { return inventory; }
}