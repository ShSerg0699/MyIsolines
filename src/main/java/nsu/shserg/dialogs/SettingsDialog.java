package nsu.shserg.dialogs;

import nsu.shserg.MainFrame;
import nsu.shserg.util.Settings;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

public class SettingsDialog extends JPanel {
    private static final int MAX_AC = 1000;
    private static final int MIN_AC = -1000;
    private static final int DEFAULT_AC = -5;
    private static final int MAX_BD = 1000;
    private static final int MIN_BD = -1000;
    private static final int DEFAULT_BD = 5;
    private static final int MAX_NM = 100;
    private static final int MIN_NM = 3;
    private static final int DEFAULT_NM = 30;
    private static final int MAX_K = 20;
    private static final int MIN_K = 5;
    private static final int DEFAULT_K = 6;
    private static final int MAX_COLOR = 255;
    private static final int MIN_COLOR = 0;
    private static final int DEFAULT_COLOR = 0;

    private final JSpinner spinnerA = new JSpinner(new SpinnerNumberModel(DEFAULT_AC, MIN_AC, MAX_AC, 1));
    private final JSpinner spinnerB = new JSpinner(new SpinnerNumberModel(DEFAULT_BD, MIN_BD, MAX_BD, 1));
    private final JSpinner spinnerC = new JSpinner(new SpinnerNumberModel(DEFAULT_AC, MIN_AC, MAX_AC, 1));
    private final JSpinner spinnerD = new JSpinner(new SpinnerNumberModel(DEFAULT_BD, MIN_BD, MAX_BD, 1));
    private final JSpinner spinnerN = new JSpinner(new SpinnerNumberModel(DEFAULT_NM, MIN_NM, MAX_NM, 1));
    private final JSpinner spinnerM = new JSpinner(new SpinnerNumberModel(DEFAULT_NM, MIN_NM, MAX_NM, 1));
    private final JSpinner spinnerK = new JSpinner(new SpinnerNumberModel(DEFAULT_K, MIN_K, MAX_K, 1));
    private final JSpinner spinnerIsolineColorRed = new JSpinner(new SpinnerNumberModel(DEFAULT_COLOR, MIN_COLOR, MAX_COLOR, 1));
    private final JSpinner spinnerIsolineColorGreen = new JSpinner(new SpinnerNumberModel(DEFAULT_COLOR, MIN_COLOR, MAX_COLOR, 1));
    private final JSpinner spinnerIsolineColorBlue = new JSpinner(new SpinnerNumberModel(DEFAULT_COLOR, MIN_COLOR, MAX_COLOR, 1));
    private final MainFrame frame;
    private JPanel colorsPanel = new JPanel();
    private JScrollPane scroll = null;
    private ArrayList<Integer> colors = new ArrayList<>();

