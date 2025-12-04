import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Generic indeed
public class Storage<T extends Jsonable> {
    // For the player (Linear list)
    private ArrayList<T> list = new ArrayList<>();
    // For the Room (Mapped by ID)
    private Map<Integer, T> map = new HashMap<>();

    // arraylist operations, for player
    public void add(T item) { list.add(item); }
    public void remove(T item) { list.remove(item); }
    public ArrayList<T> getList() { return list; }

    // Map specific methods, for rooms
    public void put(int id, T item) { map.put(id, item); }
    public T get(int id) { return map.get(id); }
    public void remove(int id) { map.remove(id); }
    public Map<Integer, T> getMap() { return map; }
    
    public int getNextFreeId() { // this is for the room id management
        for (int i = 1; i <= 99; i++) { // we will not hit 99
            if (!map.containsKey(i)) return i;
        }
        return -1; // Full
    }
}