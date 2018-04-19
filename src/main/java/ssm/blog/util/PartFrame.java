package ssm.blog.util;

public class PartFrame {

    public PartFrame(int x, int y, int with, int height) {
        this.x = x;
        this.y = y;
        this.with = with;
        this.height = height;
    }

    public int x;
    public int y;
    public int with;
    public int height;

    @Override
    public String toString() {
        return "PartFrame{" +
                "x=" + x +
                ", y=" + y +
                ", with=" + with +
                ", height=" + height +
                '}';
    }
}
