package nsu.shserg;

import nsu.shserg.util.FunctionModel;
import nsu.shserg.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class LegendPanel extends JPanel {
    private FunctionModel functionModel;
    private BufferedImage canvas;
    private BufferedImage legendPanel;
    private FunctionModel legendModel;
    private boolean isColorsInterpolate = false;

    public LegendPanel(FunctionModel legendModel, FunctionModel functionModel) {
        this.legendModel = legendModel;
        this.functionModel = functionModel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        updateCanvas();
        g.drawImage(canvas, 0, 0, canvas.getWidth(), canvas.getHeight(), this);
    }


    private void updateCanvas() {
        canvas = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_RGB);
        Graphics canvasGraphics = canvas.getGraphics();
        canvasGraphics.setColor(Color.WHITE);
        canvasGraphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int x = (int) (canvas.getWidth() * (1 - 0.9) * 0.5);
        int y = (int) (canvas.getHeight() * (1 - 0.7));
        Point startPoint = new Point(x, y);

        int legendWidth = (int) (canvas.getWidth() * 0.9);
        int legendHeight = (int) (canvas.getHeight() * 0.7);

        legendPanel = new BufferedImage(legendWidth, legendHeight, BufferedImage.TYPE_INT_RGB);
        drawFunction(legendPanel);
        canvas.getGraphics().drawImage(legendPanel, x, y, legendWidth, legendHeight, this);
        double[] labels = functionModel.getIsolinesValues();
        Graphics g = canvas.getGraphics();
        g.setColor(Color.BLACK);
        int step = legendWidth / (labels.length + 1);
        drawLabel(g, String.format("%.1f", functionModel.getMinimumFunction()), new Point(startPoint.x, startPoint.y - 5));
        for (int i = 0; i < labels.length; i++) {
            String s = String.format("%.1f", labels[i]);
            drawLabel(g, s, new Point(startPoint.x + (i + 1) * step, startPoint.y - 5));
        }
        drawLabel(g, String.format("%.1f", functionModel.getMaximumFunction()), new Point(startPoint.x + (labels.length + 1) * step, startPoint.y - 5));

    }

    private void drawFunction(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Point2D domainPoint = getPointInDomain(new Point(x, y), legendModel.getSettings(), image);
                double value = countValueForPoint(domainPoint);
                if (isColorsInterpolate) {
                    image.setRGB(x, y, legendModel.getInterpolatedColorForValue(value));
                } else {
                    image.setRGB(x, y, legendModel.getColorForValue(value));
                }
            }
        }
    }

    private Point2D getPointInDomain(Point p, Settings settings, BufferedImage image) {
        double scaleX = (double) settings.getWidth() / (double) image.getWidth();
        double scaleY = (double) settings.getHeight() / (double) image.getHeight();

        double x = p.x * scaleX + settings.getA();
        double y = p.y * scaleY + settings.getC();

        return new Point2D.Double(x, y);
    }

    private double countValueForPoint(Point2D p) {
        return legendModel.getFunction().apply(p.getX(), p.getY());
    }

    private void drawLabel(Graphics g, String s, Point p) {
        int strWidth = g.getFontMetrics().stringWidth(s);
        g.drawString(s, p.x - strWidth / 2, p.y);
    }

    public void changeLegendSettings(Settings settings) {
        functionModel.setSettings(settings);
        legendModel.setSettings(settings);
        repaint();
    }

    public void interpolateColors() {
        isColorsInterpolate = !isColorsInterpolate;
        repaint();
    }

    public int getInterpolatedColor(double value) {
        double[] isolines = functionModel.getIsolinesValues();
        float dx = 2.0f * legendPanel.getWidth() / (2 * isolines.length);
        int diapason = Math.round((isolines.length - 1) * dx);
        double coefficient = (value - isolines[0])/(isolines[isolines.length - 1] - isolines[0]);
        int x = (int)Math.round(coefficient*diapason) + 50;
        x = Math.min(legendPanel.getWidth() - 1, Math.max(0, x));
        return legendPanel.getRGB(x, 0);
    }
}
