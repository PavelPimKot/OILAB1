import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        twoDimensionalCalculate(true);
    }

    private static void twoDimensionalCalculate(boolean isGaussInput) {
        List<Double> xList = linSpace(a1, a2, N);
        List<Double> yList = linSpace(a1, a2, N);
        List<List<Complex>> fList = isGaussInput ?
                calculate2DFunction(xList, yList, OISecondLab14::gauss2) :
                calculate2DFunction(xList, yList, OISecondLab14::f2);
        ExcelWriter.write(ExcelWriter.PHASE, xList, yList, phase2(fList));
        ExcelWriter.write(ExcelWriter.AMPLITUDE, xList, yList, amplitude2(fList));

        //FFT
        fillZeros2(fList, M);
        fList = reverseHalves2(fList);
        List<List<Complex>> FListFft = fastFourierTransform2(fList);
        FListFft = multiply2(FListFft, step);
        FListFft = reverseHalves2(FListFft);
        List<List<Complex>> FListFromCenter = getElementsFromCenter2(FListFft, N);
        ExcelWriter.write(ExcelWriter.FFT_PHASE, xList, yList, phase2(FListFromCenter));
        ExcelWriter.write(ExcelWriter.FFT_AMPLITUDE, xList, yList, amplitude2(FListFromCenter));
    }

    private static List<Double> linSpace(double start, double end, int numPoints) {
        step = (end - start) / (numPoints - 1);
        return IntStream.range(0, numPoints)
                .boxed()
                .map(e -> start + e * step)
                .collect(Collectors.toList());
    }

    private static List<List<Complex>> calculate2DFunction(List<Double> xList, List<Double> yList,
                                                           BiFunction<Double, Double, Double> function) {
        List<List<Complex>> result = new ArrayList<>();
        for (Double y : yList) {
            List<Complex> row = new ArrayList<>();
            for (Double x : xList) {
                row.add(Complex.valueOf(function.apply(x, y)));
            }
            result.add(row);
        }
        return result;
    }

    private static double f2(double x, double y) {
        return (1 / (4 + Math.pow(x, 2))) * (1 / (4 + Math.pow(y, 2)));
    }

    private static double gauss2(double x, double y) {
        return Math.exp(-Math.pow(x, 2) - Math.pow(y, 2));
    }

    private static List<Double> phase(List<Complex> source) {
        return source.stream().map(Complex::getArgument).collect(Collectors.toList());
    }

    private static List<Double> amplitude(List<Complex> source) {
        return source.stream().map(Complex::abs).collect(Collectors.toList());
    }

    private static List<List<Double>> phase2(List<List<Complex>> source) {
        return source.stream().map(OISecondLab14::phase).collect(Collectors.toList());
    }

    private static List<List<Double>> amplitude2(List<List<Complex>> source) {
        return source.stream().map(OISecondLab14::amplitude).collect(Collectors.toList());
    }

    private static void fillZeros(List<Complex> list, int needSize) {
        int zerosSize = needSize - list.size();
        for (int i = 0; i < zerosSize; i += 2) {
            list.add(Complex.ZERO);
            list.add(0, Complex.ZERO);
        }
    }

    private static void fillZeros2(List<List<Complex>> list, int needSize) {
        int zerosSize = needSize - list.size();
        for (List<Complex> row : list) {
            fillZeros(row, needSize);
        }

        List<Complex> zeros = Collections.nCopies(list.size() + zerosSize, Complex.ZERO);
        for (int i = 0; i < zerosSize; i += 2) {
            list.add(new ArrayList<>(zeros));
            list.add(0, new ArrayList<>(zeros));
        }
    }

    private static <T> List<T> reverseHalves(List<T> list) {
        int center = list.size() / 2;
        List<T> firstHalf = list.subList(0, center);
        List<T> secondHalf = list.subList(center, list.size());
        secondHalf.addAll(firstHalf);
        return secondHalf;
    }

    private static List<List<Complex>> reverseHalves2(List<List<Complex>> list) {
        List<List<Complex>> result = new ArrayList<>();
        for (List<Complex> row : reverseHalves(list)) {
            result.add(reverseHalves(row));
        }
        return result;
    }

    private static List<Complex> fastFourierTransform(List<Complex> toTransform) {
        return Arrays.asList(
                new FastFourierTransformer(DftNormalization.STANDARD)
                        .transform(toTransform.toArray(new Complex[0]), TransformType.FORWARD)
        );
    }

    private static List<List<Complex>> fastFourierTransform2(List<List<Complex>> toTransform) {
        List<List<Complex>> result = new ArrayList<>();
        for (List<Complex> row : toTransform) {
            result.add(fastFourierTransform(row));
        }

        result = transpose(result);
        for (int i = 0; i < result.size(); i++) {
            result.set(i, fastFourierTransform(result.get(i)));
        }
        return transpose(result);
    }

    private static List<List<Complex>> transpose(List<List<Complex>> matrix) {
        Complex[][] array = matrix.stream().map(e -> e.toArray(new Complex[0])).toArray(Complex[][]::new);
        Complex[][] transposedArray = MatrixUtils.createFieldMatrix(array).transpose().getData();
        return Arrays.stream(transposedArray)
                .map(Arrays::asList)
                .collect(Collectors.toList());
    }

    private static List<Complex> multiply(List<Complex> source, double multiplier) {
        return source.stream().map(e -> e.multiply(multiplier)).collect(Collectors.toList());
    }

    private static List<List<Complex>> multiply2(List<List<Complex>> source, double multiplier) {
        var result = new ArrayList<List<Complex>>();
        for (List<Complex> row : source) {
            result.add(multiply(row, multiplier));
        }
        return result;
    }

    private static <T> List<T> getElementsFromCenter(List<T> list, int size) {
        int center = list.size() / 2;
        return list.subList(center - (size / 2), center + (size / 2));
    }

    private static <T> List<List<T>> getElementsFromCenter2(List<List<T>> list, int size) {
        var elementsFromCenter = getElementsFromCenter(list, size);
        var result = new ArrayList<List<T>>();
        for (List<T> row : elementsFromCenter) {
            result.add(getElementsFromCenter(row, size));
        }
        return result;
    }
}
