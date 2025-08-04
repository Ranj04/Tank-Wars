import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    private boolean gameOver = false;
    private boolean showStartScreen = true;
    private String winner = "";

    public GamePanel() {
        setPreferredSize(new Dimension(1600, 900));
        setBackground(Color.BLACK);
        setLayout(null);

        player1 = new Tank(200, 300, "tank1.png");
        player2 = new Tank(1000, 300, "tank2.png");

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

        restartButton.setBackground(Color.BLACK);
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusPainted(false);
        restartButton.setBorder(null);
        restartButton.setHorizontalAlignment(SwingConstants.CENTER);

        exitButton.setBackground(Color.BLACK);
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorder(null);
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
    }

    private void drawHealthBar(Graphics2D g2d, int x, int y, int currentHealth, int maxHealth) {
        int barWidth = 60;
        int barHeight = 8;
        int barX = x - barWidth / 2;
        int barY = y + 65;

        // Background
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(barX, barY, barWidth, barHeight);

        // Health bar (neon green)
        if (currentHealth > 0) {
            int healthWidth = (int) ((double) currentHealth / maxHealth * barWidth);
            g2d.setColor(new Color(57, 255, 20));
            g2d.fillRect(barX, barY, healthWidth, barHeight);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (showStartScreen) {
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
            g.setFont(new Font("Arial", Font.BOLD, 35));
            String instruction = "Press ENTER to Start";
            FontMetrics instrFm = g.getFontMetrics();
            int instrX = (getWidth() - instrFm.stringWidth(instruction)) / 2;
            int instrY = titleY + 80;
            g.drawString(instruction, instrX, instrY);
            return;
        }

        for (Wall wall : walls) wall.draw(g);

        if (player1.isAlive()) player1.draw(g2d);
        if (player2.isAlive()) player2.draw(g2d);

        // health bars
        if (player1.isAlive()) {
            drawHealthBar(g2d, (int)player1.getX() + 30, (int)player1.getY(), player1.getLives(), 3);
        }
        if (player2.isAlive()) {
            drawHealthBar(g2d, (int)player2.getX() + 30, (int)player2.getY(), player2.getLives(), 3);
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

        drawMiniMap(g2d);

        if (gameOver) {
            g.setFont(new Font("Segoe UI Emoji", Font.BOLD, 75));
            g.setColor(Color.WHITE);
            FontMetrics metrics = g.getFontMetrics(g.getFont());
            int x = (getWidth() - metrics.stringWidth("Game Over!")) / 2;
            int y = (getHeight() - metrics.getHeight()) / 2;
            g.drawString("GAME OVER!", x, y);

            g.setFont(new Font("Segoe UI Emoji", Font.BOLD, 48));
            metrics = g.getFontMetrics(g.getFont());
            int winnerX = (getWidth() - metrics.stringWidth(winner + " wins!")) / 2;
            int winnerY = y + metrics.getHeight() + 60;

            if (winner.equals("Player 1")) g.setColor(new Color(180, 0, 0));
            else if (winner.equals("Player 2")) g.setColor(new Color(0, 90, 180));

            g.drawString(winner + " wins! \uD83D\uDE0A", winnerX, winnerY);
            restartButton.setVisible(true);
            exitButton.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (showStartScreen) return;
        player1.update();
        player2.update();

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
        if (showStartScreen && e.getKeyCode() == KeyEvent.VK_ENTER) {
            showStartScreen = false;
            repaint();
            return;
        }

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

    private void restartGame() {
        gameOver = false;
        winner = "";
        restartButton.setVisible(false);
        exitButton.setVisible(false);

        player1.reset(200, 300);
        player2.reset(1000, 300);

        requestFocusInWindow();
        repaint();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
