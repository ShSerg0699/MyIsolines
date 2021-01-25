package nsu.shserg;

import nsu.shserg.util.FunctionModel;
import nsu.shserg.util.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;

public class IsolinesPanel extends JPanel {
    private BufferedImage canvas;
    private FunctionModel model;
    private boolean isColorsInterpolate = false;
    private boolean isGridShown = false;
    private boolean isIsolinesShown = false;
    private LegendPanel legend;

    private Map<Point, ArrayList<Double>> isolinesDotes;
    private double scaleX;
    private double scaleY;
    private int oldHeight = 0;
    private int oldWidth = 0;

    private Double dynamicIsoline;


    public IsolinesPanel(FunctionModel model, JLabel statusLabel, LegendPanel legend) {
        this.model = model;
        this.legend = legend;

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point2D p = getPointInDomain(e.getPoint(), model.getSettings(), canvas);
                statusLabel.setText(String.format("(X: %.1f, Y: %.1f) -> %.1f", p.getX(), p.getY(), countValueForPoint(p)));
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = e.getPoint();
                dynamicIsoline = countValueForPoint(canvas, model.getSettings(), p);
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();
                dynamicIsoline = countValueForPoint(canvas, model.getSettings(), p);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dynamicIsoline = null;
                repaint();
            }

        });

    }

    @Override
    protected void paintComponent(Graphics g) {
        updateCanvas();
        int n = model.getSettings().getN();
        int m = model.getSettings().getM();

        g.drawImage(canvas, 0, 0, canvas.getWidth(), canvas.getHeight(), this);

        double[] isolines = model.getIsolinesValues();
        BufferedImage isolineImage = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
        if (isIsolinesShown) {
            for (int i = 0; i < isolines.length; i++) {
                drawIsoline(isolineImage, n, m, isolines[i], true);
            }
            if (dynamicIsoline != null) {
                drawIsoline(isolineImage, n, m, dynamicIsoline, true);
            }
            g.drawImage(isolineImage, 0, 0, isolineImage.getWidth(), isolineImage.getHeight(), this);
        }
    }

    private void drawFunction(BufferedImage image) {
        if (isColorsInterpolate) {
            double m = model.getSettings().getM();
            double n = model.getSettings().getN();
            double a = model.getSettings().getA();
            double c = model.getSettings().getC();
            double[][] gridNodesValue = new double[(int) m][(int) n];
            double stepX = (model.getSettings().getB() - a) / (n - 1);
            double stepY = (model.getSettings().getD() - c) / (m - 1);
            for (int i = 0; i < m; ++i) {
                for (int j = 0; j < n; ++j) {
                    gridNodesValue[i][j] = model.getFunction().apply(a + j * stepX, c + i * stepY);
                }
            }
            for (int y = 0; y < canvas.getHeight(); ++y) {
                for (int x = 0; x < canvas.getWidth(); ++x) {
                    double coefficientX = 1.0 * x / canvas.getWidth();
                    double coefficientY = 1.0 * (canvas.getHeight() - 1 - y) / canvas.getHeight();
                    canvas.setRGB(x, canvas.getHeight() - 1 - y, legend.getInterpolatedColor(getInterpolatedValue(coefficientX, coefficientY, stepX, stepY, gridNodesValue)));
                }
            }
        } else {
            int[] colors = model.getSettings().getColors();
            Graphics g = image.getGraphics();
            for (Map.Entry<Point, ArrayList<Double>> entry : isolinesDotes.entrySet()) {
                Point point = entry.getKey();
                double value = entry.getValue().get(0);
                double isHorizontal = entry.getValue().get(1);
                int c1 = model.getColorForValue(value);
                int c2 = 0;
                for (int k = 0; k < colors.length; k++) {
                    if (c1 == colors[k]) {
                        if (k == colors.length - 1) {
                            c2 = colors[k - 1];
                        } else {
                            c2 = colors[k + 1];
                        }
                        break;
                    }
                }

                if (isHorizontal == 0) {
                    double negValueX = countValueForPoint(image, model.getSettings(), new Point(point.x - 1, point.y));
                    double posValueX = countValueForPoint(image, model.getSettings(), new Point(point.x + 1, point.y));
                    if (negValueX > posValueX) {
                        int t = c1;
                        c1 = c2;
                        c2 = t;
                    }
                    if ((point.x - 1 >= 0) && (point.x - 1 < image.getWidth()) && (point.y >= 0) && (point.y < image.getHeight())
                            && (Color.WHITE).equals(new Color(image.getRGB(point.x - 1, point.y)))) {
                        g.setColor(new Color(c1));
                        spanFill(point.x - 1, point.y, g, image);
                    }
                    if ((point.x + 1 >= 0) && (point.x + 1 < image.getWidth()) && (point.y >= 0) && (point.y < image.getHeight())
                            && (Color.WHITE).equals(new Color(image.getRGB(point.x + 1, point.y)))) {
                        g.setColor(new Color(c2));
                        spanFill(point.x + 1, point.y, g, image);
                    }
                } else {
                    double negValueY = countValueForPoint(image, model.getSettings(), new Point(point.x, point.y - 1));
                    double posValueY = countValueForPoint(image, model.getSettings(), new Point(point.x, point.y + 1));
                    if (negValueY > posValueY) {
                        int t = c1;
                        c1 = c2;
                        c2 = t;
                    }
                    if ((point.y - 1 >= 0) && (point.y - 1 < image.getHeight()) && (point.x >= 0) && (point.x < image.getWidth())
                            && (Color.WHITE).equals(new Color(image.getRGB(point.x, point.y - 1)))) {
                        g.setColor(new Color(c1));
                        spanFill(point.x, point.y - 1, g, image);
                    }
                    if ((point.y + 1 >= 0) && (point.y + 1 < image.getHeight()) && (point.x >= 0) && (point.x < image.getWidth())
                            && (Color.WHITE).equals(new Color(image.getRGB(point.x, point.y + 1)))) {
                        g.setColor(new Color(c2));
                        spanFill(point.x, point.y + 1, g, image);
                    }
                }
            }
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    if ((new Color(image.getRGB(x, y))).equals(new Color(model.getSettings().getIsolineColor()))) {
                        if ((x - 1 >= 0) && !((new Color(image.getRGB(x - 1, y))).equals(new Color(model.getSettings().getIsolineColor())))) {
                            image.setRGB(x, y, image.getRGB(x - 1, y));
                        } else if ((x + 1 < image.getWidth()) && !((new Color(image.getRGB(x + 1, y))).equals(new Color(model.getSettings().getIsolineColor())))) {
                            image.setRGB(x, y, image.getRGB(x + 1, y));
                        } else if ((y - 1 >= 0) && !((new Color(image.getRGB(x, y - 1))).equals(new Color(model.getSettings().getIsolineColor())))) {
                            image.setRGB(x, y, image.getRGB(x, y - 1));
                        } else if ((y + 1 < image.getHeight()) && !((new Color(image.getRGB(x, y + 1))).equals(new Color(model.getSettings().getIsolineColor())))) {
                            image.setRGB(x, y, image.getRGB(y + 1, y));
                        }
                    }
                }
            }

        }
    }

    protected double countValueForPoint(Point2D p) {
        return model.getFunction().apply(p.getX(), p.getY());
    }

    protected double countValueForPoint(BufferedImage image, Settings settings, Point p) {
        Point2D point2D = getPointInDomain(p, settings, image);
        return countValueForPoint(point2D);
    }

    protected Point2D getPointInDomain(Point p, Settings settings, BufferedImage image) {
        double scaleX = (double) settings.getWidth() / (double) image.getWidth();
        double scaleY = (double) settings.getHeight() / (double) image.getHeight();

        double x = p.x * scaleX + settings.getA();
        double y = p.y * scaleY + settings.getC();

        return new Point2D.Double(x, y);
    }

    protected void updateCanvas() {
        Dimension size = getSize();
        if ((size.height != oldHeight) || (size.width != oldWidth)) {
            oldHeight = size.height;
            oldWidth = size.width;
            isolinesDotes = new HashMap<>();
        }
        canvas = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
        canvas.getGraphics().setColor(new Color(255 << 16 | 255 << 8 | 255));
        spanFill(0, 0, canvas.getGraphics(), canvas);
        Settings settings = model.getSettings();

        scaleX = (double) settings.getWidth() / (double) canvas.getWidth();
        scaleY = (double) settings.getHeight() / (double) canvas.getHeight();

        int n = model.getSettings().getN();
        int m = model.getSettings().getM();

        double[] isolines = model.getIsolinesValues();
        for (int i = 0; i < isolines.length; i++) {
            drawIsoline(canvas, n, m, isolines[i], false);
        }
        drawFunction(canvas);
        if (isGridShown) {
            drawGrid(canvas, n, m);
        }
    }

    private void drawGrid(BufferedImage image, int n, int m) {
        double stepX = (double) image.getWidth() / n;
        double stepY = (double) image.getHeight() / m;

        Graphics g = image.getGraphics();
        g.setXORMode(Color.BLACK);
        for (int i = 0; i < n - 1; i++) {
            int x0 = (int) (stepX * (i + 1));
            g.drawLine(x0, 0, x0, image.getHeight());
        }

        for (int i = 0; i < m - 1; i++) {
            int y0 = (int) (stepY * (i + 1));
            g.drawLine(0, y0, image.getWidth(), y0);
        }

    }

    private void drawIsoline(BufferedImage image, int n, int m, double isoline, boolean isDynamic) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                double stepX = (double) image.getWidth() / n;
                double stepY = (double) image.getHeight() / m;

                Point[] gridNodes = new Point[4];
                gridNodes[0] = new Point((int) (i * stepX), (int) (j * stepY));
                gridNodes[1] = new Point((int) ((i + 1) * stepX), (int) (j * stepY));
                gridNodes[2] = new Point((int) ((i + 1) * stepX), (int) ((j + 1) * stepY));
                gridNodes[3] = new Point((int) (i * stepX), (int) ((j + 1) * stepY));

                boolean[] signs = new boolean[gridNodes.length];

                for (int k = 0; k < gridNodes.length; k++) {
                    signs[k] = countValueForPoint(image, model.getSettings(), gridNodes[k]) > isoline;
                }

                List<Point> intersections = new LinkedList<>();
                for (int t = 0; t < gridNodes.length; t++) {
                    if (signs[t] == signs[(t + 1) % gridNodes.length]) {
                        continue;
                    }
                    Point intersection = calculateIntersection(image, gridNodes[t], gridNodes[(t + 1) % gridNodes.length], isoline, isDynamic);
                    intersections.add(intersection);
                }

                Graphics g = image.getGraphics();
                g.setColor(new Color(model.getSettings().getIsolineColor()));

                if (intersections.size() == 2) {
                    Point p1 = intersections.get(0);
                    Point p2 = intersections.get(1);

                    g.drawLine(p1.x, p1.y, p2.x, p2.y);
                }

                if (intersections.size() == 3) {
                    for (int p = 0; p < 3; p++) {
                        for (int q = 0; q < gridNodes.length; q++) {
                            if (intersections.get(p).equals(gridNodes[q])) {
                                int dx = 0;
                                int dy = 0;
                                switch (q) {
                                    case 0:
                                        dx = dy = 1;
                                        break;
                                    case 1:
                                        dx = -1;
                                        dy = 1;
                                        break;
                                    case 2:
                                        dx = dy = -1;
                                        break;
                                    case 3:
                                        dx = 1;
                                        dy = -1;
                                        break;
                                }

                                Point p1 = new Point(gridNodes[q].x + dx, gridNodes[q].y);
                                Point p2 = new Point(gridNodes[q].x, gridNodes[q].y + dy);
                                intersections.remove(p);
                                intersections.add(p, p1);
                                intersections.add(p, p2);
                            }
                        }
                    }
                }

                if (intersections.size() == 4) {
                    Point center = new Point((gridNodes[2].x + gridNodes[0].x) / 2, (gridNodes[2].y + gridNodes[0].y) / 2);
                    double cval = countValueForPoint(image, model.getSettings(), center);
                    int[] sides = new int[4];
                    Point[] center_points = new Point[2];
                    boolean center_greater = countValueForPoint(image, model.getSettings(), center) >= isoline;
                    boolean right_greater = signs[0];

                    double g0 = countValueForPoint(image, model.getSettings(), gridNodes[0]);
                    double g1 = countValueForPoint(image, model.getSettings(), gridNodes[1]);
                    double g2 = countValueForPoint(image, model.getSettings(), gridNodes[2]);
                    double g3 = countValueForPoint(image, model.getSettings(), gridNodes[3]);
                    if(((g1 > isoline) && (isoline > cval)) || ((g1 < isoline) &&(isoline < cval))) {
                        center_points[0] = calculateLineIntersection(gridNodes[1], center, isoline);
                        sides[0] = 0;
                        sides[1] = 1;

                        center_points[1] = calculateLineIntersection(gridNodes[3], center, isoline);
                        sides[2] = 2;
                        sides[3] = 3;
                    }
                    if(((g2 > isoline) && (isoline > cval)) || ((g2 < isoline) &&(isoline < cval))) {

                        center_points[0] = calculateLineIntersection(gridNodes[0], center, isoline);
                        sides[0] = 0;
                        sides[1] = 3;

                        center_points[1] = calculateLineIntersection(gridNodes[2], center, isoline);
                        sides[2] = 1;
                        sides[3] = 2;
                    }


//                    if (sides[1] == 3) {
//                        center_points[0].setLocation(center_points[0].x + 10, center_points[0].y + 10);
//                    }

                    // Draw first triangle
                    g.drawLine(intersections.get(sides[0]).x, intersections.get(sides[0]).y, center_points[0].x, center_points[0].y);
                    g.drawLine(center_points[0].x, center_points[0].y, intersections.get(sides[1]).x, intersections.get(sides[1]).y);

                    // Draw second triangle
                    g.drawLine(intersections.get(sides[2]).x, intersections.get(sides[2]).y, center_points[1].x, center_points[1].y);
                    g.drawLine(center_points[1].x, center_points[1].y, intersections.get(sides[3]).x, intersections.get(sides[3]).y);


                }
            }
        }
    }


    private Point calculateLineIntersection(Point a, Point center, double isoline) {
        double val_a = countValueForPoint(canvas, model.getSettings(), a);
        double val_center = countValueForPoint(canvas, model.getSettings(), center);
        double k, ix, iy;
        // a > isoline > center
        // center > isoline > a
        k = (isoline - val_a) / (val_center - val_a);
        if (center.x > a.x) {
            // 0
            if (center.y > a.y) {
                ix = a.x + Math.abs(a.x - center.x) * k;
                iy = a.y + Math.abs(a.y - center.y) * k;
            }
            // 3
            else {
                ix = a.x + Math.abs(a.x - center.x) * k;
                iy = a.y - Math.abs(a.y - center.y) * k;
            }
        } else {
            // 1
            if (center.y > a.y) {
                ix = a.x - Math.abs(a.x - center.x) * k;
                iy = a.y + Math.abs(a.y - center.y) * k;

            }
            // 2
            else {
                ix = a.x - Math.abs(a.x - center.x) * k;
                iy = a.y - Math.abs(a.y - center.y) * k;
            }
        }


        return new Point((int) ix, (int) iy);

    }

    public void spanFill(int x, int y, Graphics g, BufferedImage image) {
        int oldColor = image.getRGB(x, y);

        if (oldColor == g.getColor().getRGB()) {
            return;
        }

        int i;
        for (i = x; (i >= 0) && (oldColor == image.getRGB(i, y)); --i) ;
        int spanStart = i + 1;
        for (i = x; (i < image.getWidth()) && (oldColor == image.getRGB(i, y)); ++i) ;
        int spanEnd = i - 1;
        g.drawLine(spanStart, y, spanEnd, y);

        for (i = spanStart; i <= spanEnd; ++i) {
            if ((y < image.getHeight() - 1) && oldColor == image.getRGB(i, y + 1)) {
                spanFill(i, y + 1, g, image);
            }
            if ((y > 0) && oldColor == image.getRGB(i, y - 1)) {
                spanFill(i, y - 1, g, image);
            }
        }

    }

    private Point calculateIntersection(BufferedImage image, Point p1, Point p2, double isoline, boolean isDynamic) {
        boolean horizontal = p1.y == p2.y;

        double f1 = countValueForPoint(image, model.getSettings(), p1);
        double f2 = countValueForPoint(image, model.getSettings(), p2);

        double localX;
        double localY;

        if (horizontal) {
            double dx = p2.x - p1.x;
            localX = dx * scaleX * (isoline - f1) / (f2 - f1);
            localY = 0;
        } else {
            double dy = p2.y - p1.y;
            localX = 0;
            localY = dy * scaleY * (isoline - f1) / (f2 - f1);
        }

        Point point = new Point((int) (p1.x + localX / scaleX), (int) (p1.y + localY / scaleY));
        if(point.x == image.getWidth())
            point.setLocation(point.x - 1, point.y);
        if(point.y == image.getHeight())
            point.setLocation(point.x, point.y - 1);
        if (!isDynamic) {
            ArrayList<Double> list = new ArrayList<>();
            list.add(isoline);
            list.add(horizontal ? 0.0 : 1.0);
            isolinesDotes.put(point, list);
        }
        return point;
    }

    public double getInterpolatedValue(double coefficientX, double coefficientY, double xStep, double yStep, double[][] grid) {
        double width = coefficientX * (model.getSettings().getB() - model.getSettings().getA());
        double height = coefficientY * (model.getSettings().getD() - model.getSettings().getC());
        int y = (int) (height / yStep);
        int x = (int) (width / xStep);
        coefficientX = (width - x * xStep) / xStep;
        coefficientY = (height - y * yStep) / yStep;
        double c1 = grid[y][x] + coefficientX * (grid[y][x + 1] - grid[y][x]);
        double c2 = grid[y + 1][x] + coefficientX * (grid[y + 1][x + 1] - grid[y + 1][x]);
        return c1 + coefficientY * (c2 - c1);
    }

    public void changeSettings(Settings settings) {
        model.setSettings(settings);
        isolinesDotes = new HashMap<>();
        repaint();
    }

    public void interpolateColors() {
        isColorsInterpolate = !isColorsInterpolate;
        repaint();
    }

    public void showGrid() {
        isGridShown = !isGridShown;
        drawGrid(canvas, model.getSettings().getN(), model.getSettings().getM());
        repaint();
    }

    public void showIsolines() {
        isIsolinesShown = !isIsolinesShown;
        repaint();
    }
}