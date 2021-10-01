import org.jtransforms.fft.DoubleFFT_1D;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class OISecondLab14 implements OILAB {
    public static final double a1 = -5;
    public static final double a2 = 5;
    public static final int M = 2048;
    public static final int N = 200;
    public static final double b1 = Math.pow(N, 2) / 4 * a1 * M;
    public static final double b2 = b1 * -1;
    public static double step;


    private double f(double x) {
        return 1 / (4 + Math.pow(x, 2));
    }

    private double gaussBundle(double x) {
        return Math.exp(-Math.pow(x, 2));
    }

    private List<Double> segmentSpliterator(double pointFrom, double pointTo, int segmentCount) {
        List<Double> pointsList = new ArrayList<>();
        --segmentCount;
        step = (pointTo - pointFrom) / segmentCount;
        double value = pointFrom;
        for (int i = 0; i < segmentCount + 1; ++i) {
            pointsList.add(value);
            value += step;
        }
        return pointsList;
    }

    private List<Double> addZerosToListToSize(List<Double> list, int size) {
        int zeroCount = size - list.size();
        for (int i = 0; i < zeroCount; i += 2) {
            list.add(0d);
            list.add(0, 0d);
        }
        return list;
    }

    public List<Double> transferListSides(List<Double> list) {
        int listMiddle = list.size() / 2;
        List<Double> resultList = new ArrayList<>(list.subList(listMiddle, list.size()));
        resultList.addAll(list.subList(0, listMiddle));
        return resultList;
    }

    private void printValuesToConsoleAndMakeFrame(List<Double> x, List<Double> y, String title, String lowLineTitle, String highLineTitle) {
        System.out.println();
        System.out.println(title);
        y.forEach(OILAB::printDoubleForExel);
        System.out.println();
        FirstApplicationFrame frame = new FirstApplicationFrame(title, x, y, lowLineTitle, highLineTitle);
        frame.showFrame();
    }

    public void execute() {
        List<Double> beforePointsY = segmentSpliterator(a1, a2, N);
        List<Double> beforePointsX = beforePointsY.stream().map(this::gaussBundle).collect(Collectors.toList());
        printValuesToConsoleAndMakeFrame(beforePointsY, beforePointsX, "GAUSS", "X", "Y");
        printValuesToConsoleAndMakeFrame(beforePointsY, beforePointsX.stream().map(val -> 0d).collect(Collectors.toList()), "GAUSS", "X", "Y");

        List<Double> resultListBeforeFFT = transferListSides(addZerosToListToSize(beforePointsX, M));
        double[] resultArray = resultListBeforeFFT.stream().mapToDouble(Double::doubleValue).toArray();
        DoubleFFT_1D FFT = new DoubleFFT_1D(M);
        FFT.realForward(resultArray);
        List<Double> resultListAfterFFT = DoubleStream.of(resultArray).boxed().collect(Collectors.toList());
        resultListAfterFFT = resultListAfterFFT.stream().map(val -> val * step).collect(Collectors.toList());
        resultListAfterFFT = transferListSides(resultListAfterFFT).subList((M - N) / 2, M - (M - N) / 2);
        resultListAfterFFT.forEach(System.out::println);
    }

}
