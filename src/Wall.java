import java.awt.*;

public class Wall {
    private int x, y;
    private static final int SIZEX = 30;
    private static final int SIZEY = 100;

    public Wall(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect(x, y, SIZEX, SIZEY);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, SIZEX, SIZEY);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
