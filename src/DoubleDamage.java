import java.awt.*;
import javax.swing.Timer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class DoubleDamage extends PowerUp {
    private int x, y;
    private BufferedImage image;
    private boolean active = true;
    private boolean used = false;
    private final int DURATION = 5000; // 5 seconds

    public DoubleDamage(int x, int y) {
        super(x, y, "res/Rocket.gif");
        this.x = x;
        this.y = y;

        try {
            image = ImageIO.read(new File("res/Rocket.gif"));
        } catch (IOException e) {
            System.err.println("Failed to load double damage image");
        }
    }

    @Override
    public void apply(Tank tank) {
        if (!used) {
            used = true;
            active = false;

            tank.setDoubleDamage(true);
            tank.setActivePowerUpName("Double Damage");

            Timer timer = new Timer(DURATION, e -> {
                tank.setDoubleDamage(false);
                tank.setActivePowerUpName("");
            });

            timer.setRepeats(false);
            timer.start();
        }
    }

    public boolean isActive() {
        return active;
    }

    public void draw(Graphics g) {
        if (active && image != null) {
            Graphics2D g2d = (Graphics2D) g;
            int width = image.getWidth();
            int height = image.getHeight();
            int drawWidth = 50;
            int drawHeight = 50;
            g2d.drawImage(image, x - drawWidth / 2, y - drawHeight / 2, drawWidth, drawHeight, null);
        }
    }

    public boolean collidesWith(Tank tank) {
        Rectangle tankBounds = tank.getBounds();
        Rectangle powerUpBounds = new Rectangle(x, y, 50, 50);
        return tankBounds.intersects(powerUpBounds);
    }
}
