import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

public class ContourExtractor {
    public static ImagePlus extractContour(ImagePlus original) {
        // Aplicação de filtro de mediana para eliminar ruídos pontuais
        ImagePlus filtered = original.duplicate();
        IJ.run(filtered, "Median...", "radius=3");

        // Aplicação da erosão para posterior subtração
        ImagePlus eroded = filtered.duplicate();
        IJ.run(eroded, "Gray Morphology", "radius=1 type=diamond operator=erode");

        // Aplicação da subtração para obter o contorno
        ImagePlus contour = subtract(filtered, eroded);

        return contour;
    }

    private static ImagePlus subtract(ImagePlus original, ImagePlus eroded) {
        ImageProcessor ipOriginal = original.getProcessor();
        ImageProcessor ipEroded = eroded.getProcessor();

        ImageProcessor ipContour = ipOriginal.duplicate();

        for (int y = 0; y < ipOriginal.getHeight(); y++) {
            for (int x = 0; x < ipOriginal.getWidth(); x++) {
                int valueOriginal = ipOriginal.getPixel(x, y);
                int valueEroded = ipEroded.getPixel(x, y);

                int contourValue = Math.max(0, valueOriginal - valueEroded);
                ipContour.putPixel(x, y, contourValue);
            }
        }

        return new ImagePlus("Contour", ipContour);
    }
}
