# Lidar Zork: The Procedural Labyrinth

**Lidar Zork** is a hybrid text-adventure and 2D raycasting exploration game built using the **Model-View-Controller (MVC)** architecture. You play as a lost entity navigating a pitch-black liminal space using only a Lidar scanner and text commands.

### 🏃 How to Run

**Prerequisites:** Java Runtime Environment (JRE) 8 or higher.

To play the game, run the JAR file from your terminal/command line:

```bash
java -jar ZorkLidar.jar
```

**⚠️ CRITICAL NOTE:**
The game relies on external files to load the levels and assets. Ensure the following files are in the **same folder** as the `.jar`:
1.  `ZorkLidar.jar`
2.  `game-info.json` (Level data)
3.  `/assets` folder (Contains sounds and images)

If you separate these files, the game will crash or fail to load assets.

---

### 🎮 Controls & Gameplay

**Hybrid Interface:**
You can interact with the world using **three different methods**. Use whichever style you prefer!

| Action | ⌨️ Keybinds | 🖱️ UI Buttons | 💬 Text Terminal |
| :--- | :--- | :--- | :--- |
| **Move** | **Arrow Keys** | Click On-Screen Arrows | `go forward` / `back` / `left` / `right` |
| **Pick Up** | **E** | Click **Grab (E)** | `grab [item name]` |
| **Drop Item** | **R** | Click **Drop (R)** | `stash [item name]` |
| **Lidar Scan** | **Spacebar** | Click **SCAN** | `scan` |
| **Clear View** | **Q** | Click **WIPE** | `wipe` |

**Investigation Commands (Text Only):**
Use the terminal to find details that the visual scanner might miss.
*   `search` - Scours the current room for hidden items and describes them.
*   `rummage` - Checks your backpack to list the items you are carrying.

**System Controls:**
*   **ESC** - Pause Game / Save / Load / Quit.
*   **N** - **[God Mode]** Skip to the next level instantly.

---

### 🗝️ Game Mechanics

1.  **Lidar Vision:** You are in the dark. Walls are invisible until you move or scan, which paints "dots" on your screen using Raycasting mathematics.
2.  **Portals:**
    *   **Purple/Open:** Safe to walk through.
    *   **Gray/Locked:** Requires **3 Inventory Items** (Tokens) to unlock. Walking into them will consume your items automatically.
3.  **Procedural Generation:** The game map is linear (Room 0 -> 6), but the doors linking the rooms are randomized every new game.
4.  **Speedrunning:** A unique **Seed** is generated at the start of every run (displayed on HUD). The game tracks your completion time.

---

### 🛠️ Technical Highlights

*   **MVC Pattern:** Strict separation of Game Logic (Model), Swing UI/Renderer (View), and Input Handling (Controller).
*   **Raycasting Engine:** Custom 2D-to-3D projection implementation (Wolfenstein 3D style) without external graphics libraries.
*   **Custom Serialization:** Robust JSON saving and loading system using a custom bracket-counting parser (no Gson/Jackson).
*   **Multithreading:** Dedicated threads for the Game Loop, Timer, SFX, and Music management.





