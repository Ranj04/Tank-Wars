import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Bullet {
    private int x, y;
    private final int speed = 8;
    private double angle;
    private BufferedImage image;

    public Bullet(int x, int y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;

        try {
            image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("shell.gif"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Bullet image could not be loaded.");
            e.printStackTrace();
        }
    }

    public void update() {
        x += (int)(speed * Math.cos(Math.toRadians(angle)));
        y += (int)(speed * Math.sin(Math.toRadians(angle)));
    }

    public void draw(Graphics2D g2d) {
        if (image != null) {
            int cx = image.getWidth() / 2;
            int cy = image.getHeight() / 2;

            g2d.translate(x + cx, y + cy);
            g2d.rotate(Math.toRadians(angle));
            g2d.drawImage(image, -cx, -cy, null);
            g2d.rotate(-Math.toRadians(angle));
            g2d.translate(-(x + cx), -(y + cy));
        } else {
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(x, y, 10, 10);
        }
    }

    public boolean isOffScreen(int width, int height) {
        return x < -20 || x > width + 20 || y < -20 || y > height + 20;
    }
}
