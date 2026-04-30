import java.awt.event.KeyEvent;

public enum SkrotyKlawiaturowe {
    W(KeyEvent.VK_W, "W - Wczytaj plik z obrazkiem"),
    Z(KeyEvent.VK_Z, "Z - Zapisz zaznaczony fragment obrazka"),
    K(KeyEvent.VK_K, "K - Kadrowanie za pomoca prostokata"),
    L(KeyEvent.VK_L, "L - Kadrowanie za pomoca czterech linii"),
    C(KeyEvent.VK_C, "C - Powrot do obrazka bez zaznaczenia"),
    Q(KeyEvent.VK_Q, "Q - Wyjscie z programu"),
    H(KeyEvent.VK_H, "H - Historia zaznaczenia - wspolrzedne"),
    X(KeyEvent.VK_X, "X - Usuniecie pozycji z historii");

    private final int literaKlawisza;
    private final String objasnienie;

    SkrotyKlawiaturowe(int literaKlawisza, String objasnienie) {
        this.literaKlawisza = literaKlawisza;
        this.objasnienie = objasnienie;
    }

    public int getLitera() {
        return literaKlawisza;
    }

    public String getObjasnienie() {
        return objasnienie;
    }
}
