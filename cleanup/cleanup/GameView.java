import java.util.List;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GameView {

    


    public static class UIContext {
        public JFrame frame;
        public JPanel mainContainer;
        public CardLayout cardLayout;
        public DisplayGame gamePanel;
        public GameDialogPanel dialogPanel;
        public InventoryPanel inventoryPanel;
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
        
        // --- STYLING: Window Frame & Background ---
        ctx.frame.getContentPane().setBackground(Color.BLACK); 
        
        ctx.cardLayout = new CardLayout();
        ctx.mainContainer = new JPanel(ctx.cardLayout);
        ctx.mainContainer.setBackground(Color.BLACK);

        JPanel gameContainer = new JPanel(new BorderLayout());
        gameContainer.setBackground(Color.BLACK);
        
        ctx.gamePanel = new DisplayGame(model);
        gameContainer.add(ctx.gamePanel, BorderLayout.CENTER);

        // --- Left: Dialog Panel ---
        ctx.dialogPanel = new GameDialogPanel();
        ctx.dialogPanel.setPreferredSize(new Dimension(250, 0));
        styleComponent(ctx.dialogPanel, true);
        gameContainer.add(ctx.dialogPanel, BorderLayout.WEST);

        // --- Right: Inventory Panel ---
        ctx.inventoryPanel = new InventoryPanel(model);
        ctx.inventoryPanel.setPreferredSize(new Dimension(150, 0));
        styleComponent(ctx.inventoryPanel, true);
        gameContainer.add(ctx.inventoryPanel, BorderLayout.EAST);

        // --- Bottom: Controls & Actions ---
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBackground(Color.BLACK);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GREEN));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 1. ACTION SECTION
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        styleComponent(actionPanel, true);
        
        ctx.btnPickUp = new JButton("Grab (E)");
        ctx.btnDrop = new JButton("Drop (R)");
        styleComponent(ctx.btnPickUp, false);
        styleComponent(ctx.btnDrop, false);
        
        ctx.itemDropdown = new JComboBox<>();
        ctx.itemDropdown.setPreferredSize(new Dimension(150, 25));
        ctx.itemDropdown.setBackground(Color.BLACK);
        ctx.itemDropdown.setForeground(Color.GREEN);
        
        actionPanel.add(ctx.btnPickUp);
        actionPanel.add(ctx.btnDrop);
        actionPanel.add(ctx.itemDropdown);

        gbc.gridx = 0; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        bottomPanel.add(actionPanel, gbc);

        // 2. LIDAR CONTROLS
        JPanel lidarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        styleComponent(lidarPanel, true);
        
        JButton btnScan = new JButton("SCAN");
        styleComponent(btnScan, false);
        btnScan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) { model.kScan = true; }
            public void mouseReleased(java.awt.event.MouseEvent e) { model.kScan = false; }
        });

        JButton btnWipe = new JButton("WIPE");
        styleComponent(btnWipe, false);
        btnWipe.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) { model.kWipe = true; }
            public void mouseReleased(java.awt.event.MouseEvent e) { model.kWipe = false; }
        });

        lidarPanel.add(btnScan);
        lidarPanel.add(btnWipe);
        
        gbc.gridx = 1; gbc.weightx = 0.5; gbc.anchor = GridBagConstraints.CENTER;
        bottomPanel.add(lidarPanel, gbc);

        // 3. MOVEMENT PAD
        JPanel movePanel = new JPanel(new GridLayout(2, 3, 2, 2));
        styleComponent(movePanel, true);
        
        movePanel.add(new JLabel("")); 
        movePanel.add(createMoveButton("▲", model, "UP"));
        movePanel.add(new JLabel("")); 
        
        movePanel.add(createMoveButton("◄", model, "LEFT"));
        movePanel.add(createMoveButton("▼", model, "DOWN"));
        movePanel.add(createMoveButton("►", model, "RIGHT"));

        gbc.gridx = 2; gbc.weightx = 0.5; gbc.anchor = GridBagConstraints.EAST;
        bottomPanel.add(movePanel, gbc);

        gameContainer.add(bottomPanel, BorderLayout.SOUTH);

        // --- Screens ---
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

    private static void styleComponent(JComponent c, boolean isContainer) {
        c.setBackground(Color.BLACK);
        c.setForeground(Color.GREEN);
        if (!isContainer) {
            c.setFont(new Font("Monospaced", Font.BOLD, 14));
            if (c instanceof JButton) {
                ((JButton) c).setFocusPainted(false);
                ((JButton) c).setBorder(BorderFactory.createLineBorder(Color.GREEN));
            }
            if (c instanceof JTextArea || c instanceof JTextField) {
                ((javax.swing.text.JTextComponent)c).setCaretColor(Color.GREEN);
            }
        }
    }

    private static JButton createMoveButton(String text, GameModel model, String direction) {
        JButton btn = new JButton(text);
        styleComponent(btn, false);
        btn.setPreferredSize(new Dimension(45, 45)); 
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                switch(direction) {
                    case "UP": model.kUp = true; break;
                    case "DOWN": model.kDown = true; break;
                    case "LEFT": model.kLeft = true; break;
                    case "RIGHT": model.kRight = true; break;
                }
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                switch(direction) {
                    case "UP": model.kUp = false; break;
                    case "DOWN": model.kDown = false; break;
                    case "LEFT": model.kLeft = false; break;
                    case "RIGHT": model.kRight = false; break;
                }
            }
        });
        return btn;
    }


    public enum RoomTheme {
        CELL(0, Color.WHITE),
        PILLAR(1, Color.LIGHT_GRAY),
        LIBRARY(2, new Color(139, 69, 19)),  // Brown
        SERVER(3, Color.GREEN),              // Matrix style
        HYPOSTYLE(4, Color.MAGENTA),         // Mystical
        ATRIUM(5, Color.ORANGE),             // Grand
        SERPENT(6, Color.RED);               // Danger

        private final int id;
        private final Color color;

        RoomTheme(int id, Color color) {
            this.id = id;
            this.color = color;
        }

        public static Color getColor(int id) {
            for (RoomTheme t : values()) {
                if (t.id == id) return t.color;
            }
            return Color.WHITE; // Fallback
        }
    }


    // 3. The Renderer Class
    public static class DisplayGame extends JPanel {

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

        private List<Dot> dots = new LinkedList<>(); 
        private static final int MAX_DOTS = 5000; 
        private GameModel model;


        private Image victoryImage;

        public DisplayGame(GameModel model) {
            this.model = model;
            setPreferredSize(new Dimension(SCREEN_W, SCREEN_H));
            setBackground(Color.BLACK);
            setFocusable(true); 
        
        
            try {
                victoryImage = javax.imageio.ImageIO.read(new java.io.File("assets/victory.jpg"));
            } catch (Exception e) {
                System.out.println("Warning: victory.png not found in assets.");
            }
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2 = (Graphics2D) g;

            if (model.gameState == 1) {
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                if (victoryImage != null) {
                    g2.drawImage(victoryImage, 0, 0, getWidth(), getHeight(), this);
                    g2.setColor(Color.BLACK);
                    g2.drawString("VICTORY", getWidth()/2 - 50, getHeight()/2);
                } else {
                    g2.setColor(Color.GREEN);
                    g2.drawString("VICTORY", getWidth()/2 - 50, getHeight()/2);
                }
                return; 
            }
            
          
            if (model.getCurrentMap() == null) return;
            if (model.player == null) return;
            
            // Draw Dots
            for (Dot d : dots) {
                double rx = d.x - model.player.getPx();
                double ry = d.y - model.player.getPy();
                
                double rotX = (rx * Math.cos(model.player.getAngle())) + (ry * Math.sin(model.player.getAngle()));
                double rotY = (rx * Math.sin(-model.player.getAngle())) + (ry * Math.cos(model.player.getAngle()));

                if (rotX <= 0.1) continue;

                double scale = 500.0; 
                int screenX = (int)(getWidth()/2 + (rotY / rotX) * scale);
                int screenY = getHeight()/2;
                int height = (int)(SCREEN_H / rotX); 
                int width  = Math.max(2, height / 8); 

                // Shading
                float brightness = (float)(1.0 / (rotX * 0.3 + 1)); 
                brightness = Math.min(1f, Math.max(0f, brightness));
                
                Color c = d.color;
                Color shaded;
                
                if (c.equals(Color.BLACK)) {
                     shaded = Color.BLACK;
                } else {
                    shaded = new Color(
                        (int)(c.getRed() * brightness),
                        (int)(c.getGreen() * brightness),
                        (int)(c.getBlue() * brightness)
                    );
                }
                
                g2.setColor(shaded);
                g2.fillRect(screenX - width/2, screenY - height/2, width, height);
            }
            

            // HUD
            g2.setColor(Color.GREEN);
            g2.setFont(new Font("Monospaced", Font.BOLD, 20));

            if (model.timer != null) {

                String formattedTime = String.format("%.1f", model.timer.seconds);

                g2.drawString("TIME: " + formattedTime + "s", 20, 50);
            }


            g2.drawString("TOKENS: " + model.player.getInventory().getList().size() + "/3", 20, 80);

            if (model.player.getCurrentRoom() != null) {
                g2.drawString("LOC: " + model.player.getCurrentRoom().getName(), 20, 110);
            }  
            
            String seed = model.player.getCurrentRoom().getMapSeed();
                g2.drawString(seed != null ? seed : "SEED: ???", 20, 135);
            

            g2.setColor(Color.GREEN);
            g2.drawString("MAP ITEMS: " + model.countItems(), 20, 20);
        }

        public void wipeMap(){ dots.clear(); }

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
            double Rx = Math.cos(angle);
            double Ry = Math.sin(angle);
            double distance = 0;
            
            while (distance < 50.0) { 
                rayX += Rx * 0.05;
                rayY += Ry * 0.05;
                distance += 0.05;

                boolChar tile = getTile(rayX, rayY);

                if (tile.getB()) {
                    if (tile.getC() == '#') {
                        int rId = model.player.getCurrentRoom().getId();
                        addDot(rayX, rayY, distance, RoomTheme.getColor(rId));
                    } 
                    // LOCKED DOORS (i, o) -> DARK GRAY
                    else if ("io".indexOf(tile.getC()) != -1) {
                        addDot(rayX, rayY, distance, Color.DARK_GRAY); 
                    }
                    // OPEN PORTALS (I, O) -> PURPLE
                    else if ("IO".indexOf(tile.getC()) != -1) {
                        addDot(rayX, rayY, distance, new Color(138, 43, 226)); 
                    }
                    return; 
                } 
                else if (!tile.getB() && (tile.getC() >= '1' && tile.getC() <= '9')) {
                    switch (tile.getC()){
                        case '1': addDot(rayX, rayY, distance, Color.YELLOW); break;
                        case '2': addDot(rayX, rayY, distance, Color.BLUE); break;
                        case '3': addDot(rayX, rayY, distance, Color.RED); break;
                        default:  addDot(rayX, rayY, distance, Color.GREEN); break;
                    }
                    return; 
                }
            }
        }

        private void addDot(double x, double y, double dist, Color c) {
            dots.add(new Dot(x, y, dist, c));
            if (dots.size() > MAX_DOTS) dots.remove(0); 
        }

        private boolChar getTile(double x, double y) {
            boolChar answer = new boolChar('#', true);
            int mx = (int)x;
            int my = (int)y;

            if (my < 0 || my >= model.getCurrentMap().length || mx < 0 || mx >= model.getCurrentMap()[0].length()) {
                return answer;
            }

            char cha = model.getCurrentMap()[my].charAt(mx);

            switch (cha) {
                // Empty Space, Items, and 'A' (Player Start) are invisible
                case '1': case '2': case '3': case '4': case '5':
                case '6': case '7': case '8': case '9':
                case '.': 
                    answer.setB(false);
                    answer.setC(cha);
                    break;

                //  (Solid)
                case '#': case 'i': case 'o': case 'I': case 'O': case 'F':
                    answer.setB(true); 
                    answer.setC(cha);
                    break;                    

                default:
                    answer.setB(true);
                    answer.setC('#');
                    break;
            }
            return answer;
        }

        private class boolChar{
            char c; boolean b;
            boolChar(char ic, boolean ib){ c=ic; b=ib; }
            public void setB(boolean b) { this.b = b; } // is the thing we are walking into solid or not?
            public void setC(char c) { this.c = c; }
            public char getC() { return c; }            
            public boolean getB() { return b; }
        }
    }
}