import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Wall {
    private int x, y;
    private boolean isBreakable;
    private static BufferedImage breakableWallImg;
    private static BufferedImage unbreakableWallImg;

    static {
        try {
            breakableWallImg = ImageIO.read(new File("res/wall1.png"));
            unbreakableWallImg = ImageIO.read(new File("res/wall2.png"));
        } catch (IOException e) {
            System.err.println("Error loading wall images: " + e.getMessage());
            breakableWallImg = null;
            unbreakableWallImg = null;
        }
    }

    public Wall(int x, int y) {
        this(x, y, true); // Default to breakable
    }

    public Wall(int x, int y, boolean isBreakable) {
        this.x = x;
        this.y = y;
        this.isBreakable = isBreakable;
    }

    public void draw(Graphics g) {
        BufferedImage img = isBreakable ? breakableWallImg : unbreakableWallImg;

        if (img != null) {
            g.drawImage(img, x, y, 40, 40, null);
        } else {
            // fallback if image fails to load
            g.setColor(Color.GRAY);
            g.fillRect(x, y, 40, 40);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 40, 40);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isBreakable() {
        return isBreakable;
    }

    public void setBreakable(boolean b) {
        this.isBreakable = b;
    }
}
