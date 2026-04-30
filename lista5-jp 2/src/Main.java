import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.LinkedList;
import java.awt.event.KeyEvent;

public class Main {

    static JFrame frame; //glowne okno
    static JPanel pRysowanie; //obrazek, prostokat i linie
    static JPanel pInformacji; //informacje - skroty klawiaturowe i historia
    static JPanel pGorny; //dla szerokosci Width:
    static JPanel pLewy; // dla wysokosci Height:

    static JLabel lWidth; // szerokosc obrazka lewo prawo w px
    static wysokosc lHeight; // wysokosc obrazka gora dol w px
    static JPanel pDolny; // wspolrzedna x, y oraz kolor
    static JPanel pKolor; // kwadracik z kolorem

    static JLabel labelW, labelZ, labelK, labelL, labelC, labelQ, labelH, labelX; //skroty klawiaturowe
    static JLabel lWspolrzedne; // zawiera informacje o wspolrzednych

    static JList<Zaznaczenie> listaZaznaczaniaGUI; //GUi dla panelu z historia
    static LinkedList<Zaznaczenie> HistoriaZaznaczania; //lista zaznaczania

    static BufferedImage image; //wczytany obrazek
    static Zaznaczenie wspolrzedneKursora; //zapisuje obszar

    static Status status_aktualny = Status.BEZCZYNNY; // gdy nic nie jest wybrane - linia lub prostokat

    //kadrowanie
    static int wspolrzedneX, wspolrzedneY;
    static int aktualnaX, aktutalnaY, szerokosc, wysokosc;
    static int liniaLewa, liniaPrawa, liniaGorna, liniaDolna;
    static RodzajLinii obecnieWybranaLinia = RodzajLinii.BRAK;

    static JScrollPane scrollPane; //ukrywanie/widocznosc historii

    //wymiary obrazka - nie wiedzialam, czy chodzilo o wymiar obrazka czy zaznaczenia, wiec przyjelam wymiar obrazka
    private static void wymiary(int w, int h) {
        lWidth.setText("Width: " + w);
        lHeight.setText("Height: " + h);
    }

    //dopasowywanie rozmiarem
    private static void dopasuj_okno_do_obrazka() {
        if (image == null) return;

        int lewyW = 0;
        int prawyW = 0;
        int goraH = 0;
        int dolH = 0;
        int prawyH = 0;

        if (pLewy != null) {
            lewyW = pLewy.getPreferredSize().width;
        }
        if (pInformacji != null) {
            prawyW = pInformacji.getPreferredSize().width;
            prawyH = pInformacji.getPreferredSize().height;
        }

        if (pGorny != null) {
            goraH = pGorny.getPreferredSize().height;
        }
        if (pDolny != null) {
            dolH = pDolny.getPreferredSize().height;
        }

        int srodekW = image.getWidth();  // szerokosc
        int srodekH = image.getHeight(); // wysokosc

        int maxSrodekH = srodekH;
        if (prawyH > maxSrodekH) {
            maxSrodekH = prawyH;
        }

        //zeby obrazek sie nie ucinal i poprawnie dopasowywal
        int zapasW = 20;
        int zapasH = 80;

        int totalW = lewyW + srodekW + prawyW + zapasW;
        int totalH = goraH + maxSrodekH + dolH + zapasH;

        frame.setSize(totalW, totalH);
        frame.setLocationRelativeTo(null);
    }


    public static void main(String[] args) {
        createWindow();
    }

