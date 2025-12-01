import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.nio.file.*;
import java.util.*;
import java.io.IOException;



    // this should account for ui button presses and text input for 'control', directly manipulating the values of the model
    // this is the interface between the view and the model for controlling stuff

    // game control will implement the game saving and loading logic too. 
    // we need to save the entire game state to json file.

        // we could have an interface called saveable, with certain methods that must be implemented

        // game control processes the requests we try to make in a polymorphic manner, 
            // no matter if its a button press or text input into the dialog box, this is what will process it and control the model


            // GameModel model = new GameModel();
            // model.play();
            // etc


// control also runs the view script, in fact it just creates a new view off the dome/bat


import java.awt.event.ActionListener; // Import might be needed



    // This represents the "Room" data structure you requested.
    // In your full Zork game, every Room object would hold one of these arrays.
    // '1'-'9' are items, '#' are walls, '.' is empty space.


    // public static String[] CURRENT_ROOM_MAP = new String[]{
    //     "########################################",
    //     "##S.................##................##",
    //     "##...#####...........#.......1........##",
    //     "##..##...##...................####....##",
    //     "##.##.....##.................##..##...##",
    //     "##.#...2.....................#....#...##",
    //     "##.#........................##....#...##",
    //     "##.##.....##.......###......##...##...##",
    //     "##..##...##.......##.##......#####....##",
    //     "##...#####.......##...##..............##",
    //     "##..............##.....##.............##",
    //     "##..............#...3...#.............##",
    //     "##.....####.....##.....##.............##",
    //     "##....##..##.....##...................##",
    //     "##....#....#......#####.......####....##",
    //     "##....#....#.................##..##...##",
    //     "##....##..##.................#....#...##",
    //     "##.....####..................#....#...##",
    //     "##...........................##..##...##",
    //     "##............................####....##",
    //     "########################################"
    // };

// PILLAR_HALL_MAP
//     public static String[] CURRENT_ROOM_MAP = new String[]{
//     "########################################",
//     "##....................................##",
//     "##..S..............................1..##",
//     "##......##......##....##......##......##",
//     "##......##......##....##......##......##",
//     "##....................................##",
//     "##....................................##",
//     "##..##......##............##......##..##",
//     "##..##......##.....2......##......##..##",
//     "##..........##............##..........##",
//     "##....................................##",
//     "##....................................##",
//     "##..........##............##..........##",
//     "##..##......##............##......##..##",
//     "##..##......##............##......##..##",
//     "##....................................##",
//     "##....................................##",
//     "##......##......##....##......##......##",
//     "##......##......##....##...3..##......##",
//     "##....................................##",
//     "########################################"
// };


// TWIN_FORTRESS_MAP
// public static String[] CURRENT_ROOM_MAP = new String[]{
//     "########################################",
//     "##S..##...................##..........##",
//     "##...##...................##....1.....##",
//     "##...##...................##..........##",
//     "##...##.......#####.......##..........##",
//     "##............#...#.......##...####...##",
//     "##............#...#............#..#...##",
//     "###############...##############..#...##",
//     "##................................#...##",
//     "##................................#...##",
//     "###############...##############..#...##",
//     "##............#...#............#..#...##",
//     "##............#...#.......##...####...##",
//     "##...##.......#####.......##..........##",
//     "##...##...................##....2.....##",
//     "##...##...................##..........##",
//     "##...##...................##..........##",
//     "##...##...................##..........##",
//     "##...##...3...............##..........##",
//     "##........................##..........##",
//     "########################################"
// };


// SPIRAL_MAP
public static String[] CURRENT_ROOM_MAP = new String[]{
    "########################################",
    "##....................................##",
    "##.S..###############################.##",
    "##....................................##",
    "##.#####################################",
    "##....................................##",
    "#####################################.##",
    "##....................................##",
    "##.#####################################",
    "##...................1................##",
    "#####################################.##",
    "##....................................##",
    "##.#####################################",
    "##....................................##",
    "#####################################.##",
    "##....................................##",
    "##.#####################################",
    "##...................................2##",
    "########################################",
    "########################################",
    "########################################"
};




        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Lidar Zork Interface");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            RaycastCave gamePanel = addComponentsToPane(frame.getContentPane());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            
            gamePanel.requestFocusInWindow();
        });
    

        GameView.DisplayGame gamePanel = new GameView.DisplayGame(level1);
        
        frame.add(gamePanel);
        frame.setSize(400, 400);
        frame.setVisible(true);




public class GameController {

    public static List<String[]> ALL_MAPS = new ArrayList<>();

    public static void loadMaps() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("maps.json"));
        List<String> currentMap = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();
            // If line is a map row (starts with quote, doesn't contain ':')
            if (line.startsWith("\"") && !line.contains(":")) {
                currentMap.add(line.replace("\"", "").replace(",", ""));
            } 
            // If line is end of an array ']', save what we have
            else if (line.startsWith("]")) {
                ALL_MAPS.add(currentMap.toArray(new String[0]));
                currentMap.clear();
            }
        }
    }




    public GameController(GameDialogPanel view) {
        this.view = view;

        // Attach the listener so when user hits 'Enter', handleInput() runs
        this.view.addInputListener(e -> handleInput());
    }


    private void handleInput() {
        String userInput = view.getInputText();

        if (userInput != null && !userInput.trim().isEmpty()) {
            // Echo input to screen
            view.appendText("You: " + userInput);

            // TODO: Pass 'userInput' to your Model here
            // String result = model.process(userInput);
            
            // For now, just dummy response:
            view.appendText("Game: I don't know how to process '" + userInput + "' yet.");

            view.clearInput();
        }

        // need to handle text input here. 


    }



        public static void main(String[] args) throws IOException {
            loadMaps();
            private GameDialogPanel view;
            private GameModel model = new GameModel();

            model.play();

            String[] selectedMap = ALL_MAPS.get(1); 

            // 3. Pass 'selectedMap' to your renderer logic...
            System.out.println("Loaded map with " + selectedMap.length + " rows.");
    }
}