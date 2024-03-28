import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class CurrencyDetection {

    public static void main(String[] args) {
        // Load the OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Replace with the path to your banknote image
        String imagePath = "D:\\Downloads\\100.jpg";
        detectFakeCurrency(imagePath);
    }

    public static void detectFakeCurrency(String imagePath) {
        // Load the image
        Mat image = Imgcodecs.imread(imagePath);

        if (image.empty()) {
            System.out.println("Image not found.");
            return;
        }

        // Convert the image to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Apply a threshold to create a binary image
        Mat binaryImage = new Mat();
        Imgproc.threshold(gray, binaryImage, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

        // Find contours in the binary image
        MatVector contours = new MatVector();
        Imgproc.findContours(binaryImage, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Iterate through contours
        for (int i = 0; i < contours.size(); i++) {
            Mat contour = contours.get(i);

            // Calculate the area of the contour
            double area = Imgproc.contourArea(contour);

            // If the area is below a certain threshold, consider it a counterfeit
            if (area < 1000) {
                Imgproc.drawContours(image, contours, i, new Scalar(0, 0, 255), 2);
            }
        }

        // Display the result
        BufferedImage resultImage = matToBufferedImage(image);
        displayImage(resultImage, "Counterfeit Detection");
    }

    public static BufferedImage matToBufferedImage(Mat mat) {
        int width = mat.width(), height = mat.height(), channels = mat.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        mat.get(0, 0, sourcePixels);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

        return image;
    }

    public static void displayImage(BufferedImage image, String windowName) {
        ImageIcon icon = new ImageIcon(image);
        JFrame frame = new JFrame(windowName);
        frame.setLayout(new FlowLayout());
        frame.setSize(image.getWidth() + 10, image.getHeight() + 35);

        JLabel label = new JLabel();
        label.setIcon(icon);
        frame.add(label);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
