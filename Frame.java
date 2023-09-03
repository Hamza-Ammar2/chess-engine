import javax.swing.JFrame;

public class Frame extends JFrame {
    Canvas canvas;

    Frame() {
        canvas = new Canvas();
        this.add(canvas);
        this.setResizable(false);
        this.setVisible(true);
        this.setTitle("Chess");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
    }
}