import java.awt.*;
import javax.swing.Timer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SpeedBoost extends PowerUp {
    private int x, y;
    private BufferedImage image;
    private boolean active = true;
    private final int DURATION = 5000; // 5 seconds
    private boolean used = false;

    public SpeedBoost(int x, int y) {
        super(x, y, "res/SpeedStar.png"); // Use your new gif path
        this.x = x;
        this.y = y;

        try {
            image = ImageIO.read(new File("res/SpeedStar.png")); // Use your new gif
        } catch (IOException e) {
            System.err.println("Failed to load speed boost image");
        }
    }

    @Override
    public void apply(Tank tank) {
        if (!used) {
            used = true;
            active = false;

            int originalSpeed = tank.getSpeed();
            tank.setSpeed(originalSpeed + 3); // Boost by +3
            tank.setActivePowerUpName("Speed Boost");

            Timer timer = new Timer(DURATION, e -> {
                tank.setSpeed(originalSpeed);       // Revert speed
                tank.setActivePowerUpName("");      // Clear name
            });

            timer.setRepeats(false);  // Only run once
            timer.start();            // Start countdown
        }
    }

    public boolean isActive() {
        return active;
    }


    public void draw(Graphics g) {
        if (active && image != null) {
            Graphics2D g2 = (Graphics2D) g;

            // Get original image dimensions
            int width = image.getWidth();
            int height = image.getHeight();
            int drawWidth = (int) (width * 3.5);
            int drawHeight = (int) (height * 3.5);

            // Optional: center the image on (x, y)
            g2.drawImage(image, x - drawWidth / 2, y - drawHeight / 2, drawWidth, drawHeight, null);
        }
    }



    public boolean collidesWith(Tank tank) {
        Rectangle tankBounds = tank.getBounds();
        Rectangle powerUpBounds = new Rectangle(x, y, 40, 40);
        return tankBounds.intersects(powerUpBounds);
    }
}
