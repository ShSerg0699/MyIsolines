package nsu.shserg.util;

import java.awt.*;

public class Settings {
    private int a, b, c, d;
    private int n, m;
    private int k;
    private int[] colors;
    private int isolineColor;

    public Settings(int a, int b, int c, int d,int n, int m, int k, int[] colors, int isolineColor) {
        this.a =a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.n = n;
        this.m = m;
        this.k = k;
        this.colors = colors;
        this.isolineColor = isolineColor;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getC() {
        return c;
    }

    public int getD() {
        return d;
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public int getK() {
        return k;
    }

    public int[] getColors() {
        return colors;
    }

    public int getIsolineColor() {
        return isolineColor;
    }

    public int getWidth() {
        return Math.abs(a - b);
    }

    public int getHeight() {
        return Math.abs(c - d);
    }
}
