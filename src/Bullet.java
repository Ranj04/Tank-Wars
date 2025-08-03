import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Bullet {
    private double x, y;
    private double angle;
    private int speed = 10;
    private BufferedImage image;
    private final int WIDTH = 30;
    private final int HEIGHT = 30;

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
    }

    public void draw(Graphics2D g2d) {
        if (image == null) {
            g2d.setColor(Color.YELLOW);
            g2d.fillOval((int) x, (int) y, WIDTH, HEIGHT);
            return;
        }
        AffineTransform original = g2d.getTransform();

        // Move to the center of the bullet
        g2d.translate(x + image.getWidth() / 2, y + image.getHeight() / 2);

        // Rotate using radians
        g2d.rotate(Math.toRadians(angle));

        // Draw the image centered
        g2d.drawImage(image, -image.getWidth() / 2, -image.getHeight() / 2, null);

        // Restore original transform
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

    // checks collision with a wall
    public boolean collidesWith(Rectangle wallBounds) {
        return getBounds().intersects(wallBounds);
    }

    // New method: for external use
    public boolean collidesWith(Wall wall) {
        return collidesWith(wall.getBounds());
    }
}
