import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;


public class Stegano {

    public static void main(String[] args) {

        try{

            File baseImageFile = new File("images/image1.jpg");
            BufferedImage baseImage = ImageIO.read(baseImageFile);


            File secreteImageFile = new File("images/image4.jpg");
            BufferedImage secretImage = ImageIO.read(secreteImageFile);

            secretImage = resizeImage(secretImage, baseImage.getWidth(), baseImage.getHeight());
            BufferedImage overlayedImage = overlayImages(baseImage, secretImage);


            if (overlayedImage != null){
                //writeImage(overlayedImage, "overLayedImage1.jpg", "JPG");
                ImageIO.write(overlayedImage,"jpg", new File("/Users/luca/Dropbox/Studium/Semester_8/krysi-stegano/out/overlayedImage.jpg"));
                System.out.println("Hiding Completed...");
                System.out.println("");
            }else
                System.out.println("Problem With Overlay...");

            System.out.println("Starting Extraction ");
            System.out.println("");
            BufferedImage extractedImage = exctractImage(overlayedImage);
            if (extractedImage != null){
                //writeImage(overlayedImage, "overLayedImage1.jpg", "JPG");
                ImageIO.write(extractedImage,"jpg", new File("/Users/luca/Dropbox/Studium/Semester_8/krysi-stegano/out/extractedImage.jpg"));
                System.out.println("Extraction Completed...");
            }else
                System.out.println("Problem With Overlay...");

        }catch(Exception e){System.out.println("Error");}

    }


    //Used to Fill the String of R G or B value with 0's so they all have to same length
    public static String fillColorString(String toFill) {
        if (toFill.length() < 8) {
            while (toFill.length() % 8 != 0) {
                toFill = "0" + toFill;
            }
        }
        return toFill;
    }

    //Merges the Colour of the base and the hidden Image Pixels
    public static Color mergeColours(Color base, Color secret){

        String redBase = Integer.toBinaryString(base.getRed());
        redBase = fillColorString(redBase);
        String greenBase = Integer.toBinaryString(base.getGreen());
        greenBase = fillColorString(greenBase);
        String blueBase = Integer.toBinaryString(base.getBlue());
        blueBase = fillColorString(blueBase);

        String redSecret = Integer.toBinaryString(secret.getRed());
        redSecret = fillColorString(redSecret);
        String greenSecret = Integer.toBinaryString(secret.getGreen());
        greenSecret = fillColorString(greenSecret);
        String blueSecret = Integer.toBinaryString(secret.getBlue());
        blueSecret = fillColorString(blueSecret);

        String redMerged = merge(redBase, redSecret);
        String greenMerged = merge(greenBase, greenSecret);
        String blueMerged = merge(blueBase, blueSecret);


        Color mergedColor = new Color(Integer.parseInt(redMerged,2), Integer.parseInt(greenMerged, 2), Integer.parseInt(blueMerged, 2));
        return mergedColor;

    }

    //Method to split and merge the Bitstrings of base and secret
    public static String merge(String base, String secret){

        String mergedValue = "";
        String [] splitBase = base.split("(?<=\\G.{4})");
        String [] splitSecret = secret.split("(?<=\\G.{4})");

        mergedValue = splitBase[0]+splitSecret[0];

        return mergedValue;
    }

    //Extracts the last 4 bits of the R G or B value
    public static String extract(String base){

        String exctractedValue;
        String [] splitBase = base.split("(?<=\\G.{4})");


        exctractedValue = splitBase[1]+"0000";

        return exctractedValue;
    }


    //Returns the Hidden colour in the image
    public static Color getHiddenColor(Color baseColor){

        String redBase = Integer.toBinaryString(baseColor.getRed());
        redBase = fillColorString(redBase);
        String greenBase = Integer.toBinaryString(baseColor.getGreen());
        greenBase = fillColorString(greenBase);
        String blueBase = Integer.toBinaryString(baseColor.getBlue());
        blueBase = fillColorString(blueBase);

        String exctractedRed = extract(redBase);
        String exctractedGreen = extract(greenBase);
        String exctractedBlue = extract(blueBase);

        Color exctractedColor = new Color(Integer.parseInt(exctractedRed,2), Integer.parseInt(exctractedGreen, 2), Integer.parseInt(exctractedBlue, 2));
        return exctractedColor;
    }




    //Hides the secret image in the base image
    public static BufferedImage overlayImages(BufferedImage baseImage, BufferedImage secretImage) {

        System.out.println("Started hiding image");
        System.out.println(" ");
        if (secretImage.getHeight() > baseImage.getHeight()
                || secretImage.getWidth() > secretImage.getWidth()) {
            JOptionPane.showMessageDialog(null,
                    "Foreground Image Is Bigger In One or Both Dimensions"
                            + "\nCannot proceed with overlay."
                            + "\n\n Please use smaller Image for foreground");

            return null;
        }

        //Go Through Image
        BufferedImage mergedImage = baseImage;


        int w = secretImage.getWidth();
        int h = secretImage.getHeight();


        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                //To Test loop through image
                //System.out.println("x,y: " + j + ", " + i);

                //Get RGB Values Base Image
                int pixelBase = baseImage.getRGB(j, i);
                Color rgbValuesBaseImage = new Color(pixelBase);

                //Get RGB Values Secret Image
                int pixelSecret = secretImage.getRGB(j, i);
                Color rgbValuesSecretImage = new Color(pixelSecret);


                Color mergedColor = mergeColours(rgbValuesBaseImage, rgbValuesSecretImage);
                int pixelMerged = mergedColor.getRGB();

               mergedImage.setRGB(j, i, pixelMerged);
                }

                //System.out.println(" ");
            }
        return mergedImage;
        }


    public static BufferedImage exctractImage(BufferedImage imageWithMessage) {

        BufferedImage extracted = imageWithMessage;

        for (int i = 0; i < imageWithMessage.getHeight(); i++) {
            for (int j = 0; j < imageWithMessage.getWidth(); j++) {


                int pixelBase = imageWithMessage.getRGB(j, i);
                Color rgbValuesImage = new Color(pixelBase);

                Color extractedColor = getHiddenColor(rgbValuesImage);

                int pixelExtracted = extractedColor.getRGB();

                extracted.setRGB(j, i, pixelExtracted);

            }

            //System.out.println(" ");
        }
        return extracted;
    }

    public static BufferedImage resizeImage(final Image image, int width, int height) {
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        //below three lines are for RenderingHints for better image quality at cost of higher processing time
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(image, 0, 0, width, height, null);
        graphics2D.dispose();
        return bufferedImage;
    }
}