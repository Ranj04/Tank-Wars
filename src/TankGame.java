import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TankGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Tank Wars");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);
        frame.pack(); // Use preferred size of GamePanel (1600x900)
        frame.setLocationRelativeTo(null); // Center window
        frame.setVisible(true);

        // Game loop using Swing Timer
        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.repaint(); // Triggers paintComponent
            }
        });
        timer.start();
    }
}