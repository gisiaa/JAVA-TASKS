import java.awt.*;

public class Trasa {
    Point punktStart, punkt1, punkt2, punktKoniec;
    Color kolor;
    int kierunek;

    public Trasa(Point start, Point p1, Point p2, Point pkoniec, int typ) {
        this.punktStart = start;
        this.punkt1 = p1;
        this.punkt2 = p2;
        this.punktKoniec = pkoniec;
        this.kierunek = typ;

        Color baza;
        if (typ == 0) {
            baza = Color.RED; // skrecanie w lewo
        } else if (typ == 1) {
            baza = Color.YELLOW; // jazda prosto
        } else {
            baza = Color.BLUE; // skrecanie w prawo
        }

        this.kolor = new Color(baza.getRed(), baza.getGreen(), baza.getBlue(), 80);
    }
}
