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

    public List<Complex> discreteFT(List<Complex> fdata, int N, boolean fwd) {
        List<Complex> resultList = new ArrayList<>(N);
        double omega;
        int k, n;
        if (fwd) {
            omega = 2.0 * Math.PI / N;
        } else {
            omega = -2.0 * Math.PI / N;
        }
        for (k = 0; k < N; k++) {
            Complex curr = new Complex(0.0, 0.0);
            for (n = 0; n < N; ++n) {
                double first = fdata.get(n).getReal() * Math.cos(omega * n * k) +
                        fdata.get(n).getImaginary() * Math.sin(omega * n * k);
                double second = -fdata.get(n).getReal() * Math.sin(omega * n * k) +
                        fdata.get(n).getImaginary() * Math.cos(omega * n * k);
                curr.add(new Complex(
                        fdata.get(n).getReal() * Math.cos(omega * n * k) +
                                fdata.get(n).getImaginary() * Math.sin(omega * n * k),
                        -fdata.get(n).getReal() * Math.sin(omega * n * k) +
                                fdata.get(n).getImaginary() * Math.cos(omega * n * k)
                ));
            }
            resultList.add(curr);
        }
        if (fwd) {
            for (k = 0; k < N; ++k) {
                resultList.set(k, resultList.get(k).divide(N));
            }
        }
        return resultList;
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

    private void printValuesToConsoleAndMakeFrame(List<Double> x, List<Double> y, String title, String lowLineTitle, String highLineTitle) {
        FirstApplicationFrame frame = new FirstApplicationFrame(title, x, y, lowLineTitle, highLineTitle);
        frame.showFrame();
    }

    public void execute() {
        List<Double> beforePointsX = segmentSpliterator(a1, a2, N);
        List<Complex> beforePointsY = beforePointsX.stream().map(val -> new Complex(gaussBundle(val), 0d)).collect(Collectors.toList());
        printValuesToConsoleAndMakeFrame(beforePointsX, beforePointsY.stream()
                .map(Complex::getArgument)
                .collect(Collectors.toList()), "PHASE_INC", "X", "Y");
        printValuesToConsoleAndMakeFrame(beforePointsX, beforePointsY.stream()
                .map(Complex::abs)
                .collect(Collectors.toList()), "AMPLI_INC", "X", "Y");

        beforePointsY = transferListSides(addZerosToListToSize(beforePointsY, M));
        Complex[] res = new Complex[M];
        for (int i = 0; i < M; ++i) {
            res[i] = beforePointsY.get(i);
        }
        FastFourierTransformer fastFourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);
        res = fastFourierTransformer.transform(res, TransformType.FORWARD);
        List<Complex> resultListAfterFFT = Arrays.stream(res).sequential().map(val -> val.multiply(step)).collect(Collectors.toList());
        resultListAfterFFT = transferListSides(resultListAfterFFT).subList((M - N) / 2, (M + N) / 2);
        List<Double> afterXPoints = segmentSpliterator(b1, b2, N);
        printValuesToConsoleAndMakeFrame(afterXPoints, resultListAfterFFT.stream()
                .map(Complex::getArgument)
                .collect(Collectors.toList()), "PHASE_FFT", "X", "Y");
        printValuesToConsoleAndMakeFrame(afterXPoints, resultListAfterFFT.stream()
                .map(Complex::abs)
                .collect(Collectors.toList()), "AMPLI_FFT", "X", "Y");
        DoubleFFT_1D discreteFourierTransform = new DoubleFFT_1D(M);
        double[] resultArray = new double[M * 2];
        for (int i = 0; i < M; i += 1) {
            resultArray[i * 2] = beforePointsY.get(i).getReal();
            resultArray[i * 2 + 1] = beforePointsY.get(i).getImaginary();
        }
        discreteFourierTransform.complexForward(resultArray);
        List<Complex> resultListAfterDFT = new ArrayList<>();
        for (int i = 0; i < M; i += 1) {
            resultListAfterDFT.add(new Complex(resultArray[i * 2], resultArray[i * 2 + 1]).multiply(step));
        }
        resultListAfterDFT = transferListSides(resultListAfterDFT).subList((M - N) / 2, (M + N) / 2);
        printValuesToConsoleAndMakeFrame(afterXPoints, resultListAfterDFT.stream()
                .map(Complex::getArgument)
                .collect(Collectors.toList()), "PHASE_DFT", "X", "Y");
        printValuesToConsoleAndMakeFrame(afterXPoints, resultListAfterDFT.stream()
                .map(Complex::abs)
                .collect(Collectors.toList()), "AMPLI_DFT", "X", "Y");
    }

}
