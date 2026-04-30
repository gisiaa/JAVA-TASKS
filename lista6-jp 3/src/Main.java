import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends JFrame {

    static final int TARCZA_MIN_X = 400;
    static final int TARCZA_MAX_X = 600;
    static final int TARCZA_MIN_Y = 400;
    static final int TARCZA_MAX_Y = 600;

    static final AtomicInteger autaNaTarczy = new AtomicInteger(0);

    public static void wjechalNaTarcze() {
        autaNaTarczy.incrementAndGet();
    }

    public static void wyjechalZTarczy() {
        int v = autaNaTarczy.decrementAndGet();
        if (v < 0) {
            autaNaTarczy.set(0);
        }
    }

    public static boolean tarczaPusta() {
        return autaNaTarczy.get() == 0;
    }

    static JFrame frame; // glowne okno
    static PanelSymulacji pRysowanie; // panel z droga i autami
    static JPanel pDolny;
    static JSlider suwakPredkosci; // suwak by przyspieszac/ spowalniac - dodalam dla testow
    static JLabel lPredkosc;

    // wymiary okna i mapy
    static final int SZEROKOSC_OKNA = 1350;
    static final int WYSOKOSC_OKNA = 1000;

    static final int SZEROKOSC_MAPY = 1000;
    static final int WYSOKOSC_MAPY = 1000;

    // lista wszystkich pasow na skrzyzowaniu
    static List<PasRuchu> listaPasow = new ArrayList<>();

    //zmienne
    static double predkosc = 1.0;
    static long czasRozpoczecia;
    static int[] licznikiWlotow = new int[4]; // licznik aut dla kazdego wlotu

    // statystyki
    static long sumaCzasowOczekiwania = 0;
    static long ileAutPrzejechaloLacznie = 0;

    public static void main(String[] args) {
        createWindow();
    }

    private static void createWindow() {
        frame = new JFrame("SKRZYZOWANIE DROGOWE Z SYGNALIZACJA SWIETLNA");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        czasRozpoczecia = System.currentTimeMillis();
        inicjalizujPasy(); // tworzenie drog

        createUI(frame);

        frame.setSize(SZEROKOSC_OKNA, WYSOKOSC_OKNA);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // czujniki
        for (PasRuchu p : listaPasow) {
            new Thread(new CzujnikZajetosci(p)).start();
        }

        // watki
        new Thread(new SterownikSygnalizacji()).start();
        new Thread(new GenerowanieSamochodow()).start();

        // watek do odswiezania ekranu
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    pRysowanie.repaint();
                    try {
                        Thread.sleep(30);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }).start();
    }

    private static void createUI(final JFrame frame) {
        frame.setLayout(new BorderLayout());

        pRysowanie = new PanelSymulacji();
        frame.add(pRysowanie, BorderLayout.CENTER);

        pDolny = new JPanel(new FlowLayout());
        pDolny.setBackground(Color.DARK_GRAY);

        lPredkosc = new JLabel("Predkosc symulacji: ");
        lPredkosc.setForeground(Color.WHITE);
        pDolny.add(lPredkosc);

        suwakPredkosci = new JSlider(0, 50, 10);
        suwakPredkosci.setBackground(Color.DARK_GRAY);
        suwakPredkosci.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                predkosc = suwakPredkosci.getValue() / 10.0;
            }
        });
        pDolny.add(suwakPredkosci);

        frame.add(pDolny, BorderLayout.SOUTH);
    }

    // statystyki
    public static synchronized void autoZakonczyloTrase(int numerWlotu, long czasStaniaMs) {
        licznikiWlotow[numerWlotu]++;
        sumaCzasowOczekiwania += czasStaniaMs;
        ileAutPrzejechaloLacznie++;
    }

    public static synchronized double obliczSredniCzas() {
        if (ileAutPrzejechaloLacznie == 0) {
            return 0.0;
        }
        return (sumaCzasowOczekiwania / 1000.0) / ileAutPrzejechaloLacznie;
    }

    public static synchronized long pobierzLiczbeAut() {
        return ileAutPrzejechaloLacznie;
    }

    public static double obliczPrzepustowosc() {
        double sekundy = (System.currentTimeMillis() - czasRozpoczecia) / 1000.0;
        if (sekundy <= 0) {
            return 0.0;
        }
        return pobierzLiczbeAut() / sekundy;
    }

    private static void inicjalizujPasy() {
        PasRuchu p;

        // polnoc
        p = new PasRuchu(0, 0, 483, -50, 483, 390);
        p.dodajTrase(new Point(483, 390), new Point(483, 580), new Point(600, 517), new Point(1050, 517), 0);
        p.dodajTrase(new Point(483, 390), new Point(483, 490), new Point(517, 490), new Point(517, -50), 0);
        listaPasow.add(p);

        p = new PasRuchu(0, 1, 450, -50, 450, 390);
        p.dodajTrase(new Point(450, 390), null, null, new Point(450, 1050), 1);
        listaPasow.add(p);

        p = new PasRuchu(0, 2, 417, -50, 417, 390);
        p.dodajTrase(new Point(417, 390), null, null, new Point(417, 1050), 1);
        p.dodajTrase(new Point(417, 390), new Point(417, 417), new Point(450, 417), new Point(-50, 417), 2);
        listaPasow.add(p);

        // poludnie
        p = new PasRuchu(2, 0, 517, 1050, 517, 610);
        p.dodajTrase(new Point(517, 610), new Point(517, 420), new Point(400, 483), new Point(-50, 483), 0);
        listaPasow.add(p);

        p = new PasRuchu(2, 1, 550, 1050, 550, 610);
        p.dodajTrase(new Point(550, 610), null, null, new Point(550, -50), 1);
        listaPasow.add(p);

        p = new PasRuchu(2, 2, 583, 1050, 583, 610);
        p.dodajTrase(new Point(583, 610), null, null, new Point(583, -50), 1);
        p.dodajTrase(new Point(583, 610), new Point(583, 583), new Point(550, 583), new Point(1050, 583), 2);
        listaPasow.add(p);

        // wschod
        p = new PasRuchu(1, 0, 1050, 483, 610, 483);
        p.dodajTrase(new Point(610, 483), new Point(420, 483), new Point(483, 600), new Point(483, 1050), 0);
        listaPasow.add(p);

        p = new PasRuchu(1, 1, 1050, 450, 610, 450);
        p.dodajTrase(new Point(610, 450), null, null, new Point(-50, 450), 1);
        listaPasow.add(p);

        p = new PasRuchu(1, 2, 1050, 417, 610, 417);
        p.dodajTrase(new Point(610, 417), null, null, new Point(-50, 417), 1);
        p.dodajTrase(new Point(610, 417), new Point(583, 417), new Point(583, 450), new Point(583, -50), 2);
        listaPasow.add(p);

        //zachod
        p = new PasRuchu(3, 0, -50, 517, 390, 517);
        p.dodajTrase(new Point(390, 517), new Point(580, 517), new Point(517, 400), new Point(517, -50), 0);
        listaPasow.add(p);

        p = new PasRuchu(3, 1, -50, 550, 390, 550);
        p.dodajTrase(new Point(390, 550), null, null, new Point(1050, 550), 1);
        listaPasow.add(p);

        p = new PasRuchu(3, 2, -50, 583, 390, 583);
        p.dodajTrase(new Point(390, 583), null, null, new Point(1050, 583), 1);
        p.dodajTrase(new Point(390, 583), new Point(417, 583), new Point(417, 550), new Point(417, 1050), 2);
        listaPasow.add(p);
    }
}
