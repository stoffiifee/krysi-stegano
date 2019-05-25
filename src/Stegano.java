import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class Stegano {


    public static void main(String[] args) {

    BufferedImage image1 = null;
    BufferedImage image2 = null;


    try{
        image1 = ImageIO.read(new File("XXX"));
        image2 = ImageIO.read(new File("YYY"));

    } catch (IOException noImage) {
        System.out.println("Bild konnte nicht geladen werden.");
    }

    }

       public void getScale(BufferedImage image) {

        int widht = image.getWidth();
        int height = image.getHeight();


    }

    public void testSameScale(){
        if()

    }

}
