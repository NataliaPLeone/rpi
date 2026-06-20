

import java.util.*;
import java.math.*;
public class DistanceCalculator {

    public enum DistanceType {
        MANHATTAN,
        EUCLIDIAN,
        MINKOWSKI
    }
    
    private FeatureVector imageRefVector;
    private FeatureVector imageToCompareVector;
    private DistanceType distanceType;
    private double p = 3.0; // For Minkowski distance
    

    public DistanceCalculator(DistanceType distanceType, FeatureVector imageRefVector, FeatureVector imageToCompareVector) {
        this.distanceType = distanceType;
        this.imageRefVector = imageRefVector;
        this.imageToCompareVector = imageToCompareVector;
    }




    // ==== MAIN METHOD ====
    public double calculate(FeatureVector imageRefVector, FeatureVector imageToCompareVector) {
        if (imageRefVector == null || imageToCompareVector == null) {
            throw new IllegalArgumentException("Feature vectors must not be null");
        }

        switch (distanceType) {
            case EUCLIDIAN:
                double dx = imageRefVector.getEffectiveDiameter() - imageToCompareVector.getEffectiveDiameter();
                double dy = imageRefVector.getCircularity() - imageToCompareVector.getCircularity();
                double dz = imageRefVector.getRoundness() - imageToCompareVector.getRoundness();
                double dw = imageRefVector.getRadiusRatio() - imageToCompareVector.getRadiusRatio();
                return Math.sqrt(dx * dx + dy * dy + dz * dz + dw * dw);
            case MANHATTAN:
                return Math.abs(imageRefVector.getEffectiveDiameter() - imageToCompareVector.getEffectiveDiameter()) +
                       Math.abs(imageRefVector.getCircularity() - imageToCompareVector.getCircularity()) +
                       Math.abs(imageRefVector.getRoundness() - imageToCompareVector.getRoundness()) +
                       Math.abs(imageRefVector.getRadiusRatio() - imageToCompareVector.getRadiusRatio());
            case MINKOWSKI:
                double sum = Math.pow(Math.abs(imageRefVector.getEffectiveDiameter() - imageToCompareVector.getEffectiveDiameter()), p) +
                Math.pow(Math.abs(imageRefVector.getCircularity() - imageToCompareVector.getCircularity()), p) +
                Math.pow(Math.abs(imageRefVector.getRoundness() - imageToCompareVector.getRoundness()), p) +
                Math.pow(Math      .abs(imageRefVector.getRadiusRatio() - imageToCompareVector.getRadiusRatio()), p);
                return Math.pow(sum, 1.0 / p);
            default:
                throw new IllegalArgumentException("Unknown distance type");
        }
    }

    public double calculateDistance(FeatureVector imageRefVector, FeatureVector imageToCompareVector) {
        return calculate(imageRefVector, imageToCompareVector);
    }
    
    // Get the name of the distance type
    public String getDistanceName() {
        return distanceType.name();
    }
}
