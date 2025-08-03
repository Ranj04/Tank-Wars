import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import java.util.ArrayList;

public class Tank {
    private int x, y;
    private double angle;
    private BufferedImage image;
    private String imagePath;
    private List<Bullet> bullets;
    private List<Wall> walls; // ADDED: Wall reference for collision (not yet used)

    public Tank(int x, int y, String imagePath) {
        this.x = x;
        this.y = y;
        this.imagePath = imagePath;
        this.angle = 0;
        this.bullets = new ArrayList<>();
        loadImage();
    }

    private void loadImage() {
        try {
            image = ImageIO.read(new File("res/" + imagePath));
        } catch (IOException e) {
            System.err.println("Error loading image: res/" + imagePath);
            e.printStackTrace();
        }
    }


    public void setWalls(List<Wall> walls) {
        this.walls = walls;
    }

    public void draw(Graphics2D g) {
        if (image == null) return;

        int w = image.getWidth();
        int h = image.getHeight();
        g.rotate(Math.toRadians(angle), x + w / 2, y + h / 2);
        g.drawImage(image, x, y, null);
        g.rotate(-Math.toRadians(angle), x + w / 2, y + h / 2);

        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
    }

    public void moveForward() {
        x += (int)(Math.cos(Math.toRadians(angle)) * 5);
        y += (int)(Math.sin(Math.toRadians(angle)) * 5);
    }

    public void moveBackward() {
        x -= (int)(Math.cos(Math.toRadians(angle)) * 5);
        y -= (int)(Math.sin(Math.toRadians(angle)) * 5);
    }

    public void rotateLeft() {
        angle -= 5;
    }

    public void rotateRight() {
        angle += 5;
    }

    public void fire() {
        int centerX = x + image.getWidth() / 2;
        int centerY = y + image.getHeight() / 2;
        bullets.add(new Bullet(centerX, centerY, angle));
    }

    public void update() {
        bullets.removeIf(b -> b.isOffScreen(800, 600));
        for (Bullet bullet : bullets) {
            bullet.update();
        }
    }

}

