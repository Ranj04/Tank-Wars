import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Tank player1;
    private Tank player2;

    public GamePanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);

        player1 = new Tank(200, 300, "tank1.png"); // WASD
        player2 = new Tank(600, 300, "tank2.png"); // Arrows

        setFocusable(true);
        addKeyListener(this);

        timer = new Timer(16, this); // 60 FPS
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        player1.draw(g2d);
        player2.draw(g2d);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        player1.update();
        player2.update();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
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
            case KeyEvent.VK_LEFT -> player2.rotateLeft();
            case KeyEvent.VK_RIGHT -> player2.rotateRight();
            case KeyEvent.VK_ENTER -> player2.fire();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Optional for smooth movement
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
}
