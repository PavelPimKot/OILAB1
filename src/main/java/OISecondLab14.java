import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OISecondLab14 implements OILAB {
    public static final double a1 = -5;
    public static final double a2 = 5;
    public static final int M = 2048;
    public static final int N = 200;
    public static final double b2 = Math.pow(N, 2) / (4 * a2 * M);
    public static final double b1 = b2 * -1;
    public static double step;


    private double f(double x) {
        return 1 / (4 + Math.pow(x, 2));
    }

    private double gaussBundle(double x) {
        return Math.exp(-Math.pow(x, 2));
    }

    private List<Double> segmentSpliterator(double pointFrom, double pointTo, int segmentCount) {
        step = 0;
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

    private List<Complex> addZerosToListToSize(List<Complex> list, int size) {
        int zeroCount = size - list.size();
        for (int i = 0; i < zeroCount; i += 2) {
            list.add(new Complex(0d, 0d));
            list.add(0, new Complex(0d, 0d));
        }
        return list;
    }

    public List<Complex> transferListSides(List<Complex> list) {
        int listMiddle = list.size() / 2;
        List<Complex> resultList = new ArrayList<>(list.subList(listMiddle, list.size()));
        resultList.addAll(list.subList(0, listMiddle));
        return resultList;
    }

    private void printValuesToConsoleAndMakeFrame(List<Double> x, List<Double> y, String title) {
        FirstApplicationFrame frame = new FirstApplicationFrame(title, x, y, "X", "Y");
        frame.showFrame();
    }

    private void printPhaseAndAmplitudeForValue(List<Double> x, List<Complex> y, String functionName) {
        printValuesToConsoleAndMakeFrame(x, y.stream()
                .map(Complex::getArgument)
                .collect(Collectors.toList()), "PHASE_" + functionName);
        printValuesToConsoleAndMakeFrame(x, y.stream()
                .map(Complex::abs)
                .collect(Collectors.toList()), "AMPLI_" + functionName);
    }

    private double[] convertComplexListToDoubleArray(List<Complex> points) {
        double[] resultArray = new double[points.size() * 2];
        for (int i = 0; i < points.size(); i += 1) {
            resultArray[i * 2] = points.get(i).getReal();
            resultArray[i * 2 + 1] = points.get(i).getImaginary();
        }
        return resultArray;
    }

    private List<Complex> convertDoubleArrayToComplexList(double[] array) {
        List<Complex> resultList = new ArrayList<>();
        for (int i = 0; i < M; i += 1) {
            resultList.add(new Complex(array[i * 2], array[i * 2 + 1]));
        }
        return resultList;
    }

    private void oneToSevenTasks1D(String functionName, boolean isGauss) {
        List<Double> beforePointsX = segmentSpliterator(a1, a2, N);
        List<Double> afterXPoints = segmentSpliterator(b1, b2, N);
        List<Complex> beforePointsY = (isGauss) ?
                beforePointsX.stream()
                        .map(val -> new Complex(gaussBundle(val), 0d))
                        .collect(Collectors.toList()) :
                beforePointsX.stream()
                        .map(val -> new Complex(f(val), 0d))
                        .collect(Collectors.toList());
        printPhaseAndAmplitudeForValue(beforePointsX, beforePointsY, "INCOMING_" + functionName);

        beforePointsY = transferListSides(addZerosToListToSize(beforePointsY, M));
        Complex[] res = beforePointsY.toArray(new Complex[0]);
        FastFourierTransformer fastFourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);
        res = fastFourierTransformer.transform(res, TransformType.FORWARD);
        List<Complex> resultListAfterFFT = Arrays.stream(res)
                .sequential()
                .map(val -> val.multiply(step))
                .collect(Collectors.toList());
        resultListAfterFFT = transferListSides(resultListAfterFFT).subList((M - N) / 2, (M + N) / 2);
        printPhaseAndAmplitudeForValue(afterXPoints, resultListAfterFFT, "FFT_RES_" + functionName);

        DoubleFFT_1D discreteFourierTransform = new DoubleFFT_1D(M);
        double[] resultArray = convertComplexListToDoubleArray(beforePointsY);
        discreteFourierTransform.complexForward(resultArray);
        List<Complex> resultListAfterDFT = convertDoubleArrayToComplexList(resultArray).stream()
                .map(val -> val.multiply(step))
                .collect(Collectors.toList());
        resultListAfterDFT = transferListSides(resultListAfterDFT).subList((M - N) / 2, (M + N) / 2);
        printPhaseAndAmplitudeForValue(afterXPoints, resultListAfterDFT, "DFT_RES_" + functionName);

    }


    public void execute() {
        oneToSevenTasks1D("GAUSS", true);
        oneToSevenTasks1D("OWN_FUNCTION", false);
    }

}
