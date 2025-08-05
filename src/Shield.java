import java.awt.*;
import javax.swing.Timer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Shield extends PowerUp {
    private int x, y;
    private BufferedImage image;
    private boolean active = true;
    private boolean used = false;
    private final int DURATION = 5000; // 5 seconds
    private int allowedPlayer; // 1 for red tank, 2 for blue tank

    public Shield(int x, int y, int allowedPlayer) {
        super(x, y, allowedPlayer == 1 ? "res/Shield1.gif" : "res/Shield2.gif");
        this.x = x;
        this.y = y;
        this.allowedPlayer = allowedPlayer;

        try {
            image = ImageIO.read(new File(allowedPlayer == 1 ? "res/Shield1.gif" : "res/Shield2.gif"));
        } catch (IOException e) {
            System.err.println("Failed to load shield image");
        }
    }

    @Override
    public void apply(Tank tank) {
        if (!used && tank.getPlayerNumber() == allowedPlayer) {
            used = true;
            active = false;

            tank.setShielded(true);
            tank.setActivePowerUpName("Shield");

            Timer timer = new Timer(DURATION, e -> {
                tank.setShielded(false);
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

            int drawWidth = 30;
            int drawHeight = 30;

            // Center the image on (x, y)
            g2d.drawImage(image, x - drawWidth / 2, y - drawHeight / 2, drawWidth, drawHeight, null);
        }
    }


    public boolean collidesWith(Tank tank) {
        Rectangle tankBounds = tank.getBounds();
        Rectangle powerUpBounds = new Rectangle(x, y, 60, 60);
        return tankBounds.intersects(powerUpBounds);
    }
}
