import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.Rectangle;

public class Bullet {
    private double x, y;
    private double angle;
    private int speed = 10;
    private BufferedImage image;
    private final int WIDTH = 30;
    private final int HEIGHT = 30;
    private boolean alive = true;

    public Bullet(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;

        try {
            image = ImageIO.read(new File("res/shell.gif"));
        } catch (IOException e) {
            System.out.println("Error loading bullet image");
        }
    }

    public void update() {
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;

        // Deactivate bullet if it leaves the screen bounds
        if (x < 0 || x > 1600 || y < 0 || y > 900) {
            alive = false;
        }
    }

    public void draw(Graphics2D g2d) {
        if (image == null) {
            g2d.setColor(Color.YELLOW);
            g2d.fillOval((int) x, (int) y, WIDTH, HEIGHT);
            return;
        }

        AffineTransform original = g2d.getTransform();
        g2d.translate(x + image.getWidth() / 2, y + image.getHeight() / 2);
        g2d.rotate(Math.toRadians(angle));
        g2d.drawImage(image, -image.getWidth() / 2, -image.getHeight() / 2, null);
        g2d.setTransform(original);
    }

    public boolean isOffScreen(int width, int height) {
        return x < 0 || y < 0 || x > width || y > height;
    }

    public Rectangle getBounds() {
        int w = (image != null) ? image.getWidth() : WIDTH;
        int h = (image != null) ? image.getHeight() : HEIGHT;
        return new Rectangle((int) x, (int) y, w, h);
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
