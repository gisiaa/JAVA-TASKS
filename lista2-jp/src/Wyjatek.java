import java.util.List;

public class Wyjatek extends Exception {

  private List<Samochod> listaWyjatku;

  public Wyjatek(List<Samochod> lista) {
    super();
    this.listaWyjatku = lista;
  }

  public List<Samochod> getlistaWyjatku() {
    return listaWyjatku;
  }
}