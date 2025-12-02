import java.util.List;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GameView {

    // Helper class for Controller to access components
    public static class UIContext {
        public JFrame frame;
        public JPanel mainContainer;
        public CardLayout cardLayout;
        
        public DisplayGame gamePanel;
        public GameDialogPanel dialogPanel;
        public InventoryPanel inventoryPanel;  // NEW
        public StartScreen startScreen;
        public PauseScreen pauseScreen;
        
        public JButton btnPickUp;
        public JButton btnDrop;
        public JComboBox<String> itemDropdown;
    }

    public static UIContext createGameUI(GameModel model, 
                                         ActionListener newGame, 
                                         ActionListener loadGame,
                                         ActionListener saveGame,
                                         ActionListener resumeGame,
                                         ActionListener quitToMenu)
    {

        UIContext ctx = new UIContext();
        ctx.frame = new JFrame("MVC Zork Lidar");
        ctx.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        ctx.cardLayout = new CardLayout();
        ctx.mainContainer = new JPanel(ctx.cardLayout);

        JPanel gameContainer = new JPanel(new BorderLayout());
        
        ctx.gamePanel = new DisplayGame(model);
        gameContainer.add(ctx.gamePanel, BorderLayout.CENTER);

        // Left: Dialog Panel
        ctx.dialogPanel = new GameDialogPanel();
        ctx.dialogPanel.setPreferredSize(new Dimension(250, 0));
        gameContainer.add(ctx.dialogPanel, BorderLayout.WEST);

        // Right: Inventory Panel 
        ctx.inventoryPanel = new InventoryPanel(model);
        ctx.inventoryPanel.setPreferredSize(new Dimension(150, 0)); // Adjust width here
        gameContainer.add(ctx.inventoryPanel, BorderLayout.EAST);

        // Bottom: Buttons
        JPanel bottomPanel = new JPanel();
        ctx.btnPickUp = new JButton("Pick Up (E)");

        // Add dropdown for item selection
        ctx.itemDropdown = new JComboBox<>();
        ctx.itemDropdown.setPreferredSize(new Dimension(200, 25));

        ctx.btnDrop = new JButton("Drop (R)");
        bottomPanel.add(ctx.btnPickUp);
        bottomPanel.add(new JLabel("Select Item:"));
        bottomPanel.add(ctx.itemDropdown);
        bottomPanel.add(ctx.btnDrop);
        gameContainer.add(bottomPanel, BorderLayout.SOUTH);







        ctx.startScreen = new StartScreen(newGame, loadGame);
        ctx.pauseScreen = new PauseScreen(saveGame, resumeGame, quitToMenu);

        ctx.mainContainer.add(ctx.startScreen, "MENU");
        ctx.mainContainer.add(gameContainer, "GAME");
        ctx.mainContainer.add(ctx.pauseScreen, "PAUSE");

        ctx.frame.add(ctx.mainContainer);
        ctx.frame.pack();
        ctx.frame.setLocationRelativeTo(null);
        
        return ctx;
    }


    
    // 3. The Renderer Class
    public static class DisplayGame extends JPanel {
        

        // ===========================================

        private static final int SCREEN_W = 1000;
        private static final int SCREEN_H = 600;
        private static final double VIEW_ANGLE = Math.toRadians(90); 

        private static class Dot {
            double x, y;
            Color color;
            Dot(double x, double y, double dist, Color c) {
                this.x = x; this.y = y; this.color = c;
            }
        }

        private List<Dot> dots = new LinkedList<>(); // used the list interface referenc so I can swap for other implementations if needs be
        private static final int MAX_DOTS = 5000; // too many and the game gets too slow

        // ===========================================


        private GameModel model;

        public DisplayGame(GameModel model) {
            this.model = model;
            setPreferredSize(new Dimension(SCREEN_W, SCREEN_H));
            setBackground(Color.BLACK);
            setFocusable(true); // Crucial for KeyListeners
        }


        // ===============================

        
        // Rendering
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (model.getCurrentMap() == null) return;
            if (model.player == null) return;
            
            Graphics2D g2 = (Graphics2D) g;

            // Draw Background
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0,  getWidth(),getHeight());
            g2.setColor(Color.white);
            // String timer = ""+duration;

            // g2.drawString(timer, SCREEN_W/2, 40);
            g2.setColor(Color.BLACK);

            // Draw all Dots, including those that are technically occluded but what the heck
            for (Dot d : dots) {
                // Transform world coordinates to screen coordinates
                // 1. Relative to player
                double rx = d.x - model.player.getPx();
                double ry = d.y - model.player.getPy();
                
                // cos-theta = costheta
                // -sin-theta = sintheta

                // 2. Rotate to face player's view (Standard rotation matrix)
                double rotX = (rx * Math.cos(model.player.getAngle())) + (ry * Math.sin(model.player.getAngle()));

                // System.out.printf("%2f %2f %2f",rotX, rx * Math.cos(model.player.getAngle()), ry * Math.sin(model.player.getAngle()));
                // System.out.println();
                
                double rotY = (rx * Math.sin(-model.player.getAngle())) + (ry * Math.cos(model.player.getAngle()));

                // 3. If behind us, don't draw
                if (rotX <= 0.1) continue;

                // 4. Project to screen (Perspective math: Y / X)
                double scale = 500.0; // Zoom factor
                int screenX = (int)(getWidth()/2 + (rotY / rotX) * scale);
                int screenY = getHeight()/2;
                
                // 5. Calculate Height (Closer = Bigger)
                int height = (int)(SCREEN_H / rotX); // why the fuck is rotx this magical constant that can scale the height of the walls?. 
                int width  = Math.max(2, height / 8); // Thin rectangles

                // 6. Shading (Darker if further away)
                float brightness = (float)(1.0 / (rotX * 0.3 + 1)); 
                brightness = Math.min(1f, Math.max(0f, brightness));
                
                Color c = d.color;
                Color shaded = new Color(
                    (int)(c.getRed() * brightness),
                    (int)(c.getGreen() * brightness),
                    (int)(c.getBlue() * brightness)
                );
                
                g2.setColor(shaded);
                g2.fillRect(screenX - width/2, screenY - height/2, width, height);
            }
            
            // Simple HUD
            g2.setColor(Color.GREEN);
            g2.drawString("MAP ITEMS: " + model.countItems(), 20, 20);
        }



        // ===============================


        // Wipes the map
        public void wipeMap(){
            for (int idx=0 ; idx<dots.size(); idx++){
                dots.remove(idx);
            }
        }

        // Raycaster
        public void fireLidar() {

            double startAngle = model.player.getAngle() - (VIEW_ANGLE)/2; 
            double endAngle   = model.player.getAngle() + (VIEW_ANGLE)/2;

            double stepAngle  = 0.1;

            for (double a = startAngle; a < endAngle; a += stepAngle) {
                castSingleRay(a);
            }
        }



        private void castSingleRay(double angle) {
            double rayX = model.player.getPx();
            double rayY = model.player.getPy();
            
            // Math: Direction of the ray
            double Rx = Math.cos(angle);
            double Ry = Math.sin(angle);
            
            double distance = 0;
            
            // This is easier to understand than DDA algorithms.
            while (distance < 50.0) { // Max range 25 meters
                rayX += Rx * 0.05;
                rayY += Ry * 0.05;
                distance += 0.05;

                boolChar tile = getTile(rayX, rayY);

                if ((tile.getB() == true)&&(tile.getC()=='#')) {
                    // Hit Wall -> Add White Dot
                    addDot(rayX, rayY, distance, Color.WHITE);
                    return; // Stop ray
                } 
                else if ((tile.getB()==false)&&(tile.getC()!='S')&&(tile.getC()!='.')) {
                    // Hit Item (1-9) -> Add Yellow Dot
                    switch (tile.getC()){
                        case '1':
                            addDot(rayX, rayY, distance, Color.YELLOW);
                            break;
                        case '2':
                            addDot(rayX, rayY, distance, Color.BLUE);
                            break;

                        case '3':
                            addDot(rayX, rayY, distance, Color.RED);
                            break;

                    }

                    return; // Stop ray
                }
            }
        }


        // If we hit somenthing that can be registered as a dot, me create a dot
        private void addDot(double x, double y, double dist, Color c) {
            dots.add(new Dot(x, y, dist, c));
            if (dots.size() > MAX_DOTS) dots.remove(0); // Oldest dot deleted
        }


        private boolChar getTile(double x, double y) {

            boolChar answer = new boolChar('#', true);

            int mx = (int)x;
            int my = (int)y;

            // Bounds check
            if (my < 0 || my >= model.getCurrentMap().length || mx < 0 || mx >= model.getCurrentMap()[0].length()) {
                return answer;
            }

            char cha = model.getCurrentMap()[my].charAt(mx);

            switch (cha) {
                case '1': case '2': case '3': case '4': case '5':
                case '6': case '7': case '8': case '9':
                    answer.setB(false);
                    answer.setC(cha);
                    break;

                case 'S':
                    answer.setB(false);
                    answer.setC('S');
                    break;

                case '.':
                    answer.setB(false);
                    answer.setC('.');
                    break;
            }
            return answer;

        }


        private class boolChar{
            char c;
            boolean b;
            boolChar(char ic, boolean ib){
                c=ic;
                b=ib;
            }
            public void setB(boolean b) {
                this.b = b;
            }
            public void setC(char c) {
                this.c = c;
            }
            public char getC() {
                return c;
            }            
            public boolean getB() {
                return b;
            }
        }


        private void modifyMap(int x, int y, char newChar) {
            char[] row = model.getCurrentMap()[y].toCharArray();
            row[x] = newChar;
            model.getCurrentMap()[y] = new String(row);
        }


        


    }
}