    private static void createWindow() {
        frame = new JFrame("KADROWANIE ZDJECIA");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        createUI(frame);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                czy_zapisac_zaznaczenie(true);
                System.exit(0);
            }
        });

        frame.setSize(800 + 320, 450 + 150);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void createUI(final JFrame frame) {
        frame.setLayout(new BorderLayout());

        HistoriaZaznaczania = new LinkedList<>();
        listaZaznaczaniaGUI = new JList<>();
        listaZaznaczaniaGUI.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        pInformacji = new JPanel();
        pInformacji.setLayout(new FlowLayout(FlowLayout.LEFT));

        pInformacji.add(new JLabel("Skroty klawiaturowe dostepne w programie:"));

        labelW = new JLabel(SkrotyKlawiaturowe.W.getObjasnienie());
        pInformacji.add(labelW);
        labelZ = new JLabel(SkrotyKlawiaturowe.Z.getObjasnienie());
        pInformacji.add(labelZ);
        labelK = new JLabel(SkrotyKlawiaturowe.K.getObjasnienie());
        pInformacji.add(labelK);
        labelL = new JLabel(SkrotyKlawiaturowe.L.getObjasnienie());
        pInformacji.add(labelL);
        labelC = new JLabel(SkrotyKlawiaturowe.C.getObjasnienie());
        pInformacji.add(labelC);
        labelQ = new JLabel(SkrotyKlawiaturowe.Q.getObjasnienie());
        pInformacji.add(labelQ);
        labelH = new JLabel(SkrotyKlawiaturowe.H.getObjasnienie());
        pInformacji.add(labelH);
        labelX = new JLabel(SkrotyKlawiaturowe.X.getObjasnienie());
        pInformacji.add(labelX);

        scrollPane = new JScrollPane(listaZaznaczaniaGUI);
        scrollPane.setPreferredSize(new Dimension(280, 200));
        scrollPane.setVisible(false);
        pInformacji.add(scrollPane);

        Dimension rozmiar = pInformacji.getPreferredSize();
        pInformacji.setPreferredSize(new Dimension(300, rozmiar.height));

        frame.getContentPane().add(pInformacji, BorderLayout.EAST);

        pGorny = new JPanel();
        lWidth = new JLabel("Width: 0", SwingConstants.CENTER);
        pGorny.add(lWidth);

        frame.getContentPane().add(pGorny, BorderLayout.NORTH);

        pLewy = new JPanel();
        pLewy.setLayout(new BorderLayout());

        lHeight = new wysokosc("Height: 0");
        lHeight.setPreferredSize(new Dimension(30, 450));
        pLewy.add(lHeight, BorderLayout.CENTER);

        frame.getContentPane().add(pLewy, BorderLayout.WEST);

        pRysowanie = new rysuj_prostokat_linie();
        pRysowanie.setFocusable(true);
        frame.getContentPane().add(pRysowanie, BorderLayout.CENTER);

        pDolny = new JPanel(new FlowLayout(FlowLayout.CENTER));
        lWspolrzedne = new JLabel("Wspolrzedne: ");
        pDolny.add(lWspolrzedne);
        pDolny.add(new JLabel(" Kolor: "));

        pKolor = new JPanel();
        pKolor.setPreferredSize(new Dimension(20, 20));
        pKolor.setBackground(Color.WHITE);
        pDolny.add(pKolor);

        frame.getContentPane().add(pDolny, BorderLayout.SOUTH);

        listeners();
    }

    private static void listeners() {
        //sterowanie w oknie obrazka za pomoca przyciskow
        pRysowanie.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                naciskane_klawisze(e);
            }
        });

        //sterowanie w oknie historii
        listaZaznaczaniaGUI.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                naciskane_klawisze(e);
            }
        });

        pRysowanie.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                listaZaznaczaniaGUI.clearSelection();
                pRysowanie.repaint();
                if (SwingUtilities.isRightMouseButton(e)) {
                    menu_z_przyciskami(e);
                } else {
                    if (image != null) { //zeby nie dalo sie rysowac poza obrazkiem zaznaczenia
                        if (e.getX() < 0 || e.getY() < 0 || e.getX() >= image.getWidth() || e.getY() >= image.getHeight()) {
                            return;
                        }
                    }
                    wciskane_klawisze_myszki(e);
                }
                pRysowanie.requestFocusInWindow();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                puszczenie_myszka(e);
            }
        });

        pRysowanie.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                aktualna_pozycja_kursora_i_kolor_punktu(e);
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                aktualna_pozycja_kursora_i_kolor_punktu(e);
                przeciaganie_myszka(e);
            }
        });

        listaZaznaczaniaGUI.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && !listaZaznaczaniaGUI.isSelectionEmpty()) {
                    Zaznaczenie z = listaZaznaczaniaGUI.getSelectedValue();
                    aktualnaX = z.X;
                    aktutalnaY = z.Y;
                    szerokosc = z.W;
                    wysokosc = z.H;
                    status_aktualny = Status.BEZCZYNNY;
                    pRysowanie.repaint();
                }
            }
        });
    }

    private static void naciskane_klawisze(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == SkrotyKlawiaturowe.W.getLitera()) {
            okno_wyboru_obrazka();
        }
        else if (key == SkrotyKlawiaturowe.K.getLitera()) {
            kadrowanie_za_pomoca_prostokata();
        }
        else if (key == SkrotyKlawiaturowe.L.getLitera()) {
            kadrowanie_za_pomoca_linii();
        }
        else if (key == SkrotyKlawiaturowe.C.getLitera()) {
            status_aktualny = Status.BEZCZYNNY;
            wspolrzedneKursora = null;
            szerokosc = 0;
            wysokosc = 0;
            listaZaznaczaniaGUI.clearSelection();
            pRysowanie.repaint();
        }
        else if (key == SkrotyKlawiaturowe.Z.getLitera()) {
            czy_zapisac_zaznaczenie(false);
        }
        else if (key == SkrotyKlawiaturowe.Q.getLitera()) {
            czy_zapisac_zaznaczenie(true);
            System.exit(0);
        }
        else if (key == SkrotyKlawiaturowe.H.getLitera()) {
            boolean widoczne = !scrollPane.isVisible(); //historia
            scrollPane.setVisible(widoczne);
            frame.revalidate();
            frame.repaint();
        }
        else if (key == SkrotyKlawiaturowe.X.getLitera()) {
            usun_historie();
        }
    }

    public static void okno_wyboru_obrazka() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new ImageFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                image = ImageIO.read(fileChooser.getSelectedFile());
                if (image != null) {

                    szerokosc = 0;
                    wysokosc = 0;
                    wspolrzedneKursora = null;
                    status_aktualny = Status.BEZCZYNNY;

                    HistoriaZaznaczania.clear();
                    listaZaznaczaniaGUI.setListData(new Zaznaczenie[0]);
                    listaZaznaczaniaGUI.clearSelection();

                    wymiary(image.getWidth(), image.getHeight());

                    pRysowanie.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
                    pRysowanie.revalidate();
                    pRysowanie.repaint();

                    lHeight.setPreferredSize(new Dimension(30, image.getHeight()));
                    pLewy.revalidate();
                    pLewy.repaint();

                    dopasuj_okno_do_obrazka();
                }
                else {
                    JOptionPane.showMessageDialog(frame, "Nie udalo sie wczytac pliku z obrazkiem!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Nie udalo sie wczytac pliku");
            }
        }
    }

    public static void kadrowanie_za_pomoca_prostokata() {

        if (image == null) {
            JOptionPane.showMessageDialog(frame,"Nie mozna kadrowac bez wybranego obrazka!");
            return;
        }

        status_aktualny = Status.PROSTOKAT;
        szerokosc = 0;
        wysokosc = 0;
        listaZaznaczaniaGUI.clearSelection();
        pRysowanie.repaint();
    }

    public static void kadrowanie_za_pomoca_linii() {

        if (image == null) {
            JOptionPane.showMessageDialog(frame, "Nie mozna kadrowac bez wybranego obrazka!");
            return;
        }

        status_aktualny = Status.LINIE;
        listaZaznaczaniaGUI.clearSelection();
        if (image != null) {
            liniaLewa = 50;
            liniaPrawa = image.getWidth() - 50;
            liniaGorna = 50;
            liniaDolna = image.getHeight() - 50;
        }
        pRysowanie.repaint();
    }

    public static void aktualna_pozycja_kursora_i_kolor_punktu(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        lWspolrzedne.setText("x: " + x + ", y: " + y);
        if (image != null && x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight()) {
            Color c = new Color(image.getRGB(x, y));
            pKolor.setBackground(c);
        } else {
            pKolor.setBackground(Color.WHITE);
        }
    }

    public static void czy_zapisac_zaznaczenie(boolean Qpytanie) {

        if (image == null) {
            if (!Qpytanie) {
                JOptionPane.showMessageDialog(frame, "Nie mozna zaznaczac bez obrazka!");
            }
            return;
        }

        int x = 0, y = 0, w = 0, h = 0;

        boolean nowe = false; //zeby tylko nowe zaznaczneie trafilo do historii

        if (status_aktualny == Status.PROSTOKAT) {
            x = Math.min(wspolrzedneX, wspolrzedneX + szerokosc);
            y = Math.min(wspolrzedneY, wspolrzedneY + wysokosc);
            w = Math.abs(szerokosc);
            h = Math.abs(wysokosc);

            nowe = true;

        }
        else if (status_aktualny == Status.LINIE) {
            x = liniaLewa;
            y = liniaGorna;
            w = liniaPrawa - liniaLewa;
            h = liniaDolna - liniaGorna;

            nowe = true;

        }
        else if (status_aktualny == Status.BEZCZYNNY && !listaZaznaczaniaGUI.isSelectionEmpty()) {
            x = aktualnaX;
            y = aktutalnaY;
            w = szerokosc;
            h = wysokosc;
        }

        if (w > 0 && h > 0) {
            if (Qpytanie) {
                int decyzja = JOptionPane.showConfirmDialog(frame, "Czy chcesz zapisac zaznaczony obszar?", "UWAGA!", JOptionPane.YES_NO_OPTION);
                if (decyzja == JOptionPane.NO_OPTION || decyzja == JOptionPane.CLOSED_OPTION) return;
            }

            try {
                BufferedImage wycinek = image.getSubimage(x, y, w, h);
                BufferedImage doZapisu = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics g = doZapisu.getGraphics();
                g.drawImage(wycinek, 0, 0, null);
                g.dispose();

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Wybierz miejsce, w ktorym ma zostac zapisany plik:");

                if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {

                    wspolrzedneKursora = new Zaznaczenie(x, y, w, h);

                    File przycietyObrazek = fileChooser.getSelectedFile();
                    String sciezka = przycietyObrazek.getAbsolutePath();

                    if (!sciezka.toLowerCase().endsWith(".jpg")) {
                        przycietyObrazek = new File(sciezka + ".jpg");
                    }
                    ImageIO.write(doZapisu, "jpg", przycietyObrazek);

                    if (nowe) {
                        HistoriaZaznaczania.add(wspolrzedneKursora);
                        Zaznaczenie[] tab = new Zaznaczenie[HistoriaZaznaczania.size()];
                        HistoriaZaznaczania.toArray(tab);
                        listaZaznaczaniaGUI.setListData(tab);
                    }

                    JOptionPane.showMessageDialog(frame, "Przyciety obrazek zostal zapisany!");
                    status_aktualny = Status.BEZCZYNNY;
                    szerokosc = 0;
                    wysokosc = 0;
                    listaZaznaczaniaGUI.clearSelection();
                    pRysowanie.repaint();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "ERROR: " + ex.getMessage());
            }
        }
        else {
            if (!Qpytanie){
                JOptionPane.showMessageDialog(frame, "Nic nie zostalo zaznaczone!");
            }
        }
    }


    public static void usun_historie() {

        int indeksElementyHistoria = listaZaznaczaniaGUI.getSelectionModel().getMinSelectionIndex();

        if (indeksElementyHistoria != -1) {
            HistoriaZaznaczania.remove(indeksElementyHistoria);
            Zaznaczenie[] tab = new Zaznaczenie[HistoriaZaznaczania.size()];
            HistoriaZaznaczania.toArray(tab);
            listaZaznaczaniaGUI.setListData(tab);

            aktualnaX = 0;
            aktutalnaY = 0;
            szerokosc = 0;
            wysokosc = 0;

            listaZaznaczaniaGUI.clearSelection();
            pRysowanie.repaint();
        }
        else {
            JOptionPane.showMessageDialog(frame, "Element do usuniecia nie zostal zaznaczony!");
        }
    }

    private static void wciskane_klawisze_myszki(MouseEvent e) {
        if (image == null) {
            return;
        }

        int poleKlikaniaX = Math.max(0, Math.min(e.getX(), image.getWidth() - 1));
        int poleKlikaniaY = Math.max(0, Math.min(e.getY(), image.getHeight() - 1));

        if (status_aktualny == Status.PROSTOKAT) {
            wspolrzedneX = poleKlikaniaX;
            wspolrzedneY = poleKlikaniaY;
            szerokosc = 0;
            wysokosc = 0;

        } else if (status_aktualny == Status.LINIE) {
            if (Math.abs(poleKlikaniaX - liniaLewa) < 10) obecnieWybranaLinia = RodzajLinii.LEWA;
            else if (Math.abs(poleKlikaniaX - liniaPrawa) < 10) obecnieWybranaLinia = RodzajLinii.PRAWA;
            else if (Math.abs(poleKlikaniaY - liniaGorna) < 10) obecnieWybranaLinia = RodzajLinii.GORNA;
            else if (Math.abs(poleKlikaniaY - liniaDolna) < 10) obecnieWybranaLinia = RodzajLinii.DOLNA;
            else obecnieWybranaLinia = RodzajLinii.BRAK;
        }
    }

    private static void przeciaganie_myszka(MouseEvent e) {

        if (image == null) return;

        int maksMyszX = Math.max(0, Math.min(e.getX(), image.getWidth()));
        int maksMyszY = Math.max(0, Math.min(e.getY(), image.getHeight()));

        if (status_aktualny == Status.PROSTOKAT) {
            szerokosc = maksMyszX - wspolrzedneX;
            wysokosc = maksMyszY - wspolrzedneY;
            pRysowanie.repaint();

        } else if (status_aktualny == Status.LINIE && obecnieWybranaLinia != RodzajLinii.BRAK) {
            int minOdstep = 10;

            if (obecnieWybranaLinia == RodzajLinii.LEWA) {
                liniaLewa = Math.max(0, Math.min(maksMyszX, liniaPrawa - minOdstep));
            }
            if (obecnieWybranaLinia == RodzajLinii.PRAWA) {
                liniaPrawa = Math.min(image.getWidth(), Math.max(maksMyszX, liniaLewa + minOdstep));
            }
            if (obecnieWybranaLinia == RodzajLinii.GORNA) {
                liniaGorna = Math.max(0, Math.min(maksMyszY, liniaDolna - minOdstep));
            }
            if (obecnieWybranaLinia == RodzajLinii.DOLNA) {
                liniaDolna = Math.min(image.getHeight(), Math.max(maksMyszY, liniaGorna + minOdstep));
            }
            pRysowanie.repaint();
        }
    }

    private static void puszczenie_myszka(MouseEvent e) {
        obecnieWybranaLinia = RodzajLinii.BRAK;
    }

    private static void menu_z_przyciskami(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem wczytaj = new JMenuItem(SkrotyKlawiaturowe.W.getObjasnienie());
        wczytaj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okno_wyboru_obrazka();
            }
        });
        menu.add(wczytaj);

        if (image != null) {
            menu.addSeparator();
            JMenuItem prostokat = new JMenuItem(SkrotyKlawiaturowe.K.getObjasnienie());
            prostokat.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    kadrowanie_za_pomoca_prostokata();
                }
            });
            menu.add(prostokat);

            JMenuItem linie = new JMenuItem(SkrotyKlawiaturowe.L.getObjasnienie());
            linie.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    kadrowanie_za_pomoca_linii();
                }
            });
            menu.add(linie);

            menu.addSeparator();
            JMenuItem zapisz = new JMenuItem(SkrotyKlawiaturowe.Z.getObjasnienie());
            zapisz.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    czy_zapisac_zaznaczenie(false);
                }
            });
            menu.add(zapisz);

            JMenuItem czysc = new JMenuItem(SkrotyKlawiaturowe.C.getObjasnienie());
            czysc.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    status_aktualny = Status.BEZCZYNNY;
                    wspolrzedneKursora = null;
                    szerokosc = 0;
                    wysokosc = 0;
                    listaZaznaczaniaGUI.clearSelection();
                    pRysowanie.repaint();
                }
            });
            menu.add(czysc);

            menu.addSeparator();
            JMenuItem historia = new JMenuItem(SkrotyKlawiaturowe.H.getObjasnienie());
            historia.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    boolean widoczne = !scrollPane.isVisible();
                    scrollPane.setVisible(widoczne);
                    frame.revalidate();
                    frame.repaint();
                }
            });
            menu.add(historia);

            JMenuItem usun = new JMenuItem(SkrotyKlawiaturowe.X.getObjasnienie());
            usun.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    usun_historie();
                }
            });
            menu.add(usun);

            menu.addSeparator();
            JMenuItem wyjscie = new JMenuItem(SkrotyKlawiaturowe.Q.getObjasnienie());
            wyjscie.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    czy_zapisac_zaznaczenie(true);
                    System.exit(0);
                }
            });
            menu.add(wyjscie);
        }
        menu.show(pRysowanie, e.getX(), e.getY());
    }

    static class rysuj_prostokat_linie extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            if (image != null) {
                g.drawImage(image, 0, 0, null);
            }

            g2.setStroke(new BasicStroke(3));

            if (status_aktualny == Status.PROSTOKAT) {
                g.setColor(Color.BLACK);

                int klikMyszkiX = wspolrzedneX;
                int klikMyszkiY = wspolrzedneY;
                int aktualnaPozycjaMyszkiX = wspolrzedneX + szerokosc;
                int aktualnaPozycjaMyszkiY = wspolrzedneY + wysokosc;

                int lewyRog = Math.min(klikMyszkiX, aktualnaPozycjaMyszkiX);
                int gora = Math.min(klikMyszkiY, aktualnaPozycjaMyszkiY);
                int prawyRog = Math.max(klikMyszkiX, aktualnaPozycjaMyszkiX);
                int dol = Math.max(klikMyszkiY, aktualnaPozycjaMyszkiY);

                int w = prawyRog - lewyRog;
                int h = dol - gora;

                if (w > 0 && h > 0) {
                    g.drawRect(lewyRog, gora, w - 1, h - 1);
                }
            }
            if (status_aktualny == Status.LINIE) {
                g.setColor(Color.BLACK);
                g.drawLine(liniaLewa, 0, liniaLewa, getHeight());
                g.drawLine(liniaPrawa, 0, liniaPrawa, getHeight());
                g.drawLine(0, liniaGorna, getWidth(), liniaGorna);
                g.drawLine(0, liniaDolna, getWidth(), liniaDolna);
            }
            if (!listaZaznaczaniaGUI.isSelectionEmpty() && status_aktualny == Status.BEZCZYNNY) {
                g.setColor(Color.BLACK);
                if (szerokosc > 0 && wysokosc > 0) {
                    g.drawRect(aktualnaX, aktutalnaY, szerokosc - 1, wysokosc - 1);
                }
            }
        }
    }

    static class wysokosc extends JLabel {
        public wysokosc(String text) {
            super(text);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setFont(getFont());
            g2.setColor(getForeground());

            int srodekPaska = getHeight() / 2;
            int x = srodekPaska - 30;

            int y = 15;

            g2.rotate(-Math.PI / 2);
            g2.translate(-getHeight(), 0);

            g2.drawString(getText(), x, y);
        }
    }
}
