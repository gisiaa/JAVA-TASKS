import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PasRuchu {
    int numerWlotu;
    int numerPasa;
    int startX, startY, stopX, stopY;
    boolean czyZielone = false;

    volatile boolean czyKtosCzeka = false;
    volatile double priorytet = 0;

    List<Pojazd> listaPojazdow = new ArrayList<>();
    List<Trasa> dostepneTrasy = new ArrayList<>();

    public PasRuchu(int nrw, int nrp, int startx, int starty, int stopx, int stopy) {
        this.numerWlotu = nrw;
        this.numerPasa = nrp;
        this.startX = startx;
        this.startY = starty;
        this.stopX = stopx;
        this.stopY = stopy;
    }

    public void dodajTrase(Point s, Point p1, Point p2, Point p3, int typ) {
        dostepneTrasy.add(new Trasa(s, p1, p2, p3, typ));
    }

    public synchronized void dodajPojazd(Pojazd p) {
        listaPojazdow.add(p);
    }

    public synchronized void usunPojazd(Pojazd p) {
        listaPojazdow.remove(p);
    }

    public synchronized int ileAut() {
        return listaPojazdow.size();
    }

    public synchronized List<Pojazd> lista() {
        return new ArrayList<>(listaPojazdow);
    }
}
