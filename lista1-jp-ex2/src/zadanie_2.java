import java.io.IOException;

public class zadanie_2 {

    public static void kolejna(int n_zero) {
        int iteration = 0;
        int n = n_zero;
        int next_n;

        while (n != 1 && iteration < 1000) {

            int n_i = n;
            boolean even;

            if (n % 2 == 0) {
                next_n = n / 2;
                even = true;
            } else {
                next_n = n * 3 + 1;
                even = false;
            }

            if (even) {
                System.out.println(n_i + "," + "parzysta" + "," + next_n);
            }
            else {
                System.out.println(n_i + "," + "nieparzysta" + "," + next_n);
            }

            n = next_n;

            iteration++;
        }
    }

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            throw new IOException("ERROR: There's no n parameter.");
        }

        // wczytanie wartości n od użytkownika
        try {
            int n_zero = Integer.parseInt(args[0]);

            // sprawdzenie, czy n nalezy do liczb naturalnych
            if (n_zero <= 0) {
                System.err.println("ERROR: Number of parameters must be positive (natural number).");
                return;
            }

            kolejna(n_zero);

        } catch (NumberFormatException e) {
            System.err.println("ERROR: Argument is not an integer.");
        }
    }
}