public class ComplexNumber {

    /**
     * Represents the real part of a complex number
     */
    private double re;

    /**
     * Represents imaginary part of a complex number
     */
    private double im;

    public static final ComplexNumber I = new ComplexNumber(0, 1);

    public ComplexNumber(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public double getRe() {
        return re;
    }

    public double getIm() {
        return im;
    }

    /**
     * @return modulus (or absolute value) of the number
     */
    public double getModule() {
        return Math.sqrt(this.re * this.re + this.im * this.im);
    }

    /**
     * Allows to get the sum of two complex numbers given in the parameters.
     *
     * @return the new complex number
     */
    public static ComplexNumber sum(ComplexNumber cn1, ComplexNumber cn2) {
        return new ComplexNumber(cn1.getRe() + cn2.getRe(), cn1.getIm() + cn2.getIm());
    }

    /**
     * Allows to get the product of two complex numbers given in the parameters.
     *
     * @return the new complex number
     */
    public static ComplexNumber multiply(ComplexNumber cn1, ComplexNumber cn2) {
        return new ComplexNumber(cn1.getRe() * cn2.getRe() - cn1.getIm() * cn2.getIm(), cn1.getRe() * cn2.getIm() + cn1.getIm() * cn2.getRe());
    }

    /**
     * Allows to get the difference of two complex numbers given in the parameters.
     *
     * @return the new complex number
     */
    public static ComplexNumber subtract(ComplexNumber cn1, ComplexNumber cn2) {
        return new ComplexNumber(cn1.getRe() - cn2.getRe(), cn1.getIm() - cn2.getIm());
    }

    /**
     * Allows to get the product of two complex numbers given in the parameters.
     *
     * @return the new complex number
     */
    public static ComplexNumber divide(ComplexNumber cn1, ComplexNumber cn2) {
        ComplexNumber temp = new ComplexNumber(cn2.getRe(), (-1) * cn2.getIm());
        temp = ComplexNumber.multiply(cn1, temp);
        double denominator = cn2.getRe() * cn2.getRe() + cn2.getIm() * cn2.getIm();
        return new ComplexNumber(temp.getRe() / denominator, temp.getIm() / denominator);
    }

    /**
     * This function allows to get the argument of complex number to represent it in trigonometric form
     *
     * @return argument of complex number
     */
    public double getArg() {
        if (this.re > 0) {
            return Math.atan(im / re);
        } else {
            if (re < 0 && im > 0) {
                return Math.PI + Math.atan(im / re);
            } else {
                return -Math.PI + Math.atan(im / re);
            }
        }
    }

    /**
     * Allows to raise complex number to specified power with the help of de Moivre's formula.
     *
     * @param cn    needed complex number (the base)
     * @param power the exponent
     * @return the new complex number
     */
    public static ComplexNumber pow(ComplexNumber cn, int power) {
        double factor = Math.pow(cn.getModule(), power);
        return new ComplexNumber(factor * Math.cos(power * cn.getArg()), factor * Math.sin(power * cn.getArg()));
    }

    /**
     * The function of getting square roots of complex number cn
     *
     * @return an array of pair of square roots
     */
    public static ComplexNumber[] sqrt(ComplexNumber cn) {
        double a = cn.getModule() / 2;
        ComplexNumber pos = new ComplexNumber(Math.sqrt(a + cn.getRe() / 2), Math.signum(cn.getIm()) * Math.sqrt(a - cn.getRe() / 2));
        ComplexNumber neg = new ComplexNumber((-1) * pos.getRe(), (-1) * pos.getIm());
        ComplexNumber[] answer = {pos, neg};
        return answer;
    }

    /**
     * Defines and returns the sign required for correct record of a number
     *
     * @return string with appropriate sign
     */
    private String sign() {
        if (im > 0) return " + ";
        else return " - ";
    }

    @Override
    public String toString() {
        String string;
        if (im == 1 || im == -1) {
            if (re == 0) {
                string = sign() + "i";
            } else {
                string = re + sign() + "i";
            }
        } else {
            string = re + sign() + Math.abs(im) + "i";
        }
        return string;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass() == obj.getClass() && obj != null;
    }
}