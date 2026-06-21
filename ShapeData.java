import ij.ImagePlus;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import ij.gui.GenericDialog;

public class ShapeData {
    private double area;
    private double perimeter;
    private double majorAxis;
    private double minorAxis;

    public ShapeData(ImagePlus original, ImagePlus contour) {
        // Cálculo da área
        this.area = calculateArea(original);

        // Cálculo do perímetro
        this.perimeter = calculatePerimeter(contour);

        // Cálculo dos eixos maior e menor
        double[] axes = calculateAxes(original, contour);

        this.majorAxis = axes[0];
        this.minorAxis = axes[1];
    }

    private double calculateArea(ImagePlus original) {
        double area = 0.0;

        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                int pixelValue = original.getProcessor().getPixel(x, y);

                if (pixelValue > 0) {
                    area++;
                }
            }
        }

        return area;
    }

    private double calculatePerimeter(ImagePlus contour) {
        double perimeter = 0.0;

        for (int y = 0; y < contour.getHeight(); y++) {
            for (int x = 0; x < contour.getWidth(); x++) {
                int pixelValue = contour.getProcessor().getPixel(x, y);

                if (pixelValue > 0) {
                    perimeter++;
                }
            }
        }

        return perimeter;
    }

    private Object[] calculateMajorAxis(List<Point> borderPixels) {
        double maxDistance = 0.0;
        Point bestP1 = null;
        Point bestP2 = null;

        for (int i = 0; i < borderPixels.size(); i++) {
            Point p1 = borderPixels.get(i);

            for (int j = i + 1; j < borderPixels.size(); j++) {
                Point p2 = borderPixels.get(j);

                double distance = p1.distance(p2);

                if (distance > maxDistance) {
                    maxDistance = distance;

                    bestP1 = p1;
                    bestP2 = p2;
                }
            }
        }

        return new Object[]{ maxDistance, bestP1, bestP2 };
    }

    private boolean isObjectPixel(ImagePlus img, int x, int y) {
        if (x < 0 || y < 0 || x >= img.getWidth() || y >= img.getHeight()) {
            return false;
        }

        return img.getProcessor().getPixel(x, y) > 0;
    }

    private double calculateMinorAxis(ImagePlus original, Point p1, Point p2) {
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;

        double axisLength = Math.sqrt(dx * dx + dy * dy);

        if (axisLength == 0) {
            return 0;
        }

        // vetor unitário do eixo maior
        double ux = dx / axisLength;
        double uy = dy / axisLength;

        // vetor unitário perpendicular
        double px = -uy;
        double py = ux;

        double maxWidth = 0;

        for (double t = 0; t <= axisLength; t += 1.0) {
            double cx = p1.x + dx * t;
            double cy = p1.y + dy * t;

            double positive = 0;

            while (isObjectPixel(
                    original,
                    (int) Math.round(cx + px * positive),
                    (int) Math.round(cy + py * positive))) {

                positive++;
            }

            double negative = 0;

            while (isObjectPixel(
                    original,
                    (int) Math.round(cx - px * negative),
                    (int) Math.round(cy - py * negative))) {

                negative++;
            }

            double width = positive + negative - 1;

            if (width > maxWidth) {
                maxWidth = width;
            }
        }

        return maxWidth;
    }

    private double[] calculateAxes(ImagePlus original, ImagePlus contour) {
        List<Point> borderPixels = new ArrayList<>();

        for (int y = 0; y < contour.getHeight(); y++) {
            for (int x = 0; x < contour.getWidth(); x++) {
                if (contour.getProcessor().getPixel(x, y) > 0) {
                    borderPixels.add(new Point(x, y));
                }
            }
        }

        if (borderPixels.isEmpty()) {
            return new double[]{ 0, 0 };
        }

        Object[] major = calculateMajorAxis(borderPixels);

        double majorAxis = (double) major[0];
        double minorAxis = calculateMinorAxis(original, (Point) major[1], (Point) major[2]);

        return new double[]{ majorAxis, minorAxis };
    }

    public double getArea() {
        return area;
    }

    public double getPerimeter() {
        return perimeter;
    }

    public double getMajorAxis() {
        return majorAxis;
    }

    public double getMinorAxis() {
        return minorAxis;
    }
}
