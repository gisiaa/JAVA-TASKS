import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Funkcja {
    private FUNKCJE typ;

    private double a;
    private double b;
    private double c;

    private List<Double> wartosciFunkcji = new ArrayList<>(); //k

    private double y_min;
    private double y_max;

    Color kolor;
    private int grubosc;

    //konstruktor
    public Funkcja(FUNKCJE typ, double a, double b, double c, Color kolor, int grubosc) {
        this.typ = typ;
        this.a = a;
        this.b = b;
        this.c = c;
        this.kolor = kolor;
        this.grubosc = grubosc;
    }

    public double WartosciFunkcji(double x_min, double x_max, int k) {

        wartosciFunkcji.clear();

        this.y_min = Double.MAX_VALUE;
        this.y_max = -Double.MAX_VALUE;

        double odstep = (x_max - x_min) / (k - 1);

        double x = x_min;

        for (int i = 0; i < k; i++) {
            double y = 0;

            if (typ == FUNKCJE.LINIOWA) {
                y = a * x + b;
            } else if (typ == FUNKCJE.KWADRATOWA) {
                y = a * Math.pow(x, 2) + b * x + c;
            } else if (typ == FUNKCJE.Y) {
                y = a * Math.sin(x - (b * Math.PI)) + c;
            }

            wartosciFunkcji.add(y);

            if (y < this.y_min) this.y_min = y;
            if (y > this.y_max) this.y_max = y;

            x += odstep;

        }
        return odstep;
    }

    // gettery

    public FUNKCJE getTyp() {
        return typ;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public Color getKolor() {
        return kolor;
    }

    public int getGrubosc() {
        return grubosc;
    }

    /*public double getY_min() {
        return y_min;
    }

    public double getY_max() {
        return y_max;
    }*/

    public List<Double> getWartosciFunkcji() {
        return wartosciFunkcji;
    }

    // settery

    public void setTyp(FUNKCJE typ) {
        this.typ = typ;
    }

    public void setA(double a) {
        this.a = a;
    }

    public void setB(double b) {
        this.b = b;
    }

    public void setC(double c) {
        this.c = c;
    }

    public void setKolor(Color kolor) {
        this.kolor = kolor;
    }

    public void setGrubosc(int grubosc) {
        this.grubosc = grubosc;
    }

    /*public void setWartosciFunkcji(List<Double> list) {
        this.wartosciFunkcji = list;
    }*/

    @Override
    public String toString() {
        return typ + " a = " + a + ", b = " + b + ", c = " + c + ", Kolor = " + solve.pobierzNazweKoloru(kolor) + ", Grubosc = " + grubosc;
    }
}
