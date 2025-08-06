import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public abstract class PowerUp {
    protected int x, y;
    protected BufferedImage image;
    protected Rectangle bounds;
    protected long spawnTime;
    protected final int SIZE = 40;


    public PowerUp(int x, int y, String imagePath) {
        this.x = x;
        this.y = y;
        this.spawnTime = System.currentTimeMillis();
        try {
            this.image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.err.println("Failed to load power-up image: " + imagePath);
        }

        this.bounds = new Rectangle(x, y, SIZE, SIZE);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public abstract void apply(Tank tank);

    public void draw(Graphics2D g2) {
        long time = System.currentTimeMillis() - spawnTime;
        double pulse = Math.sin(time * 0.01) * 0.3 + 0.7f;

        Composite oldComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) pulse));

        g2.drawImage(image, x, y, SIZE, SIZE, null);

        g2.setComposite(oldComposite);
    }

}
