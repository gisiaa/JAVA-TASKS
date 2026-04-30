import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class solve extends JFrame implements ListSelectionListener {

    public static void dodajFunkcjeDoListyIwykresu() {
        try {
            double a = Double.parseDouble(tfA.getText());
            double b = Double.parseDouble(tfB.getText());
            double c = Double.parseDouble(tfC.getText());
            double xMin = Double.parseDouble(tfXMin.getText());
            double xMax = Double.parseDouble(tfXMax.getText());
            int k = Integer.parseInt(tfK.getText());

            if (k < 100) {
                JOptionPane.showMessageDialog(f, "ERROR: Wartosc parametru k nie moze byc mniejsza niz 100");
                return;
            }

            FUNKCJE typ = czyWybrana();
            Color kolor = kolory((String) cbKolor.getSelectedItem());
            int grubosc = grubosc();

            Funkcja nowaFun = new Funkcja(typ, a, b, c, kolor, grubosc);
            nowaFun.WartosciFunkcji(xMin, xMax, k);

            model.addElement(nowaFun);
            przeliczWszystkie();
            pWykres.repaint();

            bDodaj.setText("Dodaj");
            ustawStanPol(false);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(f, "ERROR: Podano niepoprawne wartosci");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(f, "ERROR: " + ex.getMessage());
        }
    }

    public static void usunFunkcjeZlistyIformatki() {
        int i = lista.getSelectedIndex();
        if (i != -1) {
            model.remove(i);
            pWykres.repaint();
        }
    }

    public static void zedytujFunkcjeIzaktualizujWykres() {
        Funkcja fun = lista.getSelectedValue();
        if (fun != null) {
            try {
                int k = Integer.parseInt(tfK.getText());

                if (k < 100) {
                    JOptionPane.showMessageDialog(f, "ERROR: Wartosc parametru k nie moze byc mniejsza niz 100");
                    return;
                }

                fun.setA(Double.parseDouble(tfA.getText()));
                fun.setB(Double.parseDouble(tfB.getText()));
                fun.setC(Double.parseDouble(tfC.getText()));
                fun.setTyp(czyWybrana());
                fun.setKolor(kolory((String) cbKolor.getSelectedItem()));
                fun.setGrubosc(grubosc());

                fun.WartosciFunkcji(Double.parseDouble(tfXMin.getText()),
                        Double.parseDouble(tfXMax.getText()), k);

                przeliczWszystkie();

                lista.repaint();
                pWykres.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(f, "ERROR: Podano niepoprawne wartosci");
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(f, "ERROR: " + ex.getMessage());
            }
        }
    }

    public static void zmianaParametrow() {
        try {
            double xMin = Double.parseDouble(tfXMin.getText());
            double xMax = Double.parseDouble(tfXMax.getText());
            int k = Integer.parseInt(tfK.getText());

            if (k < 100) {
                JOptionPane.showMessageDialog(f, "ERROR: Wartosc parametru k nie moze byc mniejsza niz 100");
                return;
            }

            for (int i = 0; i < model.size(); i++) {
                Funkcja f = model.getElementAt(i);
                f.WartosciFunkcji(xMin, xMax, k);
            }
            pWykres.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(f, "ERROR: Podano niepoprawne wartosci");
        }
    }

    static JFrame f;
    static JList<Funkcja> lista;
    static DefaultListModel<Funkcja> model;

    // pole do wpisania wartosci a, b, c, xmin, xmax, k

    static JTextField tfA;
    static JTextField tfB;
    static JTextField tfC;
    static JTextField tfXMin;
    static JTextField tfXMax;
    static JTextField tfK;


    static JRadioButton bLiniowa;
    static JRadioButton bKwadratowa;
    static JRadioButton bY;

    static ButtonGroup bGroup; //zaznaczenie jednego przycisku (by nie dopuscic do sytuacji, ze dwa lub wiecej sa zaznaczone w tym samym czasie)

    //grubosc lini
    static JRadioButton bCienka;
    static JRadioButton bSrednia;
    static JRadioButton bGruba;

    static ButtonGroup bGroupGrubosc;

    static JButton bDodaj;
    static JButton bUsun;
    static JButton bEdytuj;

    static JPanel pWykres;
    static JComboBox<String> cbKolor;

    // kolory mozliwe do wybrania
    private static Color kolory(String nazwaKoloru) {
        switch (nazwaKoloru) {
            case "Czerwony": return Color.RED;
            case "Pomaranczowy": return Color.ORANGE;
            case "Zolty": return Color.YELLOW;
            case "Zielony": return Color.GREEN;
            case "Niebieski": return Color.BLUE;
            case "Rozowy": return Color.MAGENTA;
            default: return Color.BLACK;
        }
    }

    public static String pobierzNazweKoloru(Color kolor) {
        if (kolor.equals(Color.RED)) return "Czerwony";
        if (kolor.equals(Color.ORANGE)) return "Pomaranczowy";
        if (kolor.equals(Color.YELLOW)) return "Zolty";
        if (kolor.equals(Color.GREEN)) return "Zielony";
        if (kolor.equals(Color.BLUE)) return "Niebieski";
        if (kolor.equals(Color.MAGENTA)) return "Rozowy";
        return "";
    }

    //mozliwe grubosci lini
    private static int grubosc() {
        if (bCienka.isSelected()) return 1;
        if (bSrednia.isSelected()) return 3;
        if (bGruba.isSelected()) return 5;
        return 2;
    }

    //informacja, co w danej chwili wybral uzytkownik
    private static FUNKCJE czyWybrana() {
        if (bLiniowa.isSelected()) {
            return FUNKCJE.LINIOWA;
        }
        else if (bKwadratowa.isSelected()) {
            return FUNKCJE.KWADRATOWA;
        }
        else {
            return FUNKCJE.Y;
        }
    }

    private static void przeliczWszystkie() {
        try {
            double xMin = Double.parseDouble(tfXMin.getText());
            double xMax = Double.parseDouble(tfXMax.getText());
            int k = Integer.parseInt(tfK.getText());

            for (int i = 0; i < model.size(); i++) {
                Funkcja f = model.getElementAt(i);
                f.WartosciFunkcji(xMin, xMax, k);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void ustawStanPol(boolean dostepne) {
        tfA.setEnabled(dostepne);
        tfB.setEnabled(dostepne);
        tfC.setEnabled(dostepne);

        /*
        tfXMin.setEnabled(dostepne);
        tfXMax.setEnabled(dostepne);
        tfK.setEnabled(dostepne); */

        bLiniowa.setEnabled(dostepne);
        bKwadratowa.setEnabled(dostepne);
        bY.setEnabled(dostepne);

        bCienka.setEnabled(dostepne);
        bSrednia.setEnabled(dostepne);
        bGruba.setEnabled(dostepne);

        cbKolor.setEnabled(dostepne);
    }

    private static void wyczyscPola() {
        tfA.setText("0.0");
        tfB.setText("0.0");
        tfC.setText("0.0");
    }

    public static void main(String[] args) {

        f = new JFrame("Wykresy funkcji");
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });

        solve s = new solve();
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel pMenu = new JPanel(new GridLayout(0, 1));

        //osie
        JPanel pOsie = new JPanel(new GridLayout(3, 2));
        tfXMin = new JTextField("-10"); tfXMax = new JTextField("10"); tfK = new JTextField("100");
        pOsie.add(new JLabel("Min:")); pOsie.add(tfXMin);
        pOsie.add(new JLabel("Max:")); pOsie.add(tfXMax);
        pOsie.add(new JLabel("K:")); pOsie.add(tfK);
        pMenu.add(pOsie);

        ActionListener zmianaParametrowListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zmianaParametrow();
            }
        };
        tfXMin.addActionListener(zmianaParametrowListener);
        tfXMax.addActionListener(zmianaParametrowListener);
        tfK.addActionListener(zmianaParametrowListener);


        //mozliwosc wybrania funkcji
        JPanel pWyborFunkcji = new JPanel(new GridLayout(0, 1));

        pWyborFunkcji.add(new JLabel("Typy funkcji:", JLabel.CENTER));

        bLiniowa = new JRadioButton("Funkcja liniowa");
        bLiniowa.setSelected(true);
        bKwadratowa = new JRadioButton("Funkcja kwadratowa");
        bY = new JRadioButton("Y");

        bGroup = new ButtonGroup();

        bGroup.add(bLiniowa);
        bGroup.add(bKwadratowa);
        bGroup.add(bY);

        pWyborFunkcji.add(bLiniowa);
        pWyborFunkcji.add(bKwadratowa);
        pWyborFunkcji.add(bY);
        pMenu.add(pWyborFunkcji);

        //dostosowania parametrow funkcji tzn. a, b, c
        JPanel pParametry = new JPanel(new GridLayout(3, 2));
        tfA = new JTextField("1.0"); tfB = new JTextField("0.0"); tfC = new JTextField("0.0");
        pParametry.add(new JLabel("a:", JLabel.LEFT)); pParametry.add(tfA);
        pParametry.add(new JLabel("b:", JLabel.LEFT)); pParametry.add(tfB);
        pParametry.add(new JLabel("c:", JLabel.LEFT)); pParametry.add(tfC);
        pMenu.add(pParametry);

        //kolor lini
        JPanel pKoloru = new JPanel(new FlowLayout(FlowLayout.CENTER));
        String[] kolory = {"Czerwony", "Pomaranczowy", "Zolty", "Zielony", "Niebieski", "Rozowy"};
        cbKolor = new JComboBox<>(kolory);
        cbKolor.setSelectedItem("Czerwony"); // domyslnie ustawilam na czerwony
        pKoloru.add(new JLabel("Kolor:")); pKoloru.add(cbKolor);
        pMenu.add(pKoloru);

        // guzik odpowiadajacy za wybor grubosci lini
        JPanel pGrubosc = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bCienka = new JRadioButton("Cienka");
        bSrednia = new JRadioButton("Srednia");
        bGruba = new JRadioButton("Gruba");

        bSrednia.setSelected(true);

        bGroupGrubosc = new ButtonGroup();

        bGroupGrubosc.add(bCienka);
        bGroupGrubosc.add(bSrednia);
        bGroupGrubosc.add(bGruba);

        pGrubosc.add(new JLabel("Grubosc:"));
        pGrubosc.add(bCienka);
        pGrubosc.add(bSrednia);
        pGrubosc.add(bGruba);
        pMenu.add(pGrubosc);

        model = new DefaultListModel<>();
        lista = new JList<>(model);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.addListSelectionListener(s);
        pMenu.add(new JLabel("Lista funkcji:", JLabel.CENTER));
        pMenu.add(new JScrollPane(lista));

        JPanel pB = new JPanel(new FlowLayout());
        bDodaj = new JButton("Dodaj");
        bEdytuj = new JButton("Edytuj");
        bUsun = new JButton("Usun");

        bEdytuj.setEnabled(false);
        bUsun.setEnabled(false);
        //na poczatku ma dzialac tylko przycisk dodaj

        pB.add(bDodaj);
        pB.add(bEdytuj);
        pB.add(bUsun);

        pMenu.add(pB);

        mainPanel.add(pMenu, BorderLayout.EAST);

        ustawStanPol(false);

        pWykres = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int szerokosc = getWidth();
                int wysokosc = getHeight();
                int odstep_od_bokow = 30;

                g.setColor(Color.WHITE);
                g.fillRect(0, 0, szerokosc, wysokosc);

                double minX = -10, maxX = 10;
                try {
                    minX = Double.parseDouble(tfXMin.getText());
                    maxX = Double.parseDouble(tfXMax.getText());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }

                double minY = -10, maxY = 10;
                if (model.size() > 0) {
                    minY = Double.MAX_VALUE; maxY = -Double.MAX_VALUE;
                    for (int i = 0; i < model.size(); i++) {
                        Funkcja f = model.getElementAt(i);
                        for (Double val : f.getWartosciFunkcji()) {
                            if (val < minY) minY = val;
                            if (val > maxY) maxY = val;
                        }
                    }
                    if (Math.abs(maxY - minY) < 0.00000001) {
                        maxY += 10; minY -= 10;
                    }

                    double odstep = (maxY - minY) * 0.1;
                    maxY += odstep;
                    minY -= odstep;
                }

                double zakresX = maxX - minX;
                double zakresY = maxY - minY;

                if (zakresX == 0) zakresX = 1;
                if (zakresY == 0) zakresY = 1;

                double skalaX = (szerokosc - 2 * odstep_od_bokow) / zakresX;
                double skalaY = (wysokosc - 2 * odstep_od_bokow) / zakresY;

                g.setColor(Color.BLACK);
                int zeroX = odstep_od_bokow + (int) ((0 - minX) * skalaX);
                int zeroY = odstep_od_bokow + (int) ((maxY - 0) * skalaY);

                if (zeroX >= odstep_od_bokow && zeroX <= szerokosc - odstep_od_bokow) {
                    g.drawLine(zeroX, odstep_od_bokow, zeroX, wysokosc - odstep_od_bokow);
                    g.drawLine(zeroX, odstep_od_bokow, zeroX - 5, odstep_od_bokow + 5);
                    g.drawLine(zeroX, odstep_od_bokow, zeroX + 5, odstep_od_bokow + 5);
                    g.drawString("OY", zeroX + 10, odstep_od_bokow + 15);
                }
                if (zeroY >= odstep_od_bokow && zeroY <= wysokosc - odstep_od_bokow) {
                    g.drawLine(odstep_od_bokow, zeroY, szerokosc - odstep_od_bokow, zeroY);
                    int koniecX = szerokosc - odstep_od_bokow;
                    g.drawLine(koniecX, zeroY, koniecX - 5, zeroY - 5);
                    g.drawLine(koniecX, zeroY, koniecX - 5, zeroY + 5);
                    g.drawString("OX", szerokosc - odstep_od_bokow - 25, zeroY - 10);
                }
                if (zeroX >= odstep_od_bokow && zeroX <= szerokosc - odstep_od_bokow && zeroY >= odstep_od_bokow && zeroY <= wysokosc - odstep_od_bokow) {
                    g.drawString("0", zeroX + 5, zeroY + 15);
                }

                g.setColor(Color.BLACK);
                int krok = 1;
                if (zakresX > 50) krok = 5;

                int minX1 = (int) minX;
                int maxX1 = (int) maxX;

                for (int i = minX1; i <= maxX1; i += krok) {
                    if (i == 0) continue;

                    int pikselX = odstep_od_bokow + (int) ((i - minX) * skalaX);

                    if (pikselX >= odstep_od_bokow && pikselX <= szerokosc - odstep_od_bokow) {
                        g.drawLine(pikselX, zeroY - 3, pikselX, zeroY + 3);
                        g.drawString("" + i, pikselX - 5, zeroY + 15);
                    }
                }

                int krokY = 1;
                if (zakresY > 50) krokY = 5;

                int minY1 = (int) minY;
                int maxY1 = (int) maxY;

                for (int j = minY1; j <= maxY1; j += krokY) {
                    if (j == 0) continue;
                    int pikselY = odstep_od_bokow + (int) ((maxY - j) * skalaY);

                    if (pikselY >= odstep_od_bokow && pikselY <= wysokosc - odstep_od_bokow) {
                        g.drawLine(zeroX - 3, pikselY, zeroX + 3, pikselY);
                        g.drawString("" + j, zeroX + 5, pikselY + 5);
                    }
                }

                for (int i = 0; i < model.size(); i++) {
                    Funkcja f = model.getElementAt(i);
                    g.setColor(f.getKolor());

                    Graphics2D g2 = (Graphics2D) g;
                    g2.setStroke(new BasicStroke(f.getGrubosc()));

                    java.util.List<Double> punkty = f.getWartosciFunkcji();
                    int ilosc = punkty.size();
                    if (ilosc < 2) continue;

                    double krokX = (szerokosc - 2 * odstep_od_bokow) / (double)(ilosc - 1);

                    for (int j = 0; j < ilosc - 1; j++) {
                        int x1 = odstep_od_bokow + (int) (j * krokX);
                        int y1 = odstep_od_bokow + (int) ((maxY - punkty.get(j)) * skalaY);
                        int x2 = odstep_od_bokow + (int) ((j + 1) * krokX);
                        int y2 = odstep_od_bokow + (int) ((maxY - punkty.get(j+1)) * skalaY);

                        g2.drawLine(x1, y1, x2, y2);
                    }
                    g2.setStroke(new BasicStroke(1));
                }
            }
        };
        mainPanel.add(pWykres, BorderLayout.CENTER);


        bDodaj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tekst = bDodaj.getText();

                if (tekst.equals("Dodaj")) {
                    ustawStanPol(true);
                    wyczyscPola();
                    lista.clearSelection();//ozdnaczenie listy
                    bDodaj.setText("Rysuj"); //zmiana nazwy z dodaj na rysuj

                } else {
                    dodajFunkcjeDoListyIwykresu();
                }
            }
        });

        bUsun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usunFunkcjeZlistyIformatki();
            }
        });

        bEdytuj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zedytujFunkcjeIzaktualizujWykres();
            }
        });

        f.add(mainPanel);
        f.setSize(900, 700);
        f.setVisible(true);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            Funkcja f = lista.getSelectedValue();

            if (f != null) {
                //zmiana z rysuj na dodaj
                if (bDodaj.getText().equals("Rysuj")) {
                    bDodaj.setText("Dodaj");
                }

                ustawStanPol(true);//zmiana statusu pol

                tfA.setText(String.valueOf(f.getA()));
                tfB.setText(String.valueOf(f.getB()));
                tfC.setText(String.valueOf(f.getC()));

                switch(f.getTyp()) {
                    case LINIOWA:
                        bLiniowa.setSelected(true);
                        break;
                    case KWADRATOWA:
                        bKwadratowa.setSelected(true);
                        break;
                    case Y:
                        bY.setSelected(true);
                        break;
                }
                cbKolor.setSelectedItem(pobierzNazweKoloru(f.getKolor()));
                int g = f.getGrubosc();
                if (g == 1) bCienka.setSelected(true);
                else if (g == 5) bGruba.setSelected(true);
                else bSrednia.setSelected(true);

                bEdytuj.setEnabled(true);
                bUsun.setEnabled(true);
            } else {
                bEdytuj.setEnabled(false);
                bUsun.setEnabled(false);

                if (bDodaj.getText().equals("Dodaj")) {
                    ustawStanPol(false);
                }
            }
        }
    }
}
