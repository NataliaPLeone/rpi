

public class FeatureExtractor {
    public static FeatureVector extractFeatures(ShapeData shapeData, String imageName) {
        // Cálculo do diâmetro efetivo
        double effectiveDiameter = calculateEffectiveDiameter(shapeData);

        // Cálculo da circularidade
        double circularity = calculateCircularity(shapeData);

        // Cálculo da arredondamento
        double roundness = calculateRoundness(shapeData);

        // Cálculo da razão de raios
        double radiusRatio = calculateRadiusRatio(shapeData);

        return new FeatureVector(imageName, effectiveDiameter, circularity, roundness, radiusRatio);
    }

    private static double calculateEffectiveDiameter(ShapeData shapeData) {
        double d = 2 * Math.sqrt(shapeData.getArea() / Math.PI);

        return d;
    }

    private static double calculateCircularity(ShapeData shapeData) {
        double c = (4 * Math.PI * shapeData.getArea()) / (Math.pow(shapeData.getPerimeter(), 2));

        return c;
    }

    private static double calculateRoundness(ShapeData shapeData) {
        double r = (4 * shapeData.getArea()) / (Math.PI * Math.pow(shapeData.getMajorAxis(), 2));

        return r;
    }

    private static double calculateRadiusRatio(ShapeData shapeData) {
        double rr = shapeData.getMajorAxis() / shapeData.getMinorAxis();

        return rr;
    }
}
