import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Tank player1;
    private Tank player2;
    private List<Wall> walls = new ArrayList<>();
    private boolean gameOver = false;
    private String winner = "";

    public GamePanel() {
        setPreferredSize(new Dimension(5000, 5000)); // Enlarged window
        setBackground(Color.BLACK);

        player1 = new Tank(200, 300, "tank1.png"); // WASD
        player2 = new Tank(1000, 300, "tank2.png"); // Arrows

        // Walls
        walls.add(new Wall(300, 200));
        walls.add(new Wall(800, 600));
        walls.add(new Wall(1200, 400));

        player1.setWalls(walls);
        player2.setWalls(walls);

        setFocusable(true);
        addKeyListener(this);

        timer = new Timer(16, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw walls
        for (Wall wall : walls) {
            wall.draw(g);
        }

        // Draw tanks only if alive
        if (player1.isAlive()) player1.draw(g2d);
        if (player2.isAlive()) player2.draw(g2d);

        // Player 1 HUD
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(new Color(180, 0, 0));
        g.drawString("Player 1", 50, 50);
        g.setColor(Color.WHITE);
        g.drawString("Lives: " + player1.getLives(), 50, 100);

        // Player 2 HUD
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(new Color(0, 90, 180));
        g.drawString("Player 2", 1300, 50);
        g.setColor(Color.WHITE);
        g.drawString("Lives: " + player2.getLives(), 1300, 100);

        drawMiniMap(g2d);

        // Game over screen
        if (gameOver) {
            g.setFont(new Font("Segoe UI Emoji", Font.BOLD, 75)); // Slightly bigger font
            g.setColor(new Color(255, 255, 255)); // Maroon color
            FontMetrics metrics = g.getFontMetrics(g.getFont());

            // Center "Game Over!" text
            int x = (getWidth() - metrics.stringWidth("Game Over!")) / 2;
            int y = (getHeight() - metrics.getHeight()) / 2;
            g.drawString("GAME OVER!", x, y);

            // Center winner text below "Game Over!"
            g.setFont(new Font("Segoe UI Emoji", Font.BOLD, 48)); // Font for winner text
            metrics = g.getFontMetrics(g.getFont());
            int winnerX = (getWidth() - metrics.stringWidth(winner + " wins!")) / 2;
            int winnerY = y + metrics.getHeight() + 60; // Increases spacing below "Game Over!"

            // Set color based on the winner
            if (winner.equals("Player 1")) {
                g.setColor(new Color(180, 0, 0)); // Player 1 color
            } else if (winner.equals("Player 2")) {
                g.setColor(new Color(0, 90, 180)); // Player 2 color
            }

            g.drawString("" + winner + " wins! \uD83D\uDE0A", winnerX, winnerY);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        player1.update();
        player2.update();

        // Bullet-tank collisions
        if (!gameOver) {
            player1.checkBulletHits(player2);
            if (!player2.isAlive()) {
                gameOver = true;
                winner = "Player 1";
            }

            player2.checkBulletHits(player1);
            if (!player1.isAlive()) {
                gameOver = true;
                winner = "Player 2";
            }
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) return;

        int code = e.getKeyCode();

        // Player 1 Controls - WASD + SPACE
        switch (code) {
            case KeyEvent.VK_W -> player1.moveForward();
            case KeyEvent.VK_S -> player1.moveBackward();
            case KeyEvent.VK_A -> player1.rotateLeft();
            case KeyEvent.VK_D -> player1.rotateRight();
            case KeyEvent.VK_SPACE -> player1.fire();
        }

        // Player 2 Controls - Arrow Keys + ENTER
        switch (code) {
            case KeyEvent.VK_UP -> player2.moveForward();
            case KeyEvent.VK_DOWN -> player2.moveBackward();
            case KeyEvent.VK_LEFT -> player2.rotateRight(); // Note: mirrored controls
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

        // Background
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(mapX, mapY, mapWidth, mapHeight);
        g.setColor(Color.WHITE);
        g.drawRect(mapX, mapY, mapWidth, mapHeight);

        // Draw scaled walls
        g.setColor(Color.DARK_GRAY);
        for (Wall wall : walls) {
            int wallX = (int)(wall.getX() * scaleX) + mapX;
            int wallY = (int)(wall.getY() * scaleY) + mapY;
            g.fillRect(wallX, wallY, 5, 5); // small block
        }

        // Draw scaled tanks
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

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}
