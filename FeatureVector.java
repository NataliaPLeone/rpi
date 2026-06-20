public class FeatureVector {
    private final String imageName;
    private final double effectiveDiameter;
    private final double circularity;
    private final double roundness;
    private final double radiusRatio;

    public FeatureVector(String imageName, double effectiveDiameter, double circularity, double roundness, double radiusRatio) {
        this.imageName = imageName;
        this.effectiveDiameter = effectiveDiameter;
        this.circularity = circularity;
        this.roundness = roundness;
        this.radiusRatio = radiusRatio;
    }

    public String getImageName() {
        return imageName;
    }

    public double getEffectiveDiameter() {
        return effectiveDiameter;
    }

    public double getCircularity() {
        return circularity;
    }

    public double getRoundness() {
        return roundness;
    }

    public double getRadiusRatio() {
        return radiusRatio;
    }

    @Override
    public String toString() {
        return "FeatureVector {\n" +
               "  imageName = " + imageName + "\n" +
               "  effectiveDiameter = " + effectiveDiameter + "\n" +
               "  circularity = " + circularity + "\n" +
               "  roundness = " + roundness + "\n" +
               "  radiusRatio = " + radiusRatio + "\n" +
               '}';
    }
}
