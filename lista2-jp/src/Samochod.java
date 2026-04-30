import java.util.Random;

public class Samochod {

    private ZbiorMarek marka; //enum ZbiorMarek
    private double cena;
    private int rocznik;

    public Samochod(Random random) { //
        this.marka = ZbiorMarek.losowanieMarki(random);

        int minRocznik = this.marka.getMinRocznik();
        int maxRocznik = this.marka.getMaxRocznik();
        this.rocznik = random.nextInt(maxRocznik - minRocznik + 1) + minRocznik; // losowanie rocznika na liczbach calkowitych

        double srednia = this.marka.getSredniaCena();
        double odchylenie = this.marka.getOdchylenieCeny();
        this.cena = (random.nextGaussian() * odchylenie) + srednia; //odchylenie standardowe
    }

    @Override
    public String toString() {
        return "Marka: " + marka + ", Cena: " + String.format("%.2f", cena) + "zl " + "Rocznik:" + rocznik;
    }
    //ucielam formatowaniem cene do 2 znakow, poniewaz bez niego wyszla bardzo dluga liczba i bylo to nieczytelne

    public int getRocznik() {
        return rocznik;
    }
}