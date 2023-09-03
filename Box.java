import java.awt.*;

public class Box extends Rectangle {
    boolean isblack;
    int boxHeight;
    Color color;
    int id[];

    Box(int iid[], int iboxHeight, boolean iisblack) {
        x = iid[1] * iboxHeight;
        y = iid[0] * iboxHeight;
        height = iboxHeight;
        width = iboxHeight;
        
        isblack = iisblack;
        boxHeight = iboxHeight;
        color = isblack ? new Color(165, 42, 42) : Color.WHITE;
        id = iid;
    }

    public void draw(Graphics g) {
        g.setColor(color);

        g.fillRect(x, y, width, height);
    }

    public boolean isTarget(int X, int Y) {
        if (X > x && X < x + width
        && Y > y && Y < y + height) return true;
        return false;
    }
}