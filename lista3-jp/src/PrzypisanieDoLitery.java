import java.util.*;

public class PrzypisanieDoLitery {
    char litera; // litera w liscie
    List<Imie> listaImion;

    //konstruktor
    public PrzypisanieDoLitery(char litera) {
        this.litera = litera;
        this.listaImion = new LinkedList<>();
    }

    public void dodanieLubDopisanie(Imiona imie, int indexPliku, int liczbaPlikow) {
        for (Imie wpis : listaImion) {
            if (wpis.getImie() == imie) { //sprawdzenie, czy imie jest juz na liscie
                wpis.dodajWystapienie(indexPliku);
                return;
            }
        }

        Imie imieKtoregoNieBylo = new Imie(imie, liczbaPlikow); //jesli go nie bylo, to tworze
        imieKtoregoNieBylo.dodajWystapienie(indexPliku);

        posortowaneImiona(imieKtoregoNieBylo); //dodaje do listy
    }

    private void posortowaneImiona(Imie imieKtoregoNieBylo) {
        //sortowanie imion (wykorzystalam insert sort)
        for (int i = 0; i < listaImion.size(); i++) {
            String obecneImie = listaImion.get(i).getImie().name(); //imie ktore juz jest na liscie
            String imieKtoregonieByloWczesniej = imieKtoregoNieBylo.getImie().name(); //imie nowe/napotkane w pliku

            if (imieKtoregonieByloWczesniej.compareTo(obecneImie) < 0) { //w ktorym miejscu umiescic
                listaImion.add(i, imieKtoregoNieBylo);
                return;
            }
        }
        listaImion.add(imieKtoregoNieBylo); //imie dodane na sam koniec np moze byc to Zbigniew
    }

    @Override
    public String toString() {
        return litera + ": " + listaImion.toString();
    }
}