import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Bullet {
    private double x, y;
    private double angle; // in radians
    private final int speed = 10;
    private BufferedImage image;
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;

    public Bullet(int x, int y, double angleDegrees) {
        this.x = x;
        this.y = y;
        this.angle = Math.toRadians(angleDegrees); // convert once in constructor

        try {
            image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("shell.gif"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        x += speed * Math.cos(angle);
        y += speed * Math.sin(angle);
    }

    public void draw(Graphics2D g2d) {
        AffineTransform original = g2d.getTransform();

        // Translate to the center of the bullet
        g2d.translate(x + image.getWidth() / 2, y + image.getHeight() / 2);

        // Rotate the bullet to match the tank's angle
        g2d.rotate(angle); // already in radians from constructor

        // Draw the image centered
        g2d.drawImage(image, -image.getWidth() / 2, -image.getHeight() / 2, null);

        g2d.setTransform(original);
    }


    public boolean isOffScreen(int width, int height) {
        return x < 0 || y < 0 || x > width || y > height;
    }
}
