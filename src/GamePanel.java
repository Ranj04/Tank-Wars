import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private JButton restartButton;
    private JButton exitButton;
    private JPanel buttonPanel;
    private Timer timer;
    private Tank player1;
    private Tank player2;
    private List<Wall> walls = new ArrayList<>();
    private List<PowerUp> powerUps = new ArrayList<>();
    private boolean gameOver = false;
    private boolean showStartScreen = true;
    private String winner = "";
    private List<Explosion> explosions = new ArrayList<>();
    private boolean explosionPlayed = false;
    private Image startGif;
    private int gifX = -200; // start off-screen left
    private boolean startAnimationInProgress = false;
    private boolean startAnimationDone = false;
    private float gameAlpha = 0f;  // For fade-in
    private Clip startSound;
    private AudioPlayer backgroundMusic;
    private boolean musicStarted = false;
    private JSlider volumeSlider;
    private JPanel volumePanel;
    private JLabel sliderValue; // Make sliderValue a field



    public GamePanel() {
        setLayout(null);
        setPreferredSize(new Dimension(1600, 900));
        setBackground(Color.BLACK);

        startGif = Toolkit.getDefaultToolkit().createImage("res/tankgif.gif");
        startGif = Toolkit.getDefaultToolkit().createImage("res/tankgif.gif");

        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("res/engine.wav"));
            startSound = AudioSystem.getClip();
            startSound.open(audioIn);
        } catch (Exception e) {
            System.err.println("Failed to load engine sound: " + e.getMessage());
        }


        player1 = new Tank(200, 300, "tank1.png", 1);
        player2 = new Tank(1000, 300, "tank2.png", 2);


        Random rand = new Random();
        walls.clear();
        int wallWidth = 40;
        int wallHeight = 40;
        int numWalls = 6;

        for (int i = 0; i < numWalls; ) {
            int x = rand.nextInt(1600 - wallWidth);
            int y = rand.nextInt(900 - wallHeight);

            boolean isBreakable = rand.nextInt(100) < 70;
            Wall newWall = new Wall(x, y, isBreakable);

            Rectangle wallRect = new Rectangle(x, y, wallWidth, wallHeight);
            Rectangle p1 = new Rectangle((int) player1.getX(), (int) player1.getY(), 60, 60);
            Rectangle p2 = new Rectangle((int) player2.getX(), (int) player2.getY(), 60, 60);

            boolean overlaps = false;
            for (Wall existing : walls) {
                Rectangle existingRect = new Rectangle(existing.getX(), existing.getY(), wallWidth, wallHeight);
                if (wallRect.intersects(existingRect)) {
                    overlaps = true;
                    break;
                }
            }

            if (wallRect.intersects(p1) || wallRect.intersects(p2)) overlaps = true;

            if (!overlaps) {
                walls.add(newWall);
                i++;
            }
        }

        player1.setWalls(walls);
        player2.setWalls(walls);
        SpeedBoost boost = generateValidSpeedBoost(walls);
        if (boost != null) {
            powerUps.add(boost);
        }
        powerUps.add(new Shield(300, 400, 1)); // Red shield for tank1
        powerUps.add(new Shield(1200, 400, 2)); // Blue shield for tank2
        powerUps.add(new DoubleDamage(700, 500));

        setFocusable(true);
        addKeyListener(this);

        timer = new Timer(16, this);
        timer.start();

        // Buttons
        restartButton = new JButton("Restart");
        exitButton = new JButton("Exit");
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 30);
        restartButton.setFont(buttonFont);
        exitButton.setFont(buttonFont);

        restartButton.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusPainted(false);
        restartButton.setBorder(null);
        restartButton.setOpaque(false); // Make button transparent
        restartButton.setContentAreaFilled(false); // Remove button fill
        restartButton.setHorizontalAlignment(SwingConstants.CENTER);

        exitButton.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorder(null);
        exitButton.setOpaque(false); // Make button transparent
        exitButton.setContentAreaFilled(false); // Remove button fill
        exitButton.setHorizontalAlignment(SwingConstants.CENTER);

        restartButton.setVisible(false);
        exitButton.setVisible(false);

        restartButton.addActionListener(e -> restartGame());
        exitButton.addActionListener(e -> System.exit(0));

        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBounds((1600 - 200) / 2, 580, 200, 100);
        buttonPanel.add(restartButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        buttonPanel.add(exitButton);

        add(buttonPanel);
        // Create and style the volume slider
        volumeSlider = new JSlider(0, 100, 80); // 0 = mute, 100 = full
        volumeSlider.setPreferredSize(new Dimension(100, 24));
        volumeSlider.setOpaque(true);
        volumeSlider.setFocusable(false);
        volumeSlider.setForeground(new Color(57, 255, 20)); // neon green
        volumeSlider.setBackground(new Color(30, 30, 30));  // dark background
        volumeSlider.setBorder(BorderFactory.createLineBorder(new Color(75, 83, 32), 2));
        volumeSlider.setUI(new javax.swing.plaf.metal.MetalSliderUI() {
            @Override
            public void paintThumb(Graphics g) {
                g.setColor(new Color(180, 0, 0));
                g.fillRect(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
            }
            @Override
            public void paintTrack(Graphics g) {
                g.setColor(new Color(57, 255, 20));
                g.fillRect(trackRect.x, trackRect.y + trackRect.height / 2 - 2, trackRect.width, 4);
            }
        });

        // Volume panel setup
        volumePanel = new JPanel();
        volumePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        volumePanel.setBounds(20, 780, 160, 36);   // Increase width for label
        volumePanel.setOpaque(false);
        volumePanel.setBackground(null);
        volumePanel.setBorder(null);
        volumePanel.removeAll();
        volumePanel.add(volumeSlider);
        // Make sliderValue a field so it doesn't get garbage collected
        sliderValue = new JLabel(volumeSlider.getValue() + "%");
        sliderValue.setForeground(new Color(57, 255, 20));
        sliderValue.setFont(sliderValue.getFont().deriveFont(Font.BOLD, 16f));
        sliderValue.setPreferredSize(new Dimension(40, 24)); // Ensure space is reserved
        volumePanel.add(sliderValue);
        volumeSlider.addChangeListener(e -> {
            float volume = volumeSlider.getValue() / 100f;
            backgroundMusic.setVolume(volume);
            sliderValue.setText(volumeSlider.getValue() + "%");
        });
        volumePanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                System.out.println("Panel size: " + volumePanel.getWidth() + "x" + volumePanel.getHeight());
                System.out.println("Slider size: " + volumeSlider.getWidth() + "x" + volumeSlider.getHeight());
            }
        });

        add(volumePanel);
        volumePanel.setVisible(false);


        backgroundMusic = new AudioPlayer("res/background_music.wav");


    }


    private Clip engineClip;

    private void playEngineSound() {
        try {
            if (engineClip == null || !engineClip.isActive()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("res/engine.wav"));
                engineClip = AudioSystem.getClip();
                engineClip.open(audioIn);
                engineClip.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void drawHealthBar(Graphics2D g2, int x, int y, int currentHealth, int maxHealth) {
        int barWidth = 60;
        int barHeight = 8;
        int barX = x - barWidth / 2;
        int barY = y + 65;

        // Background
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(barX, barY, barWidth, barHeight);

        // Health bar (neon green)
        if (currentHealth > 0) {
            int healthWidth = (int) ((double) currentHealth / maxHealth * barWidth);
            g2.setColor(new Color(57, 255, 20));
            g2.fillRect(barX, barY, healthWidth, barHeight);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (showStartScreen && !startAnimationInProgress) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(new Color(75, 83, 32));
            g.setFont(new Font("Arial", Font.BOLD, 80));
            String title = "Tank Wars";
            FontMetrics titleFm = g.getFontMetrics();
            int titleX = (getWidth() - titleFm.stringWidth(title)) / 2;
            int titleY = getHeight() / 2 - 50;
            g.drawString(title, titleX, titleY);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 25));
            String instruction = "Press ENTER to Start";
            FontMetrics instrFm = g.getFontMetrics();
            int instrX = (getWidth() - instrFm.stringWidth(instruction)) / 2;
            int instrY = titleY + 80;
            g.drawString(instruction, instrX, instrY);
            return;
        }

        // Animate start GIF and reveal game
        if (startAnimationInProgress && !startAnimationDone) {
            Graphics2D g2 = (Graphics2D) g;
            Shape originalClip = g2.getClip();
            g2.setClip(gifX, 0, getWidth() - gifX, getHeight());
            drawSplitScreen(g2);
            drawMiniMap(g2);
            g2.setClip(originalClip);
            g2.drawImage(startGif, gifX, getHeight() / 2 - 50, 200, 100, this);
            gifX -= 3;
            if (gifX < -200) {
                startAnimationInProgress = false;
                startAnimationDone = true;
                showStartScreen = false;
                backgroundMusic.playLoop();
                if (engineClip != null && engineClip.isRunning()) {
                    engineClip.stop();
                    engineClip.close();
                }
                // Show volume slider after animation is done
                volumePanel.setVisible(true);
            }
            repaint();
            return;
        }


        for (int i = 0; i < walls.size(); i++) {
            Wall wall = walls.get(i);
            wall.draw(g);
        }

        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(new Color(180, 0, 0));
        g.drawString("Player 1", 50, 50);
        g.setColor(Color.WHITE);
        g.drawString("Lives: " + player1.getLives(), 50, 100);

        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(new Color(0, 90, 180));
        g.drawString("Player 2", 1300, 50);
        g.setColor(Color.WHITE);
        g.drawString("Lives: " + player2.getLives(), 1300, 100);

        drawSplitScreen(g);
        drawMiniMap(g2d);

        if (gameOver) {
            // Use a less thick but still bold and impactful font for Game Over
            g.setFont(new Font("Arial Black", Font.BOLD, 80));
            g.setColor(new Color(71, 23, 23));
            FontMetrics metrics = g.getFontMetrics(g.getFont());
            int x = (getWidth() - metrics.stringWidth("Game Over !")) / 2;
            int y = (getHeight() - metrics.getHeight()) / 2;
            g.drawString("GAME OVER !", x, y);

            g.setFont(new Font("Segoe UI Emoji", Font.BOLD, 48));
            metrics = g.getFontMetrics(g.getFont());
            int winnerX = (getWidth() - metrics.stringWidth(winner + " wins!")) / 2;
            int winnerY = y + metrics.getHeight() + 60;

            if (winner.equals("Player 1")) g.setColor(new Color(180, 0, 0));
            else if (winner.equals("Player 2")) g.setColor(new Color(0, 90, 180));

            g.drawString(winner + " wins  ! \uD83D\uDE0A", winnerX, winnerY);
            restartButton.setVisible(true);
            exitButton.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (showStartScreen) return;
        player1.update();
        player2.update();

        List<PowerUp> collected = new ArrayList<>();

        for (PowerUp powerUp : powerUps) {
            if (player1.getBounds().intersects(powerUp.getBounds())) {
                powerUp.apply(player1);
                collected.add(powerUp);
                playPickupSound();
            } else if (player2.getBounds().intersects(powerUp.getBounds())) {
                powerUp.apply(player2);
                collected.add(powerUp);
                playPickupSound();
            }
        }


        powerUps.removeAll(collected);


        player1.checkBulletHits(player2);
        if (!player2.isAlive() && !explosionPlayed) {
            explosions.add(new Explosion((int) player2.getX(), (int) player2.getY()));
            playExplosionSound();
            winner = "Player 1";
            explosionPlayed = true;
        }

        player2.checkBulletHits(player1);
        if (!player1.isAlive() && !explosionPlayed) {
            explosions.add(new Explosion((int) player1.getX(), (int) player1.getY()));
            playExplosionSound();
            winner = "Player 2";
            explosionPlayed = true;
        }

        // Update and remove finished explosions
        for (int i = 0; i < explosions.size(); i++) {
            Explosion explosion = explosions.get(i);
            explosion.update();
            if (explosion.isFinished()) {
                explosions.remove(i);
                i--;
                gameOver = true;  // Only trigger game over AFTER explosion finishes
            }
        }

        // Ensure volume panel is always on top and visible after animation
        if (!volumePanel.isVisible() && startAnimationDone && !showStartScreen) {
            volumePanel.setVisible(true);
            volumePanel.repaint();
            volumePanel.revalidate();
            setComponentZOrder(volumePanel, 0); // bring to front
        }

        repaint();
    }

    private void playPickupSound() {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("res/pickup.wav"));
            Clip pickupClip = AudioSystem.getClip();
            pickupClip.open(audioIn);
            pickupClip.start();
        } catch (Exception e) {
            System.err.println("Failed to play pickup sound: " + e.getMessage());
        }
    }

    private void playExplosionSound() {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("res/Explosion_large.wav"));
            Clip explosionClip = AudioSystem.getClip();
            explosionClip.open(audioIn);
            explosionClip.start();
        } catch (Exception e) {
            System.err.println("Failed to play explosion sound: " + e.getMessage());
        }
    }



    @Override
    public void keyPressed(KeyEvent e) {
        if (showStartScreen && e.getKeyCode() == KeyEvent.VK_ENTER) {
            startAnimationInProgress = true;
            gifX = getWidth();
            playEngineSound();
            repaint();
            return;
        }

        backgroundMusic.playLoop();
        volumePanel.setVisible(true);


        if (gameOver) return;

        int code = e.getKeyCode();

        switch (code) {
            case KeyEvent.VK_W -> player1.moveForward();
            case KeyEvent.VK_S -> player1.moveBackward();
            case KeyEvent.VK_A -> player1.rotateLeft();
            case KeyEvent.VK_D -> player1.rotateRight();
            case KeyEvent.VK_SPACE -> player1.fire();
        }

        switch (code) {
            case KeyEvent.VK_UP -> player2.moveForward();
            case KeyEvent.VK_DOWN -> player2.moveBackward();
            case KeyEvent.VK_LEFT -> player2.rotateRight();
            case KeyEvent.VK_RIGHT -> player2.rotateLeft();
            case KeyEvent.VK_ENTER -> player2.fire();
        }
    }

    private void drawMiniMap(Graphics2D g) {
        final int mapWidth = 200;
        final int mapHeight = 150;
        final int mapX = getWidth() - mapWidth - 20;
        final int mapY = getHeight() - mapHeight - 20;

        final double scaleX = mapWidth / 1600.0;
        final double scaleY = mapHeight / 1200.0;

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(mapX, mapY, mapWidth, mapHeight);
        g.setColor(Color.WHITE);
        g.drawRect(mapX, mapY, mapWidth, mapHeight);

        g.setColor(Color.DARK_GRAY);
        for (Wall wall : walls) {
            int wallX = (int)(wall.getX() * scaleX) + mapX;
            int wallY = (int)(wall.getY() * scaleY) + mapY;
            g.fillRect(wallX, wallY, 5, 5);
        }

        if (player1.isAlive()) {
            int tank1X = (int)(player1.getX() * scaleX) + mapX;
            int tank1Y = (int)(player1.getY() * scaleY) + mapY;
            g.setColor(Color.RED);
            g.fillRect(tank1X, tank1Y, 6, 6);
        }

        if (player2.isAlive()) {
            int tank2X = (int)(player2.getX() * scaleX) + mapX;
            int tank2Y = (int)(player2.getY() * scaleY) + mapY;
            g.setColor(Color.BLUE);
            g.fillRect(tank2X, tank2Y, 6, 6);
        }
    }

    private void drawSplitScreen(Graphics g) {
        BufferedImage leftView = new BufferedImage(getWidth() / 2, getHeight(), BufferedImage.TYPE_INT_ARGB);
        BufferedImage rightView = new BufferedImage(getWidth() / 2, getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D gLeft = leftView.createGraphics();
        Graphics2D gRight = rightView.createGraphics();

        // Scale down camera view to each half
        drawSceneForPlayer(gLeft, player1);
        drawSceneForPlayer(gRight, player2);

        gLeft.dispose();
        gRight.dispose();

        g.drawImage(leftView, 0, 0, null);
        g.drawImage(rightView, getWidth() / 2, 0, null);

        // Optional: draw vertical divider
        g.setColor(new Color(75, 83, 32));
        g.fillRect(getWidth() / 2 - 2, 0, 4, getHeight());
    }

    private void drawSceneForPlayer(Graphics2D g2, Tank player) {
        int viewWidth = getWidth() / 2;
        int viewHeight = getHeight();

        // Calculate camera offset to center on the tank
        int camX = (int) player.getX() - viewWidth / 2;
        int camY = (int) player.getY() - viewHeight / 2;

        // Clamp camera so it doesn’t show empty space
        camX = Math.max(0, Math.min(camX, 1600 - viewWidth));
        camY = Math.max(0, Math.min(camY, 900 - viewHeight));

        // Apply camera transform
        g2.translate(-camX, -camY);

        // Background
        g2.setColor(Color.BLACK);
        g2.fillRect(camX, camY, viewWidth, viewHeight);

        // Draw walls
        for (Wall wall : walls) {
            wall.draw(g2);
        }

        for (PowerUp powerUp : powerUps) {
            powerUp.draw(g2);
        }

        // Draw both tanks and their health bars
        if (player1.isAlive()) {
            player1.draw(g2);
            drawHealthBar(g2, (int) player1.getX() + 30, (int) player1.getY(), player1.getLives(), 3);
        }

        if (player2.isAlive()) {
            player2.draw(g2);
            drawHealthBar(g2, (int) player2.getX() + 30, (int) player2.getY(), player2.getLives(), 3);
        }

        // Draw all active explosions
        for (Explosion explosion : explosions) {
            explosion.draw(g2);
        }

        g2.setFont(new Font("Arial", Font.BOLD, 36));
        g2.setColor(player == player1 ? Color.RED : Color.BLUE);
        g2.drawString(player == player1 ? "Player 1" : "Player 2", camX + 30, camY + 40);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(Color.WHITE);
        g2.drawString("Lives: " + player.getLives(), camX + 30, camY + 75);

        // Undo transform
        g2.translate(camX, camY);

        // Slightly tints background based on player
        if (player == player1) {
            g2.setColor(new Color(255, 0, 0, 20)); // red tint
            g2.fillRect(0, 0, viewWidth, viewHeight);
        } else {
            g2.setColor(new Color(0, 0, 255, 20)); // blue tint
            g2.fillRect(0, 0, viewWidth, viewHeight);
        }

    }

    private SpeedBoost generateValidSpeedBoost(List<Wall> walls) {
        int maxAttempts = 100;
        int x, y;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            x = (int) (Math.random() * (1600 - 60)) + 30;
            y = (int) (Math.random() * (900 - 60)) + 30;

            Rectangle boostBounds = new Rectangle(x, y, 60, 60);
            boolean overlapsWall = false;

            for (Wall wall : walls) {
                if (boostBounds.intersects(wall.getBounds())) {
                    overlapsWall = true;
                    break;
                }
            }

            if (!overlapsWall) {
                return new SpeedBoost(x, y); // Found valid spawn!
            }
        }

        return null; // No valid position after attempts
    }

    private void restartGame() {
        gameOver = false;
        winner = "";
        restartButton.setVisible(false);
        exitButton.setVisible(false);

        player1.reset(200, 300);
        player2.reset(1000, 300);

        explosions.clear();
        explosionPlayed = false;


        requestFocusInWindow();
        repaint();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
