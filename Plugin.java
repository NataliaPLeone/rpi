import ij.*;
import ij.gui.*;
import ij.plugin.*;
import java.awt.event.*;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.gui.GenericDialog;
import ij.io.OpenDialog;
import java.io.File;
import ij.io.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.filter.Analyzer;
import ij.measure.*;
import ij.io.DirectoryChooser;

public class Main implements PlugIn, ActionListener {
    // Keep a reference to the dialog to read other inputs later
    private GenericDialog gd; 
    
    private ImagePlus selectedImage;
    private String imageName;
    private String imagePath;

    @Override
    public void run(String arg) {
        // 1. Initialize the Generic Dialog
        gd = new GenericDialog("Plugin de medidas geométricas");
        
        // 2. Optional: Add other data input fields
        //gd.addStringField("Input Data:", "Default Text");
        
        gd.addButton("Escolha sua imagem de referencia", this);
        gd.addButton("Escolha uma pasta de imagens para comparar ", this);
        // 4. Display the dialog window
        gd.showDialog();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Escolha sua imagem de referencia")) {
            selectImageFromFileSystem();
        } else if (command.equals("Escolha uma pasta de imagens para comparar ")) {
            selectFolderBatchImages();
        }
    }


    private void selectImageFromFileSystem() {
        // Open file dialog
        OpenDialog od = new OpenDialog("Select an Image", null, "");
        String directory = od.getDirectory();
        String fileName = od.getFileName();
        
        if (fileName == null) {
            IJ.showMessage("No file selected");
            return;
        }
        
        // Build full path
        imagePath = directory + fileName;
        imageName = fileName;
        
        // Load the image
        selectedImage = IJ.openImage(imagePath);
        
        if (selectedImage == null) {
            IJ.showMessage("Error", "Failed to load image: " + imagePath);
            return;
        }
        
        // Display the image
        selectedImage.show();
        
        // Display confirmation
        IJ.showMessage("Image loaded successfully!", 
                       "Image: " + imageName + "\n" +
                       "Path: " + imagePath + "\n" +
                       "Size: " + selectedImage.getWidth() + "x" + selectedImage.getHeight() + 
                       " pixels");
        

    }
    private void selectFolderBatchImages() {
        /* 
        // Open directory chooser dialog
        DirectoryChooser dc = new DirectoryChooser("Select Folder with Images");
        String folderPath = dc.getDirectory();
        
        if (folderPath == null) {
            IJ.showMessage("No folder selected");
            return;
        }
        
        // List all files in the selected directory
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        
        if (files == null || files.length == 0) {
            IJ.showMessage("No files found in the selected folder");
            return;
        }
        
        // Process each file (for demonstration, just log the file names)
        for (File file : files) {
            if (file.isFile()) {
                IJ.log("Found file: " + file.getName());
                // Here you can add code to check if it's an image and process it
            }
        }
        */
        
        // 1. Open the folder selection dialog window
        DirectoryChooser dc = new DirectoryChooser("Select Input Folder");

        // 2. Extract the chosen folder path
        String folderPath = dc.getDirectory();

        // 3. Ensure the user didn't cancel
        if (folderPath != null) {
            IJ.showMessage("Selected folder: " + folderPath);
            // Proceed with processing files inside this folder path
        }


        gd.showDialog();

    }
    
}
