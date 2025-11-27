import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BorderLayoutDemo {
    
    // This represents the "Room" data structure you requested.
    // In your full Zork game, every Room object would hold one of these arrays.
    // '1'-'9' are items, '#' are walls, '.' is empty space.
    public static String[] CURRENT_ROOM_MAP = new String[]{
        "########################################",
        "##S.................##................##",
        "##...#####...........#.......1........##",
        "##..##...##...................####....##",
        "##.##.....##.................##..##...##",
        "##.#...2.....................#....#...##",
        "##.#........................##....#...##",
        "##.##.....##.......###......##...##...##",
        "##..##...##.......##.##......#####....##",
        "##...#####.......##...##..............##",
        "##..............##.....##.............##",
        "##..............#...3...#.............##",
        "##.....####.....##.....##.............##",
        "##....##..##.....##...................##",
        "##....#....#......#####.......####....##",
        "##....#....#.................##..##...##",
        "##....##..##.................#....#...##",
        "##.....####..................#....#...##",
        "##...........................##..##...##",
        "##............................####....##",
        "########################################"
    };




    public static boolean RIGHT_TO_LEFT = false;
     
    public static RaycastCave addComponentsToPane(Container pane) {
         
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
        }
         
        if (RIGHT_TO_LEFT) {
            pane.setComponentOrientation(
                    java.awt.ComponentOrientation.RIGHT_TO_LEFT);
        }
         
        JButton button = new JButton("");
     
        // The Game Panel, big and in the centre
        RaycastCave gamePanel = new RaycastCave();
        pane.add(gamePanel, BorderLayout.CENTER);
         
        // Simple Controls UI
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("ARROWS to Move | SPACE to Scan | W to Wipes |'E' to Pick Up | 'R' to Drop"));
        pane.add(bottomPanel, BorderLayout.PAGE_START);
        
        button = new JButton("Button 3 (LINE_START)");
        pane.add(button, BorderLayout.LINE_START);
         
        button = new JButton("Long-Named Button 4 (PAGE_END)");
        pane.add(button, BorderLayout.PAGE_END);
         
        button = new JButton("5 (LINE_END)");
        pane.add(button, BorderLayout.LINE_END);

        System.out.println(Arrays.toString(pane.getComponents()));

        return gamePanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Lidar Zork Interface");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            RaycastCave gamePanel = addComponentsToPane(frame.getContentPane());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            
            gamePanel.requestFocusInWindow();
        });
    }



    // ===================================
    // Game logic
    // ===================================
    public static class RaycastCave extends JPanel {

        private static final int SCREEN_W = 1000;
        private static final int SCREEN_H = 600;
        private static final double VIEW_ANGLE = Math.toRadians(90); 
        
        private double px = 2.5, py = 2.5; 
        private double playerAngle = 0.0;
        
        private boolean kUp, kDown, kLeft, kRight, kScan, kWipe;

        private static class Dot {
            double x, y;
            Color color;
            Dot(double x, double y, double dist, Color c) {
                this.x = x; this.y = y; this.color = c;
            }
        }
        private List<Dot> dots = new ArrayList<>();
        private static final int MAX_DOTS = 5000; // Keep dots forever-ish

        public RaycastCave() {
            setPreferredSize(new Dimension(SCREEN_W, SCREEN_H));
            setBackground(Color.BLACK);
            setFocusable(true);
            setupInput();
            new Timer(16, e -> updateAndDraw()).start();
        }

        // physics
        private void updateAndDraw() {
            // Rotation
            if (kLeft)  playerAngle -= 0.07;
            if (kRight) playerAngle += 0.07;

            // Movement
            double dx = Math.cos(playerAngle) * 0.1;
            double dy = Math.sin(playerAngle) * 0.1;
            
            // Collision Check (Very simple: look ahead)
            if (kUp) {
                if (getTile(px + dx * 2, py + dy * 2).getC() == '.') { px += dx; py += dy; }
            }
            if (kDown) {
                if (getTile(px - dx * 2, py - dy * 2).getC() == '.') { px -= dx; py -= dy; }
            }

            // Fire Lidar if Spacebar held
            if (kScan) fireLidar();
            if (kWipe) WipeMap();
            // Trigger repaint to call paintComponent
            repaint();
        }

        
        private void WipeMap(){
            for (int idx=0 ; idx<dots.size(); idx++){
                dots.remove(idx);
            }
        }


        // Raycaster
        private void fireLidar() {

            double startAngle = playerAngle - (VIEW_ANGLE)/2; 
            double endAngle   = playerAngle + (VIEW_ANGLE)/2;

            double stepAngle  = 0.1;

            for (double a = startAngle; a < endAngle; a += stepAngle) {
                castSingleRay(a);
            }
        }

        private void castSingleRay(double angle) {
            double rayX = px;
            double rayY = py;
            
            // Math: Direction of the ray
            double stepX = Math.cos(angle);
            double stepY = Math.sin(angle);
            
            double distance = 0;
            
            // This is easier to understand than DDA algorithms.
            while (distance < 50.0) { // Max range 25 meters
                rayX += stepX * 0.05;
                rayY += stepY * 0.05;
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


        private void addDot(double x, double y, double dist, Color c) {
            dots.add(new Dot(x, y, dist, c));
            if (dots.size() > MAX_DOTS) dots.remove(0); // Oldest dot deleted
        }



        private boolChar getTile(double x, double y) {

            boolChar answer = new boolChar('#', true);

            int mx = (int)x;
            int my = (int)y;

            // Bounds check
            if (my < 0 || my >= CURRENT_ROOM_MAP.length || mx < 0 || mx >= CURRENT_ROOM_MAP[0].length()) {
                return answer;
            }

            char cha = CURRENT_ROOM_MAP[my].charAt(mx);

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


        // Simulates picking up an item (turning '1' into '.')
        private void interactWithItem(boolean pickup) {
            // Check 1 meter in front of player
            int checkX = (int)(px + Math.cos(playerAngle) * 1.0);
            int checkY = (int)(py + Math.sin(playerAngle) * 1.0);
            
            
            boolChar target = getTile(checkX, checkY);

            // Modify the map string string manually (String is immutable, so we rebuild the line)
            if (pickup && !target.getB()) {
                System.out.println("Picked up item ID: " + target.getC());
                modifyMap(checkX, checkY, '.');
            } 
            else if (!pickup && target.getC() == '.') {
                System.out.printf("Dropped item ID: %s",target.getC());
                modifyMap(checkX, checkY, target.getC());
            }
        }
        

        private void modifyMap(int x, int y, char newChar) {
            char[] row = CURRENT_ROOM_MAP[y].toCharArray();
            row[x] = newChar;
            CURRENT_ROOM_MAP[y] = new String(row);
        }


        // Rendering
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Draw Background
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Draw all Dots, including those that are technically occluded but what the heck
            for (Dot d : dots) {
                // Transform world coordinates to screen coordinates
                // 1. Relative to player
                double rx = d.x - px;
                double ry = d.y - py;
                
                // cos-theta = costheta
                // -sin-theta = sintheta

                // 2. Rotate to face player's view (Standard rotation matrix)
                double rotX = (rx * Math.cos(playerAngle)) + (ry * Math.sin(playerAngle));

                System.out.printf("%2f %2f %2f",rotX, rx * Math.cos(playerAngle), ry * Math.sin(playerAngle));
                System.out.println();
                
                double rotY = (rx * Math.sin(-playerAngle)) + (ry * Math.cos(playerAngle));

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
            g2.drawString("MAP ITEMS: " + countItems(), 20, 20);
        }
        

        private int countItems() {
            int count = 0;
            for(String s : CURRENT_ROOM_MAP) {
                for(char c : s.toCharArray()) if((c>='1'&&c<='9')) count++;
            }
            return count;
        }


        // Inputs
        private void setupInput() {
            addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    switch(e.getKeyCode()) {
                        case KeyEvent.VK_UP:    kUp = true; break;
                        case KeyEvent.VK_DOWN:  kDown = true; break;
                        case KeyEvent.VK_LEFT:  kLeft = true; break;
                        case KeyEvent.VK_RIGHT: kRight = true; break;
                        case KeyEvent.VK_SPACE: kScan = true; break;
                        case KeyEvent.VK_W: kWipe = true; break;
                        
                        // Interface with "Preexisting Methods" logic
                        case KeyEvent.VK_E:     interactWithItem(true); break; // Pick up
                        case KeyEvent.VK_R:     interactWithItem(false); break; // Drop
                    }
                }
                public void keyReleased(KeyEvent e) {
                    switch(e.getKeyCode()) {
                        case KeyEvent.VK_UP:    kUp = false; break;
                        case KeyEvent.VK_DOWN:  kDown = false; break;
                        case KeyEvent.VK_LEFT:  kLeft = false; break;
                        case KeyEvent.VK_RIGHT: kRight = false; break;
                        case KeyEvent.VK_SPACE: kScan = false; break;
                        case KeyEvent.VK_W: kWipe = false; break;
                    }
                }
            });
        }
    }
}