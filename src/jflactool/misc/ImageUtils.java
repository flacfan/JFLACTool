package jflactool.misc;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class ImageUtils
{
    public static ImageIcon createScaledImageIcon(byte[] albumArtBytes)
    {
        if (albumArtBytes == null)
        {
            return null;
        }

        Image image = new ImageIcon(albumArtBytes).getImage();

        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
                image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

        double scale = determineImageScale(bufferedImage.getWidth(),
                bufferedImage.getHeight(), 600, 600);

        return new ImageIcon(bufferedImage.getScaledInstance(
                (int) (bufferedImage.getWidth() * scale),
                (int) (bufferedImage.getWidth() * scale),
                Image.SCALE_SMOOTH));
    }

    private static double determineImageScale(int sourceWidth, int sourceHeight,
            int targetWidth, int targetHeight)
    {
        double scaleX = (double) targetWidth / sourceWidth;
        double scaleY = (double) targetHeight / sourceHeight;
        return Math.min(scaleX, scaleY);
    }
}