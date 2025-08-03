import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class Tank {
    private int x, y;
    private int initialX, initialY;
    private int angle;
    private BufferedImage image;
    private String imagePath;
    private List<Bullet> bullets;
    private List<Wall> walls;
    private int lives = 3;

    public Tank(int x, int y, String imagePath) {
        this.x = x;
        this.y = y;
        this.initialX = x;
        this.initialY = y;
        this.imagePath = imagePath;
        this.angle = 0;
        this.bullets = new ArrayList<>();
        loadImage();
    }

    private void loadImage() {
        try {
            image = ImageIO.read(new File("res/" + imagePath));
        } catch (IOException e) {
            System.out.println("Error loading tank image: " + imagePath);
        }
    }

    public void draw(Graphics2D g2d) {
        if (image != null) {
            Graphics2D g = (Graphics2D) g2d.create();
            g.translate(x + image.getWidth() / 2, y + image.getHeight() / 2);
            g.rotate(Math.toRadians(angle));
            g.drawImage(image, -image.getWidth() / 2, -image.getHeight() / 2, null);
            g.dispose();

            // Draw bullets
            for (Bullet bullet : bullets) {
                bullet.draw(g2d);
            }

            drawHealthBar(g2d);
        }
    }

    public void drawHealthBar(Graphics2D g) {
        int barWidth = 60;
        int barHeight = 10;
        int barX = x + image.getWidth() / 2 - barWidth / 2;
        int barY = y + image.getHeight() + 10;

        // Draw border
        g.setColor(Color.GRAY);
        g.fillRect(barX, barY, barWidth, barHeight);

        // Draw health based on lives
        int healthWidth = (int) ((lives / 3.0) * barWidth);
        g.setColor(new Color(57, 255, 20)); // Neon green color
        g.fillRect(barX, barY, healthWidth, barHeight);

        // Outline
        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, barWidth, barHeight);
    }

    public void update() {
        List<Bullet> toRemove = new ArrayList<>();

        for (Bullet bullet : bullets) {
            bullet.update();

            // Remove if off-screen
            if (bullet.isOffScreen(1600, 1200)) {
                toRemove.add(bullet);
                continue;
            }

            // Remove if collides with a wall
            for (Wall wall : walls) {
                if (bullet.collidesWith(wall)) {
                    toRemove.add(bullet);
                    break;
                }
            }
        }

        bullets.removeAll(toRemove);
    }



    public void moveForward() {
        int newX = x + (int)(Math.cos(Math.toRadians(angle)) * 8);
        int newY = y + (int)(Math.sin(Math.toRadians(angle)) * 8);
        if (!collidesWithWall(newX, newY)) {
            x = newX;
            y = newY;
        }
    }

    public void moveBackward() {
        int newX = x - (int)(Math.cos(Math.toRadians(angle)) * 8);
        int newY = y - (int)(Math.sin(Math.toRadians(angle)) * 8);
        if (!collidesWithWall(newX, newY)) {
            x = newX;
            y = newY;
        }
    }

    public void rotateLeft() {
        angle -= 5;
    }

    public void rotateRight() {
        angle += 5;
    }

    public void fire() {
        double barrelX = x + image.getWidth() / 2;
        double barrelY = y + image.getHeight() / 2;
        bullets.add(new Bullet(barrelX, barrelY, angle));
    }

    public void setWalls(List<Wall> walls) {
        this.walls = walls;
    }

    private boolean collidesWithWall(int newX, int newY) {
        Rectangle futureBounds = new Rectangle(newX, newY, image.getWidth(), image.getHeight());
        for (Wall wall : walls) {
            if (futureBounds.intersects(wall.getBounds())) {
                return true;
            }
        }
        return false;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, image.getWidth(), image.getHeight());
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        lives--;
        resetPosition();
    }

    public void resetPosition() {
        x = initialX;
        y = initialY;
    }

    public boolean isAlive() {
        return lives > 0;
    }

    public void checkBulletHits(Tank enemy) {
        List<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet bullet : bullets) {
            if (bullet.getBounds().intersects(enemy.getBounds())) {
                bulletsToRemove.add(bullet);
                enemy.loseLife();
            }
        }
        bullets.removeAll(bulletsToRemove);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}

