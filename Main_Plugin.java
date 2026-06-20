

import ij.*;
import ij.gui.*;
import ij.plugin.*;
import java.awt.event.*;

import ij.io.OpenDialog;
import ij.io.DirectoryChooser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public class Main_Plugin implements PlugIn, ActionListener {
   
    
    // ===== 01: DECLARE TOOLS =====
    private ContourExtractor contourExtractor;
    private FeatureExtractor featureExtractor;
    private DistanceCalculator distanceCalculator;
    private KNN kMeans;
    private Output_File_Writer outputWriter;
    
    // ===== 02: STORE DATA =====
    private String referenceImagePath;
    private String referenceImageName;
    private String batchFolderPath;

    // ===== 03: Store feature vectors for all images (shape descriptors vector) =====
    private List<FeatureVector> referenceFeatures;
    private List<FeatureVector> batchFeatures;
    private List<ImagePlus> batchContours;

    // ===== 04: UI COMPONENTS AND MISCELANEOUS =====
    private GenericDialog gd;
    private int k = 3;  // Default K value
    private String distanceType = "EUCLIDIAN";
    private String[] distanceTypes = {"MANHATTAN", "EUCLIDIAN", "MINKOWSKI"};   // add more later (?) - add generalizations of types (?) - discuss with leo

    // ===== 05: MAIN RUN METHOD =====
    @Override
    public void run(String arg) {

        // ===== Initialize =====
        contourExtractor = new ContourExtractor();
        featureExtractor = new FeatureExtractor();
        distanceCalculator = new DistanceCalculator(null, null, null);
        outputWriter = new Output_File_Writer();
        referenceFeatures = new ArrayList<>();

        // ===== SHOWS UI =====
        createAndShowDialog();
    }

    // =====  06: CREATE USER INTERFACE =====

    private void createAndShowDialog() {
        gd = new GenericDialog("PLUGIN MEDIDAS GEOMÉTRICAS", IJ.getInstance());
        
        // ===== UI COMPONENTS AND BUTTONS =====
        gd.addButton("1. Select Reference Image", this);
        gd.addButton("2. Select Batch Folder", this);
        gd.addNumericField("Number of neighbors (K):", 3, 0);
        gd.addChoice("Distance Type:", distanceTypes, "EUCLIDIAN");
        
        gd.showDialog();
        
        if (gd.wasCanceled()) {
            IJ.log("User canceled the operation.");
            return;
        }

        // ===== USER INPUT ====
        k = (int) gd.getNextNumber();
        distanceType = (String) gd.getNextChoice();

        // ===== MAKE SURE USER INPUT ISN'T MISSING/BAD/USELESS
        if (referenceImagePath == null || referenceImagePath.isEmpty()) {
            IJ.showMessage("Error", "Please select a reference image first!");
            return;
        }
        
        if (batchFolderPath == null || batchFolderPath.isEmpty()) {
            IJ.showMessage("Error", "Please select a batch folder first!");
            return;
        }
        
        // ==== PROCESS DATA ====
        processEverything();

    
    }
    // ===== 06: HANDLE BUTTON CLICKS (ACTIONLISTENER IS FOR THIS) =====
     
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("1. Select Reference Image")) {
            selectReferenceImage();
        } else if (command.equals("2. Select Batch Folder")) {
            selectBatchFolder();
        }
    }

    // ===== 07: SELECT REFERENCE IMAGE =====
    private void selectReferenceImage() {
        
        // ===== Open file chooser =====
        OpenDialog od = new OpenDialog("Select Reference Image", null, "");
        String directory = od.getDirectory();
        String fileName = od.getFileName();
        
        // ===== ERROR TEST =====
        if (fileName == null) {
            IJ.showMessage("No file selected");
            return;
        }
        
        referenceImagePath = directory + fileName;
        referenceImageName = fileName;

        // ==== Load the image====
        ImagePlus imp = IJ.openImage(referenceImagePath);
        if (imp == null) {
            IJ.showMessage("Error", "Failed to load image");
            return;
        }

        // ==== 08: EXTRACT CONTOUR ====
        IJ.log("Extracting contour from reference image...");
        ImagePlus contour = contourExtractor.extractContour(imp);
        if (contour != null) {
            contour.show();
            IJ.log("Contour extracted successfully!");
        }
        // ==== 09: GEOMETRIC MEASURES ====
        
        // Cálculo das medidas geométricas
        ShapeData shapeData = new ShapeData(imp, contour);

        // ==== 08: EXTRACT FEATURES (shape descriptor???) ====
        IJ.log("Extracting features from reference image...");
        FeatureVector featureVector = FeatureExtractor.extractFeatures(shapeData, fileName);
        referenceFeatures.add(featureVector);
        IJ.log("Reference features extracted!");
        IJ.log(referenceFeatures.toString());


        IJ.showMessage("Reference Image Loaded!", 
                       "Image: " + fileName + "\n" +
                       "Size: " + imp.getWidth() + "x" + imp.getHeight() + "\n" +
                       "Features extracted: " + referenceFeatures.size()
                    + "\n\n" +
                       "Features:\n" + featureVector.toString());
        
        imp.close();
    }

    // ===== 09: SELECT BATCH FOLDER =====
    private void selectBatchFolder() {
        // Open folder chooser
        DirectoryChooser dc = new DirectoryChooser("Select Folder with Images");
        batchFolderPath = dc.getDirectory();
        
        // ==== error check ====
        if (batchFolderPath == null) {
            IJ.showMessage("No folder selected");
            return;
        }
        
        IJ.log("Selected folder: " + batchFolderPath);

        // ==== get all image files ====
        List<String> imageFiles = getImageFiles(batchFolderPath);
        
        if (imageFiles.isEmpty()) {
            IJ.showMessage("No images found in folder");
            return;
        }
        
        IJ.log("Found " + imageFiles.size() + " images in folder");

        // ==== process all the images44
        batchFeatures = new ArrayList<>();
        
        batchContours = new ArrayList<>(); 

        for (int i = 0; i < imageFiles.size(); i++) {
            String filePath = imageFiles.get(i);
            String fileName = new File(filePath).getName();
            
            IJ.log("Processing (" + (i+1) + "/" + imageFiles.size() + "): " + fileName);

            // Load image
            ImagePlus imp = IJ.openImage(filePath);
            if (imp == null) {
                IJ.log("Failed to load: " + fileName);
                continue;
            }

            // ===== EXTRACT CONTOUR FROM BATCH IMAGE =====
            IJ.log("  → Extracting contour for: " + fileName);
            ImagePlus contour = contourExtractor.extractContour(imp);
            if (contour != null) {
                batchContours.add(contour);
                // Don't show all contours immediately - we'll show them later if user wants
                IJ.log("  → Contour extracted for: " + fileName);
            }

            // Cálculo das medidas geométricas
            ShapeData shapeData = new ShapeData(imp, contour);


            // ===== EXTRACT FEATURES FROM BATCH IMAGE =====
            IJ.log("  → Extracting features for: " + fileName);
            FeatureVector singleFeature = FeatureExtractor.extractFeatures(shapeData, fileName);
            batchFeatures.add(singleFeature);
            IJ.log("  → Features extracted for: " + fileName);
            
            imp.close(); // Free memory
        }

        IJ.log("Batch processing complete!");
        IJ.log("  → Features extracted from " + batchFeatures.size() + " images");
        IJ.log("  → Contours extracted from " + batchContours.size() + " images");
        
        IJ.showMessage("Batch Folder Loaded!", 
                       "Folder: " + batchFolderPath + "\n" +
                       "Images processed: " + batchFeatures.size() + "\n" +
                       "Contours extracted: " + batchContours.size());
    }

    // ===== 09: GET ALL IMAGE FILES FROM FOLDER =====
    private List<String> getImageFiles(String folderPath) {


        List<String> imageFiles = new ArrayList<>();
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        
        // error check
        if (files == null) return imageFiles;
        
        String[] extensions = {".tif", ".tiff", ".jpg", ".jpeg", ".png", ".bmp", ".gif"};
        
        for (File file : files) {
            if (file.isFile()) {
                String name = file.getName().toLowerCase();
                for (String ext : extensions) {
                    if (name.endsWith(ext)) {
                        imageFiles.add(file.getAbsolutePath());
                        break;
                    }
                }
            }
        }
        return imageFiles;
    }

    // ===== 10: PROCESS EVERYTHING!! MAIN WORKFLOW =====
    public void processEverything() {
        IJ.log("\n========================================");
        IJ.log("Processando...");
        IJ.log("========================================");
        IJ.log("Img de ref: " + referenceImageName);
        IJ.log("Pasta de imgs: " + batchFolderPath);
        IJ.log("K = " + k);
        IJ.log("tipo de distancia: " + distanceType);

        // ver se tem como definir pra tamanho binatio (?)
        IJ.log("tamanho do dataset: " + batchFeatures.size());
        IJ.log("tamanjo do contorno das imgs da pasta: " + batchContours.size());
        IJ.log("========================================\n");


        // ===== 11. Distance Calculator =====
        DistanceCalculator.DistanceType selectedDistanceType = DistanceCalculator.DistanceType.valueOf(distanceType);
        distanceCalculator = new DistanceCalculator(selectedDistanceType, null, null);
        IJ.log("Calculadora de distancia: " + selectedDistanceType);
        
        // ===== 12. Kmenas =====
        kMeans = new KNN(k, selectedDistanceType);

        IJ.log("KNN com K = " + k);
        
        // ===== 13. Find nearest neighbors =====
        IJ.log(k + " nearest neighbors...");
        List<KNNResult> results = kMeans.findNearestNeighbors(referenceFeatures, batchFeatures);
        IJ.log("Found " + results.size() + " nearest neighbors!");
        kMeans.printResults(results);

        // ===== 14. Escrever resultado no arquivo =====
        IJ.log("Escrevendo resultado no arquivo...");
        String outputPath = batchFolderPath + File.separator + "knn_results.csv";
        outputWriter.saveKNNResults(referenceFeatures, results, outputPath);
        
        // ===== 15. Salvar as features ===== (pra experimentos próximos ou tabela ?????? )
        String featuresPath = batchFolderPath + File.separator + "all_features.csv";
        outputWriter.saveAllFeatures(batchFeatures, featuresPath);

        // ===== 16 Show results dialog =====
        
        StringBuilder message = new StringBuilder();
        message.append("KNN Processing Complete!\n\n");
        message.append("Reference: ").append(referenceImageName).append("\n");
        message.append("K = ").append(k).append("\n");
        message.append("Distance: ").append(distanceType).append("\n");
        message.append("Contours extracted: ").append(batchContours.size()).append("\n\n");
        message.append("Nearest Neighbors:\n");
        
        for (int i = 0; i < Math.min(5, results.size()); i++) {
            KNNResult result = results.get(i);
            message.append("  ").append(i+1).append(". ")
                   .append(result.imageName)
                   .append(" (distance: ").append(String.format("%.4f", result.distance))
                   .append(")\n");
        }
        
        if (results.size() > 5) {
            message.append("  ... and ").append(results.size() - 5).append(" more\n");
        }
        
        message.append("\nResults saved to:\n");
        message.append("  ").append(outputPath).append("\n");
        message.append("  ").append(featuresPath);
        
        IJ.showMessage("Processing Complete!", message.toString());
        IJ.log("\n========================================");
        IJ.log("Processing Complete!");
        IJ.log("Contours extracted: " + batchContours.size());
        IJ.log("Results saved to: " + outputPath);
        IJ.log("========================================");
    }
}



    
