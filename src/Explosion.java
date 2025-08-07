import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Explosion {
    private BufferedImage[] frames;
    private int currentFrame = 0;
    private long lastFrameTime;
    private final long frameDuration = 75; // ms per frame
    private int x, y;
    private boolean finished = false;


    public Explosion(int x, int y) {
        this.x = x;
        this.y = y;
        loadFrames();
        lastFrameTime = System.currentTimeMillis();
    }

    private void loadFrames() {
        frames = new BufferedImage[7];
        for (int i = 1; i < 6; i++) {
            try {
                frames[i] = ImageIO.read(new File("res/explosion_lg_000" + (i + 1) + ".png"));
            } catch (IOException e) {
                System.err.println("Failed to load explosion frame " + i);
            }
        }
    }

    public void update() {
        if (finished) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime >= frameDuration) {
            currentFrame++;
            lastFrameTime = currentTime;
            if (currentFrame >= frames.length) {
                finished = true;
            }
        }
    }

    public void draw(Graphics2D g2) {
        if (!finished && currentFrame < frames.length) {
            g2.drawImage(frames[currentFrame], x - 32, y - 32, 64, 64, null);
        }
    }

    public boolean isFinished() {
        return finished;
    }

}