import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OIFirstLab14 implements OILAB {
    private static final double A = -5;
    private static final double B = 5;
    private static final double P = -10;
    private static final double Q = 10;
    private static final int M = 1000;
    private static final int N = 1000;
    private static final double ALPHA = 1;

    /**
     * Эта функция запускает первую лабораторную на выполнение
     */
    @Override
    public void execute() {
        List<Double> segmentedXPoints = segmentSpliterator(A, B, N);
        List<ComplexNumber> incomingSignal = calculateIncomingSignalAndPrintPhaseAndAmpli(segmentedXPoints);
        calculateIntegralAndPrintPhaseAndAmpli(incomingSignal, segmentedXPoints);
    }

    /**
     * @param segmentedXPoints - промежуток [a,b] разбитый на n точек в каждой из которых
     *                         мы считаем значение входного сигнала
     *                         f(x) = exp(ix/10)
     * @return incomingSignal - возвращает лист комплексных значений входного сигнала в каждой точке
     * из входного промежутка segmentedXPoints
     */
    private List<ComplexNumber> calculateIncomingSignalAndPrintPhaseAndAmpli(List<Double> segmentedXPoints) {
        List<ComplexNumber> incomingSignal = new ArrayList<>();
        printDoubleListToConsole("x", segmentedXPoints);
        for (double segmentedPoint : segmentedXPoints) {
            ComplexNumber number = calculateIncomingSignal(new ComplexNumber(segmentedPoint, 0));
            incomingSignal.add(number);
        }
        printValuesToConsoleAndMakeFrame(incomingSignal.stream()
                        .map(ComplexNumber::getArg)
                        .collect(Collectors.toList()),
                segmentedXPoints,
                "Фаза входного сигнала",
                "X",
                "f(x)"
        );
        printValuesToConsoleAndMakeFrame(incomingSignal.stream()
                        .map(ComplexNumber::getModule)
                        .collect(Collectors.toList()),
                segmentedXPoints,
                "Амплитуда входного сигнала",
                "X",
                "f(x)"
        );
        return incomingSignal;
    }

    /**
     * @param x - точка в которой мы считаем значение входного сигналаб комплексная т.к.
     *          все вычисления происходят с комплексными числам.
     * @return возвращает значение входного сигнала в точке
     */
    private ComplexNumber calculateIncomingSignal(ComplexNumber x) {
        ComplexNumber exponentDegree = ComplexNumber.multiply(ComplexNumber.I, x);
        exponentDegree = ComplexNumber.divide(exponentDegree, new ComplexNumber(10, 0));
        ComplexNumber realExp = new ComplexNumber(Math.exp(exponentDegree.getRe()), 0);
        ComplexNumber imagineExp = ComplexNumber.sum(
                new ComplexNumber(Math.cos(exponentDegree.getIm()), 0),
                new ComplexNumber(0, Math.sin(exponentDegree.getIm()))
        );
        return ComplexNumber.multiply(realExp, imagineExp);
    }

    /**
     * В данном методы мы посчитываем выходной сигнал, численно вычисляя интеграл
     *
     * @param incomingSignal - значения входного сигнала в каждой точке из segmentXArray
     * @param segmentXArray  - разбитый на n точек отрезок [a,b]
     */
    private void calculateIntegralAndPrintPhaseAndAmpli(List<ComplexNumber> incomingSignal, List<Double> segmentXArray) {
        double step = (B - A) / M;
        List<Double> segmentedKsiPoints = segmentSpliterator(P, Q, M);
        printDoubleListToConsole("ksi", segmentedKsiPoints);
        List<ComplexNumber> resultList = new ArrayList<>(segmentedKsiPoints.size());
        for (int i = 0; i < segmentedKsiPoints.size(); ++i) {
            resultList.add(new ComplexNumber(0, 0));
        }
        for (int j = 0; j < segmentedKsiPoints.size(); ++j) {
            for (int i = 0; i < segmentXArray.size(); ++i) {
                ComplexNumber promResult = ComplexNumber.multiply(
                        ComplexNumber.multiply(
                                calculateCore(ALPHA, segmentXArray.get(i), segmentedKsiPoints.get(j)),
                                incomingSignal.get(i)
                        )
                        , new ComplexNumber(step, 0)
                );
                resultList.set(j, ComplexNumber.sum(resultList.get(j), promResult));
            }
        }
        printValuesToConsoleAndMakeFrame(resultList.stream()
                        .map(ComplexNumber::getArg)
                        .collect(Collectors.toList()),
                segmentedKsiPoints,
                "Фаза выходного сигнала",
                "KSI",
                "F(X)"
        );
        printValuesToConsoleAndMakeFrame(resultList.stream()
                        .map(ComplexNumber::getModule)
                        .collect(Collectors.toList()),
                segmentedKsiPoints,
                "Амплитуда выходного сигнала",
                "KSI",
                "F(X)"
        );
    }

    /**
     * Функция вычисляет значения K(X, KSI) с заданными параметрами X и KSI
     * K(X, KSI) = i * exp(-alpha * |x - ksi|)
     *
     * @param alpha - параметр использующийся для вычисления основания, задается через константы класса
     * @param x     - точка x
     * @param ksi   - точка ksi
     * @return значение функции K(X, KSI) в точках X и KSI
     */
    private ComplexNumber calculateCore(double alpha, double x, Double ksi) {
        return ComplexNumber.multiply(
                ComplexNumber.I,
                new ComplexNumber(Math.exp(-alpha * Math.abs(x - ksi)), 0)
        );
    }

    /**
     * Функция для разбиения отрезка на заданное количество сегментов
     *
     * @param pointFrom    - точка начала отрезка
     * @param pointTo      - точка конец отрезка
     * @param segmentCount - количество разбиений
     * @return pointsList - лист точек размером segmentCount на [pointFrom, pointTo]
     */
    private List<Double> segmentSpliterator(double pointFrom, double pointTo, int segmentCount) {
        List<Double> pointsList = new ArrayList<>();
        --segmentCount;
        double step = (pointTo - pointFrom) / segmentCount;
        double value = pointFrom;
        for (int i = 0; i < segmentCount + 1; ++i) {
            pointsList.add(value);
            value += step;
        }
        return pointsList;
    }

    /**
     * Функция выводит значения из листа в консоль с заданным названием
     *
     * @param title - название
     * @param list  - лист значений
     */
    private void printDoubleListToConsole(String title, List<Double> list) {
        System.out.println();
        System.out.println(title);
        list.forEach(OILAB::printDoubleForExel);
        System.out.println();
    }

    /**
     * Функция выводит заданные значения параметра y на экран, так же строит график
     * функции по значениям из x, y
     *
     * @param y     - лист значении фукнций
     * @param x     - лист точек исходного интервала
     * @param title - название графика
     */
    private void printValuesToConsoleAndMakeFrame(List<Double> y, List<Double> x, String title, String lowLineTitle, String highLineTitle) {
        System.out.println();
        System.out.println(title);
        y.forEach(OILAB::printDoubleForExel);
        System.out.println();
        FirstApplicationFrame frame = new FirstApplicationFrame(title, x, y, lowLineTitle, highLineTitle);
        frame.showFrame();
    }

}
