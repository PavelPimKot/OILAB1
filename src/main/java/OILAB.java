public interface OILAB {
    void execute();

    static void printDoubleForExel(double value) {
        System.out.println(Double.toString(value).replace('.', ','));
    }
}
