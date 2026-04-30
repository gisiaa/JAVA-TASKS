import java.io.IOException;

import java.math.BigInteger; //import BigInteger

public class zadanie_1 {

    public static BigInteger count_of_primes(long bound) {
        BigInteger result = BigInteger.valueOf(-1); //begging of equation
        // sigma j = 3 do n
        for (long j = 3; j <= bound; j++) {
            long f = j - 2; // wartość wyrażenia wewnątrz silni ()!

            BigInteger big_j = BigInteger.valueOf(j); // zamiana j na liczbę BigInteger
            BigInteger big_f = factorial(f); // zapis wyrażenia (j - 2)!, jako zmienna f zamieniona na liczbę BigInteger

            // wartość wyrażenia z podłogą
            BigInteger expression_with_floor = big_f.divide(big_j); //
            // wartość mnożenia j * [wyrażenie z podgłogą]
            BigInteger expression_with_multiplying = big_j.multiply(expression_with_floor);
            // wartość odejmowania (j-2)! z resztą wyrażenia, którą pomnożyłam
            BigInteger expression_with_subtraction = big_f.subtract(expression_with_multiplying);
            // Dodawanie zgodnie z definicją sumy (znaku sigma) uzyskanego wyniku, do zmiennej 'result'
            result = result.add(expression_with_subtraction); // użyłam .add zgodnie z dokumentacją oracle
        }
        return result; // zwrócenie wyniku sumowania (znaku sigma)
    }

    public static BigInteger factorial(long number_f) { // silnia dla wyrażenia (j-2)!
        BigInteger result_factorial = BigInteger.ONE; // 1

        for (long f = number_f; f > 0; f--) {
            result_factorial = result_factorial.multiply(BigInteger.valueOf(f));
        }
        return result_factorial;
    }

    // przeliczam jedynie wartość (n-2)!, ponieważ
    // jeśli przekroczy Long.MAX_VALUE lub Integer.MAX_VALUE
    // to dojdzie do przekroczenia zakresu na samym początku wyrażenia

    public static long max_n_for_int() {
        BigInteger max_int_value = BigInteger.valueOf(Integer.MAX_VALUE);
        long n = 3; // sigma od 3 do zadanego n
        BigInteger factorial = BigInteger.ONE;

        while (true) {
            n++;
            long f = n - 2;

            BigInteger f_value_after_iterations = factorial.multiply(BigInteger.valueOf(f));

            if (f_value_after_iterations.compareTo(max_int_value) > 0) {
                return n - 1; // zwracam wartość o -1 mniejszą, bo n byłoby już poza zakresem
            }
            factorial = f_value_after_iterations;
        }
    }

    // analogiczna funkcja, tylko dla wartości typu long
    public static long max_n_for_long() {
        BigInteger max_long_value = BigInteger.valueOf(Long.MAX_VALUE);
        long n = 3;
        BigInteger factorial = BigInteger.ONE;
        while (true) {
            n++;
            long f = n - 2;

            BigInteger f_value_after_iterations = factorial.multiply(BigInteger.valueOf(f));

            if (f_value_after_iterations.compareTo(max_long_value) > 0) {
                return n - 1;
            }
            factorial = f_value_after_iterations;
        }
    }

    public static void main(String[] args) throws IOException {
        // obsługa błędów użytkownika

        // uruchomienie bez parametru wejściowego n
        if (args.length == 0) {
            throw new IOException("ERROR: There's no n parameter.");
        }

        // wczytanie wartości n od użytkownika
        String n = args[0];
        long number;

            if (n.equals("-1")) {
                System.out.println("Max n for int: " + max_n_for_int());
                System.out.println("Max n for long: " + max_n_for_long());
                return; //by zakończyć
            }
        try {
            number = Long.parseLong(n); // zamiana wartości typu String na wartość typu Long i sprawdzenie zakresu
        } catch (NumberFormatException e) {
            throw new IOException("ERROR: The number is not an integer or the number is out of long range");
        }
        // wartość musi być większa niż 3
        if (number <= 3) {
            throw new IOException("ERROR: The number must be greater than 3");
        }

        BigInteger result = count_of_primes(number);
        System.out.println(result.toString());
    }
}