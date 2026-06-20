
import java.util.*;
import ij.IJ;

public class KNN {
    private int k;
    private DistanceCalculator.DistanceType distanceType;
    private DistanceCalculator distanceCalculator;
    private List<FeatureVector> trainingData;
    private List<String> trainingLabels;

    public KNN(int k, DistanceCalculator.DistanceType distanceType) {
        this.k = k;
        this.distanceType = distanceType;
        this.distanceCalculator = new DistanceCalculator(distanceType, null, null);
    }

    public List<KNNResult> findNearestNeighbors(List<FeatureVector> referenceFeatures, List<FeatureVector> batchFeatures) {
        List<KNNResult> results = new ArrayList<>();

        if (referenceFeatures == null || referenceFeatures.isEmpty()) {
            IJ.log("No reference features!");
            return results;
        }

        if (batchFeatures == null || batchFeatures.isEmpty()) {
            IJ.log("No batch features!");
            return results;
        }

        FeatureVector reference = referenceFeatures.get(0);

        for (FeatureVector fv : batchFeatures) {
            double distance = distanceCalculator.calculateDistance(reference, fv);
            results.add(new KNNResult(fv.getImageName(), distance, fv));
        }

        results.sort((a, b) -> Double.compare(a.distance, b.distance));

        if (results.size() > k) {
            results = results.subList(0, k);
        }

        return results;
    }

    public void printResults(List<KNNResult> results) {
        if (results == null || results.isEmpty()) {
            IJ.log("No KNN results to print.");
            return;
        }

        IJ.log("===== KNN Results =====");
        for (int i = 0; i < results.size(); i++) {
            KNNResult result = results.get(i);
            IJ.log((i + 1) + ". " + result.imageName + " -> distance=" + String.format("%.4f", result.distance));
        }
    }

    public void train(List<FeatureVector> trainingData, List<String> trainingLabels) {
        this.trainingData = trainingData;
        this.trainingLabels = trainingLabels;
    }

    public String predict(FeatureVector input) {
        if (trainingData == null || trainingLabels == null || trainingData.isEmpty() || trainingData.size() != trainingLabels.size()) {
            return "Unknown";
        }

        PriorityQueue<Neighbor> neighbors = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));
        for (int i = 0; i < trainingData.size(); i++) {
            double distance = distanceCalculator.calculateDistance(input, trainingData.get(i));
            neighbors.offer(new Neighbor(trainingLabels.get(i), distance));
        }

        Map<String, Integer> votes = new HashMap<>();
        for (int i = 0; i < Math.min(k, neighbors.size()); i++) {
            Neighbor neighbor = neighbors.poll();
            votes.put(neighbor.label, votes.getOrDefault(neighbor.label, 0) + 1);
        }

        return votes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");
    }

    public static class Neighbor {
        public final String label;
        public final double distance;

        public Neighbor(String label, double distance) {
            this.label = label;
            this.distance = distance;
        }
    }
}

class KNNResult {
    String imageName;
    double distance;
    FeatureVector features;

    public KNNResult(String imageName, double distance, FeatureVector features) {
        this.imageName = imageName;
        this.distance = distance;
        this.features = features;
    }
}
