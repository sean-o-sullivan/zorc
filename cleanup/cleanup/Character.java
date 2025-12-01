public class Character extends Inventor{
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
}
