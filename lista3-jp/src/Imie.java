import java.util.Arrays;

public class Imie {

    private final Imiona imie;
    private final int[] licznikWystapienImienia;

    public Imie(Imiona imie, int iloscPlikow) {
        this.imie = imie;
        this.licznikWystapienImienia = new int[iloscPlikow]; //dla kazdego pliku liczy wystapienia
    }

    public Imiona getImie() { //getter imienia
        return imie;
    }

    // zwiekszam licznik dla konkretnego pliku, a index jest inkrementowany w main
    public void dodajWystapienie(int indexPliku) {
        if (indexPliku >= 0 && indexPliku < licznikWystapienImienia.length) {
            licznikWystapienImienia[indexPliku]++;
        }
    }

    @Override
    public String toString() {
        return imie.name() + " " + Arrays.toString(licznikWystapienImienia);
    }
}