package nsu.shserg.util;

import java.util.function.BiFunction;

public class FunctionModel {
    private BiFunction<Double,Double,Double> function;
    private Settings settings;
    private double[] isolinesValues;
    private Double minFunction;
    private Double maxFunction;

    public FunctionModel(BiFunction<Double,Double,Double> function, Settings settings){
        this.function = function;
        this.settings = settings;
    }

    public double getMinimumFunction() {
        if (minFunction != null) {
            return minFunction;
        }
        minFunction = function.apply((double) settings.getA(), (double) settings.getC());

        for (int x = settings.getA(); x <= settings.getB(); x++) {
            for (int y = settings.getC(); y <= settings.getD(); y++) {
                double value = function.apply((double)x, (double)y);
                minFunction = (value < minFunction) ? value : minFunction;
            }
        }
        return minFunction;
    }

    public double getMaximumFunction() {
        if (maxFunction != null) {
            return maxFunction;
        }
        maxFunction = function.apply((double) settings.getA(), (double) settings.getC());

        for (int x = settings.getA(); x <= settings.getB(); x++) {
            for (int y = settings.getC(); y <= settings.getD(); y++) {
                double value = function.apply((double)x, (double)y);
                maxFunction = (value > maxFunction) ? value : maxFunction;
            }
        }
        return maxFunction;
    }

    public BiFunction<Double,Double,Double> getFunction(){
        return function;
    }

    public Settings getSettings() {
        return settings;
    }

    public double[] getIsolinesValues() {
        if (isolinesValues != null) {
            return isolinesValues;
        }

        int k = settings.getK();
        isolinesValues = new double[k];
        double step = (getMaximumFunction() - getMinimumFunction()) / (k + 1);
        double min = getMinimumFunction();

        for (int i = 0; i < k; i++) {
            isolinesValues[i] = min + (i + 1) * step;
        }

        return isolinesValues;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
        isolinesValues = null;
        maxFunction = null;
        minFunction = null;
        getIsolinesValues();
    }

    public int getColorForValue(double value) {
        int[] colors = settings.getColors();
        int k = settings.getK();
        if (isolinesValues == null){
            getIsolinesValues();
        }
        for (int i = 0; i < k; i++) {
            if (value <= isolinesValues[i]) {
                return colors[i];
            }
        }
        return colors[k];
    }

    public int getInterpolatedColorForValue(double value) {
        int[] colors = settings.getColors();
        double min = getMinimumFunction();
        double width = isolinesValues[0] - min;

        int index = (int)((value - min - width / 2) / width);

        if (value - min < width / 2) {
            return colors[0];
        }

        if (getMaximumFunction() - value < width / 2) {
            return colors[colors.length - 1];
        }

        double v1 = min + width / 2 + index * width;
        double v2 = min + width / 2 + (index + 1) * width;
        return interpolate(colors[index], colors[index + 1], v1, v2, value);
    }

    private int interpolate(int c1, int c2, double v1, double v2, double v) {
        int red = (int) (getRed(c1) * (v2 - v) / (v2 - v1) + getRed(c2) * (v - v1) / (v2 - v1));
        int green = (int) (getGreen(c1) * (v2 - v) / (v2 - v1) + getGreen(c2) * (v - v1) / (v2 - v1));
        int blue = (int) (getBlue(c1) * (v2 - v) / (v2 - v1) + getBlue(c2) * (v - v1) / (v2 - v1));

        return red<<16|green<<8|blue;
    }

    private int getRed(int rgb) {
        return (rgb >> 16) & 0x000000FF;
    }

    private int getGreen(int rgb) {
        return (rgb >> 8) & 0x000000FF;
    }

    private int getBlue(int rgb) {
        return (rgb) & 0x000000FF;
    }

}
