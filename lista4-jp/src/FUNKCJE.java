public enum FUNKCJE {
    LINIOWA("Liniowa"), //y = a * x + b
    KWADRATOWA("kwadratowa"), //y = a * x^2 + b * x + c
    Y("y"); //y = a * sin(x - (b * pi)) + c

    private final String wzor;

    FUNKCJE(String wzor) {
        this.wzor = wzor;
    }
}
