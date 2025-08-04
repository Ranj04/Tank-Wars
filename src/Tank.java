import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Tank {
    private double x, y;
    private double angle = 270;
    private BufferedImage image;
    private int speed = 4;
    private int rotationSpeed = 4;
    private List<Bullet> bullets = new ArrayList<>();
    private int lives = 3;
    private boolean alive = true;
    private List<Wall> walls = new ArrayList<>();

    public Tank(double x, double y, String imagePath) {
        this.x = x;
        this.y = y;

        try {
            image = ImageIO.read(new File("res/" + imagePath));
        } catch (IOException e) {
            System.out.println("Error loading tank image: " + imagePath);
        }
    }

    public void moveForward() {
        double newX = x + Math.cos(Math.toRadians(angle)) * speed;
        double newY = y + Math.sin(Math.toRadians(angle)) * speed;

        Rectangle newBounds = new Rectangle((int) newX, (int) newY, image.getWidth(), image.getHeight());
        for (Wall wall : walls) {
            if (newBounds.intersects(wall.getBounds())) return;
        }

        x = newX;
        y = newY;
    }

    public void moveBackward() {
        double newX = x - Math.cos(Math.toRadians(angle)) * speed;
        double newY = y - Math.sin(Math.toRadians(angle)) * speed;

        Rectangle newBounds = new Rectangle((int) newX, (int) newY, image.getWidth(), image.getHeight());
        for (Wall wall : walls) {
            if (newBounds.intersects(wall.getBounds())) return;
        }

        x = newX;
        y = newY;
    }

    public void rotateLeft() {
        angle -= rotationSpeed;
    }

    public void rotateRight() {
        angle += rotationSpeed;
    }

    public void fire() {
        double bulletX = x + image.getWidth() / 2 - 10;
        double bulletY = y + image.getHeight() / 2 - 10;
        bullets.add(new Bullet(bulletX, bulletY, angle));
    }

    public void update() {
        bullets.removeIf(b -> !b.isAlive());

        for (Bullet b : bullets) {
            b.update();
        }
    }

    public void draw(Graphics2D g2d) {
        if (!alive) return;

        AffineTransform transform = g2d.getTransform();
        g2d.translate(x + image.getWidth() / 2, y + image.getHeight() / 2);
        g2d.rotate(Math.toRadians(angle));
        g2d.drawImage(image, -image.getWidth() / 2, -image.getHeight() / 2, null);
        g2d.setTransform(transform);

        for (Bullet b : bullets) {
            b.draw(g2d);
        }
    }

    public void checkBulletHits(Tank other) {
        List<Bullet> toRemove = new ArrayList<>();

        for (Bullet bullet : bullets) {
            if (bullet.getBounds().intersects(other.getBounds())) {
                toRemove.add(bullet);
                other.loseLife();
            }
        }

        bullets.removeAll(toRemove);
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, image.getWidth(), image.getHeight());
    }

    public void loseLife() {
        lives--;
        if (lives <= 0) {
            alive = false;
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public int getLives() {
        return lives;
    }

    public void reset(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.angle = 270;
        this.lives = 3;
        this.alive = true;
        this.bullets.clear();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setWalls(List<Wall> walls) {
        this.walls = walls;
    }
}
