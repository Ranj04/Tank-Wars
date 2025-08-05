import java.awt.*;

public abstract class PowerUp {
    protected int x, y;
    protected Image image;
    protected Rectangle bounds;

    public PowerUp(int x, int y, String imagePath) {
        this.x = x;
        this.y = y;
        this.image = Toolkit.getDefaultToolkit().createImage(imagePath);
        this.bounds = new Rectangle(x, y, 40, 40); // assuming 40x40 image size
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public abstract void apply(Tank tank);

    public void draw(Graphics2D g2d) {
        g2d.drawImage(image, x, y, null);
    }
}
