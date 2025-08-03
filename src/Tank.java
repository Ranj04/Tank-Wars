import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.ImageIO;

public class Tank {
    private int x, y;
    private double angle;
    private int speed = 3;
    private BufferedImage image;
    private ArrayList<Bullet> bullets;

    public Tank(int x, int y, String imagePath) {
        this.x = x;
        this.y = y;
        this.angle = 0;
        this.bullets = new ArrayList<>();

        try {
            image = ImageIO.read(getClass().getClassLoader().getResourceAsStream(imagePath));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Tank image could not be loaded: " + imagePath);
            e.printStackTrace();
        }
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
            g2d.setColor(Color.GREEN);
            g2d.fillRect(x, y, 50, 50);
        }

        for (Bullet b : bullets) {
            b.draw(g2d);
        }
    }

    public void moveForward() {
        x += (int) (speed * Math.cos(Math.toRadians(angle)));
        y += (int) (speed * Math.sin(Math.toRadians(angle)));
    }

    public void moveBackward() {
        x -= (int) (speed * Math.cos(Math.toRadians(angle)));
        y -= (int) (speed * Math.sin(Math.toRadians(angle)));
    }

    public void rotateLeft() {
        angle -= 5;
        if (angle < 0) angle += 360;
    }

    public void rotateRight() {
        angle += 5;
        if (angle >= 360) angle -= 360;
    }

    public void fire() {
        int centerX = x + image.getWidth() / 2;
        int centerY = y + image.getHeight() / 2;
        bullets.add(new Bullet(centerX, centerY, angle));
    }

    public void update() {
        Iterator<Bullet> iter = bullets.iterator();
        while (iter.hasNext()) {
            Bullet b = iter.next();
            b.update();
            if (b.isOffScreen(800, 600)) {
                iter.remove();
            }
        }
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
