import javax.swing.JPanel;
import java.awt.event.*;
import java.awt.*;

public class Canvas extends JPanel implements Runnable {
    static final int WIDTH = 500;
    static final int HEIGHT = 500;
    static final Dimension dimensions = new Dimension(WIDTH, HEIGHT); 
    static final int PLAYER_WITDH = 40;
    static final int PLAYER_HEIGHT = 40;
    Image image;
    Board board = new Board(WIDTH, HEIGHT);
    Graphics graphics;
    Thread gameThread;


    Canvas() {
        this.setFocusable(true);
        this.addMouseListener(new AL());
        this.addMouseMotionListener(new LA());
        this.setPreferredSize(dimensions);

        gameThread = new Thread(this);
        gameThread.start();
    }

    public void draw(Graphics g) {
        board.draw(g);
    }

    public void paint(Graphics g) {
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image, 0, 0, this);
        //draw(g);
    }

    public void run() {
        
        long lastTime = System.nanoTime();
        double amountOfTicks = 10000000.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
         
        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta < 1) continue;
            delta--;
            
            repaint();
        }
    }

    public class AL extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            board.checkPieces(e.getX(), e.getY());
            e.consume();
        }  

        @Override
        public void mouseReleased(MouseEvent e) {
            if (board.held_piece_index == -1) {
                e.consume();
                return;
            }
            board.checkBoxes(e.getX(), e.getY());
            e.consume();
        }
    }

    public class LA extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            if (board.held_piece_index == -1) {
                e.consume();
                return;
            }
            board.pieces[board.held_piece_index].move(e.getX(), e.getY());
            e.consume();
        }
    }
}
