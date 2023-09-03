import java.awt.*;
import javax.swing.ImageIcon;

public class Piece extends Rectangle {
    int[] id;
    char type;
    Moves moves;
    int pieceHeight;
    int boxHeight;
    boolean dead = false;
    boolean isblack;
    Image image;

    Piece(int[] ID, int BOXHEIGHT, int PIECEHEIGHT, char TYPE, boolean ISBLACK) {
        boxHeight = BOXHEIGHT;
        pieceHeight = PIECEHEIGHT;
        update(ID);
        isblack = ISBLACK;
        width = pieceHeight;
        height = pieceHeight;
        type = TYPE;
        String prefix = isblack ? "" : "w";
        String loc = "assets/" + prefix + type + ".png";
        image = new ImageIcon(loc).getImage();
    }

    int[] getXY(int[] ID) {
        int x = ID[1]*boxHeight + ((boxHeight - pieceHeight) / 2);
        int y = ID[0]*boxHeight + ((boxHeight - pieceHeight) / 2);

        int[] newID = {x, y};
        return newID;
    }

    public void draw(Graphics g) {
        if (dead) return;
        g.drawImage(image, x, y, width, height, null);
    }

    public boolean isTarget(int X, int Y) {
        if (dead) return false;
        if (X > x && X < x + width
        && Y > y && Y < y + height) {
            return true;
        }
        return false;
    }

    public void move(int X, int Y) {
        if (dead) return;
        x = X - width / 2;
        y = Y - height / 2;
    }

    public void update(int[] ID) {
        if (dead) return;
        id = ID;
        x = getXY(ID)[0];
        y = getXY(ID)[1];
    }

    public void die() {
        dead = true;
    }

    public void updateMoves(Moves Moves) {
        if (dead) return;
        moves = Moves;
    }
}