    public SettingsDialog(MainFrame frame) {
        this.frame = frame;
        spinnerA.setToolTipText("-1000 to 1000 and a must be strictly less than b");
        spinnerB.setToolTipText("-1000 to 1000 and a must be strictly less than b");
        spinnerC.setToolTipText("-1000 to 1000 and c must be strictly less than d");
        spinnerD.setToolTipText("-1000 to 1000 and c must be strictly less than d");
        spinnerN.setToolTipText("3 to 100");
        spinnerM.setToolTipText("3 to 100");
        spinnerK.setToolTipText("5 to 20");
        spinnerIsolineColorRed.setToolTipText("0 to 255");
        spinnerIsolineColorGreen.setToolTipText("0 to 255");
        spinnerIsolineColorBlue.setToolTipText("0 to 255");

        spinnerK.addChangeListener(e -> changeColorsPanel((int) spinnerK.getValue() + 1));

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(Box.createVerticalStrut(10));

        JPanel panelABCD = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel panelAB = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel panelCD = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelAB.add(spinnerA);
        panelAB.add(spinnerB);
        panelCD.add(spinnerC);
        panelCD.add(spinnerD);
        panelAB.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.BLACK, Color.WHITE),
                "[a,b]", TitledBorder.LEADING, TitledBorder.TOP));
        panelCD.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.BLACK, Color.WHITE),
                "[c,d]", TitledBorder.LEADING, TitledBorder.TOP));
        panelABCD.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.BLACK, Color.WHITE),
                "Definitions region", TitledBorder.LEADING, TitledBorder.TOP));

        panelABCD.add(panelAB);
        panelABCD.add(Box.createHorizontalStrut(10));
        panelABCD.add(panelCD);
        this.add(panelABCD);
        this.add(Box.createVerticalStrut(10));

        JPanel panelNM = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelNM.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.BLACK, Color.WHITE),
                "Grid size", TitledBorder.LEADING, TitledBorder.TOP));
        panelNM.add(new JLabel("N:"));
        panelNM.add(spinnerN);
        panelNM.add(Box.createHorizontalStrut(10));
        panelNM.add(new JLabel("M:"));
        panelNM.add(spinnerM);
        this.add(panelNM);
        this.add(Box.createVerticalStrut(10));

        JPanel panelK = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelK.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.BLACK, Color.WHITE),
                "Number of isoline levels", TitledBorder.LEADING, TitledBorder.TOP));
        panelK.add(new JLabel("K:"));
        panelK.add(spinnerK);
        this.add(panelK);
        this.add(Box.createVerticalStrut(10));

        JPanel panelIsolineColor = addColorsPanel(spinnerIsolineColorRed, spinnerIsolineColorGreen, spinnerIsolineColorBlue, "z");
        panelIsolineColor.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.BLACK, Color.WHITE),
                "Isoline's color", TitledBorder.LEADING, TitledBorder.TOP));
        this.add(panelIsolineColor);
        this.add(Box.createVerticalStrut(10));

        colors.add(150 << 16 | 255);
        colors.add(255);
        colors.add(255 << 8 | 255);
        colors.add(255 << 8);
        colors.add(255 << 16 | 255 << 8);
        colors.add(255 << 16 | 150 << 8);
        colors.add(255 << 16);
        changeColorsPanel(DEFAULT_K + 1);
        this.add(colorsPanel);
    }

    public JPanel addColorsPanel(JSpinner spinnerRed, JSpinner spinnerGreen, JSpinner spinnerBlue, String i) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(new JLabel("C" + i + ": "));
        panel.add(Box.createHorizontalStrut(10));
        panel.add(new JLabel("red:"));
        panel.add(spinnerRed);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(new JLabel("green:"));
        panel.add(spinnerGreen);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(new JLabel("blue:"));
        panel.add(spinnerBlue);
        return panel;
    }

    public int showDialog() {
        return JOptionPane.showConfirmDialog(frame, this, "Settings",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    }

    private void changeColorsPanel(int k) {
        if(scroll != null) {
            this.remove(scroll);
        } else{
            this.remove(colorsPanel);
        }
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (int i = colors.size() - 1;i > k; i--) {
            colors.remove(i);
        }

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.BLACK, Color.WHITE),
                "Colors", TitledBorder.LEADING, TitledBorder.TOP));
        for (int i = 0; i < k; i++) {
            JSpinner spinnerRed, spinnerGreen, spinnerBlue;
            if (i > colors.size() - 1) {
                colors.add(0);
                spinnerRed = new JSpinner(new SpinnerNumberModel(DEFAULT_COLOR, MIN_COLOR, MAX_COLOR, 1));
                spinnerGreen = new JSpinner(new SpinnerNumberModel(DEFAULT_COLOR, MIN_COLOR, MAX_COLOR, 1));
                spinnerBlue = new JSpinner(new SpinnerNumberModel(DEFAULT_COLOR, MIN_COLOR, MAX_COLOR, 1));
            } else {
                spinnerRed = new JSpinner(new SpinnerNumberModel(getRed(colors.get(i)), MIN_COLOR, MAX_COLOR, 1));
                spinnerGreen = new JSpinner(new SpinnerNumberModel(getGreen(colors.get(i)), MIN_COLOR, MAX_COLOR, 1));
                spinnerBlue = new JSpinner(new SpinnerNumberModel(getBlue(colors.get(i)), MIN_COLOR, MAX_COLOR, 1));
            }
            spinnerRed.setToolTipText("0 to 255");
            spinnerGreen.setToolTipText("0 to 255");
            spinnerBlue.setToolTipText("0 to 255");
            final int j = i;
            spinnerRed.addChangeListener(e -> setColorsI(j, (int) spinnerRed.getValue(), (int) spinnerGreen.getValue(), (int) spinnerBlue.getValue()));
            spinnerGreen.addChangeListener(e -> setColorsI(j, (int) spinnerRed.getValue(), (int) spinnerGreen.getValue(), (int) spinnerBlue.getValue()));
            spinnerBlue.addChangeListener(e -> setColorsI(j, (int) spinnerRed.getValue(), (int) spinnerGreen.getValue(), (int) spinnerBlue.getValue()));
            JPanel colorPanel = addColorsPanel(spinnerRed, spinnerGreen, spinnerBlue, Integer.toString(i));

            panel.add(colorPanel);
        }
        colorsPanel = panel;
        scroll = new JScrollPane(colorsPanel);
        if (k > DEFAULT_K + 1) {
            scroll.setPreferredSize(new Dimension(100,235));
            this.add(scroll, "card2");
        }else{
            this.add(colorsPanel);
            scroll = null;
        }
        this.revalidate();
    }

    private void setColorsI(int i, int red, int green, int blue) {
        int rgb = red << 16 | green << 8 | blue;
        colors.set(i, rgb);
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

    public Settings getSettings() {
        if (((int) spinnerA.getValue() >= (int) spinnerB.getValue()) || ((int) spinnerC.getValue() >= (int) spinnerD.getValue())) {
            JOptionPane.showMessageDialog(frame,
                    "<html><h2>Incorrectly value </h2><i>a must be strictly less than b and c must be strictly less than d</i>");
            return null;
        }
        int rgb = (int) spinnerIsolineColorRed.getValue() << 16 | (int) spinnerIsolineColorGreen.getValue() << 8 | (int) spinnerIsolineColorBlue.getValue();
        int[] colors = new int[this.colors.size()];
        for (int i = 0; i < this.colors.size(); i++) {
            if(this.colors.get(i) == rgb){
                JOptionPane.showMessageDialog(frame,
                        "<html><h2>Incorrectly value </h2><i>the color of the contours should not match the colors of the palette</i>");
                return null;
            }
            colors[i] = this.colors.get(i);
        }

        Settings settings = new Settings((int) spinnerA.getValue(), (int) spinnerB.getValue(), (int) spinnerC.getValue(), (int) spinnerD.getValue(),
                (int) spinnerN.getValue(), (int) spinnerM.getValue(), (int) spinnerK.getValue(), colors, rgb);
        return settings;
    }

    public void setSetting(Settings settings){
        spinnerA.setValue(settings.getA());
        spinnerB.setValue(settings.getB());
        spinnerC.setValue(settings.getC());
        spinnerD.setValue(settings.getD());
        spinnerN.setValue(settings.getN());
        spinnerM.setValue(settings.getM());
        spinnerK.setValue(settings.getK());
        spinnerIsolineColorRed.setValue(getRed(settings.getIsolineColor()));
        spinnerIsolineColorBlue.setValue(getBlue(settings.getIsolineColor()));
        spinnerIsolineColorGreen.setValue(getGreen(settings.getIsolineColor()));
        colors = new ArrayList<>();
        for (int i=0; i< settings.getColors().length; i++){
            colors.add(settings.getColors()[i]);
        }
        changeColorsPanel((int) spinnerK.getValue());

    }
}
