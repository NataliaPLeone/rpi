import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import ij.IJ;

public class Output_File_Writer {
    // Write the output to a CSV file
    public static void writeOutputToCSV(String outputFilePath, List<FeatureVector> featureVectors, List<String> labels) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            writer.write("Effective Diameter,Circularity,Roundness,Radius Ratio,Label");
            writer.newLine();

            for (int i = 0; i < featureVectors.size(); i++) {
                FeatureVector fv = featureVectors.get(i);
                String label = labels.get(i);
                writer.write(fv.getEffectiveDiameter() + "," +
                             fv.getCircularity() + "," +
                             fv.getRoundness() + "," +
                             fv.getRadiusRatio() + "," +
                             label);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveKNNResults(List<FeatureVector> referenceFeatures, List<KNNResult> results, String outputFilePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            writer.write("ReferenceImage,NeighborImage,Distance,EffectiveDiameter,Circularity,Roundness,RadiusRatio");
            writer.newLine();

            String referenceName = "";
            if (referenceFeatures != null && !referenceFeatures.isEmpty()) {
                referenceName = referenceFeatures.get(0).getImageName();
            }

            for (KNNResult result : results) {
                FeatureVector fv = result.features;
                writer.write(referenceName + "," +
                             result.imageName + "," +
                             result.distance + "," +
                             fv.getEffectiveDiameter() + "," +
                             fv.getCircularity() + "," +
                             fv.getRoundness() + "," +
                             fv.getRadiusRatio());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAllFeatures(List<FeatureVector> featureVectors, String outputFilePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            writer.write("ImageName,EffectiveDiameter,Circularity,Roundness,RadiusRatio");
            writer.newLine();

            for (FeatureVector fv : featureVectors) {
                writer.write((fv.getImageName() != null ? fv.getImageName() : "") + "," +
                             fv.getEffectiveDiameter() + "," +
                             fv.getCircularity() + "," +
                             fv.getRoundness() + "," +
                             fv.getRadiusRatio());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Show the k nearest neighbors images in a dialog box
    // k defined by user in Main_Plugin.java
    public static void showKNNResults(List<FeatureVector> trainingData, List<String> trainingLabels, FeatureVector input, int k) {
        KNN knn = new KNN(k, DistanceCalculator.DistanceType.EUCLIDIAN);
        knn.train(trainingData, trainingLabels);
        String predictedLabel = knn.predict(input);

        String message = "Predicted Label: " + predictedLabel + "\n\n";
        message += "K Nearest Neighbors:\n";

        PriorityQueue<KNN.Neighbor> neighbors = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));
        DistanceCalculator calculator = new DistanceCalculator(DistanceCalculator.DistanceType.EUCLIDIAN, null, null);
        for (int i = 0; i < trainingData.size(); i++) {
            double distance = calculator.calculateDistance(input, trainingData.get(i));
            neighbors.offer(new KNN.Neighbor(trainingLabels.get(i), distance));
        }

        for (int i = 0; i < k && !neighbors.isEmpty(); i++) {
            KNN.Neighbor neighbor = neighbors.poll();
            message += "Label: " + neighbor.label + ", Distance: " + neighbor.distance + "\n";
        }

        IJ.showMessage("KNN Results", message);
    }
}
