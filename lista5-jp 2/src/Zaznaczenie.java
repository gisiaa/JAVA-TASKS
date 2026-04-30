public class Zaznaczenie {

    public int X;
    public int Y;
    public int W;
    public int H;

    public Zaznaczenie(int x, int y, int w, int h) {
        this.X = x;
        this.Y = y;
        this.W = w;
        this.H = h;
    }
    @Override
    public String toString() {
        return String.format("(%d, %d, %d, %d)", X, Y, W, H);
    }
}