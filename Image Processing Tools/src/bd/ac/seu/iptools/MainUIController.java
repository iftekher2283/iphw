/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd.ac.seu.iptools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

/**
 *
 * @author kmhasan
 */
public class MainUIController implements Initializable {

    @FXML
    private Label statusLabel;
    @FXML
    private AnchorPane leftPane;
    @FXML
    private AnchorPane rightPane;
    private BufferedImage inputImage;
    private BufferedImage inputImage2;
    private BufferedImage outputImage;
    @FXML
    private Text thresholdText;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inputImage = null;
        outputImage = null;
    }

    @FXML
    private void handleFileOpenAction(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            BufferedImage bufferedImage = ImageIO.read(file);
//            if(inputImage != null){
//                inputImage2 = bufferedImage;
//            }
//            else{
                inputImage = bufferedImage;
           // }
            if (bufferedImage != null) {
                outputImage = null;
            }
            statusLabel.setText("Opened " + file.getName() + " [" + bufferedImage.getWidth() + "x" + bufferedImage.getHeight() + "]");

            displayImage(inputImage, leftPane);
        } catch (IOException ex) {
            Logger.getLogger(MainUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleFileSaveAction(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showSaveDialog(null);
            String format = file.getName().substring(file.getName().indexOf(".") + 1);
            ImageIO.write(outputImage, format, file);
            statusLabel.setText("Saving to " + file.getName() + " format: " + format);
        } catch (IOException ex) {
            Logger.getLogger(MainUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public BufferedImage turnToGrayScale(BufferedImage inputImage){
        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), inputImage.getType());
        for (int c = 0; c < inputImage.getWidth(); c++) {
            for (int r = 0; r < inputImage.getHeight(); r++) {
                int rgb = inputImage.getRGB(c, r);
                int rr = (rgb >> 16) & 0xFF;
                int gg = (rgb >> 8) & 0xFF;
                int bb = (rgb >> 0) & 0xFF;

                int intensity = (int) (rr * 0.72 + gg * 0.21 + bb * 0.07);

                rgb = (intensity << 16) | (intensity << 8) | intensity;
                outputImage.setRGB(c, r, rgb);
            }
        }
        return outputImage;
    }
    @FXML
    private void handleRGBtoGrayscaleAction(ActionEvent event) {
      //  outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), inputImage.getType());
        outputImage = turnToGrayScale(inputImage);
        displayImage(outputImage, rightPane);
    }

    @FXML
    private void handleRotateClockwiseAction(ActionEvent event) {
        outputImage = new BufferedImage(inputImage.getHeight(), inputImage.getWidth(), inputImage.getType());
        for (int c = 0; c < inputImage.getWidth(); c++) {
            for (int r = 0; r < inputImage.getHeight(); r++) {
                int rgb = inputImage.getRGB(c, r);
                outputImage.setRGB(inputImage.getHeight() - 1 - r, c, rgb);
            }
        }
        displayImage(outputImage, rightPane);
    }

    private void displayImage(BufferedImage bufferedImage, AnchorPane anchorPane) {
        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
        ImageView imageView = new ImageView(image);
        anchorPane.getChildren().removeAll();
        anchorPane.getChildren().add(imageView);
    }

    @FXML
    private void handleBoxBlurAction(ActionEvent event) {
        // Implemented by Kazi Fazle Azim Rabi
        System.out.println("inside");

        outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), inputImage.getType());

        int blurKernel[][] = {
/*
            {-1, 0, +1},
            {-1, 0, +1},
            {-1, 0, +1}
*/
            {+1, -1, +1},
            {-1, 0, -1},
            {+1, -1, +1}
                
        };

        int offset = blurKernel.length / 2;

        for (int c = offset; c < inputImage.getWidth() - offset; c++) {
            for (int r = offset; r < inputImage.getHeight() - offset; r++) {
                int sumRed = 0;
                int sumGreen = 0;
                int sumBlue = 0;
                for (int dx = -offset; dx <= +offset; dx++) {
                    for (int dy = -offset; dy <= +offset; dy++) {
                        int newc = c + dx;
                        int newr = r + dy;

                        int newrgb = inputImage.getRGB(newc, newr);
                        int newrr = (newrgb >> 16) & 0xFF;
                        int newgg = (newrgb >> 8) & 0xFF;
                        int newbb = (newrgb >> 0) & 0xFF;

                        int multipliedValue;
                        multipliedValue = blurKernel[dy + offset][dx + offset] * newrr;
                        sumRed += multipliedValue;
                        multipliedValue = blurKernel[dy + offset][dx + offset] * newgg;
                        sumGreen += multipliedValue;
                        multipliedValue = blurKernel[dy + offset][dx + offset] * newbb;
                        sumBlue += multipliedValue;
                    }
                }
/*
                sumRed /= 982;
                sumGreen /= 982;
                sumBlue /= 982;
*/
                int rgb = (sumRed << 16) | (sumGreen << 8) | (sumBlue);
                outputImage.setRGB(c, r, rgb);
                // DIVIDE the sum values by 9
                // pack the new RGBs into one integer
                // put the integer in the new image
            }
        }
        System.out.println("done");
        displayImage(outputImage, rightPane);
    }

    /*
    @FXML
    private void handleBoxBlurAction(ActionEvent event) {
        outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(),
                inputImage.getType());

        int blurKernel[][] = {
            {1, 1, 1},
            {1, 1, 1},
            {1, 1, 1}
        };

        int offset = blurKernel.length / 2;

        for (int c = offset; c < inputImage.getWidth() - offset; c++) {
            for (int r = offset; r < inputImage.getHeight() - offset; r++) {
                int sumRed = 0;
                int sumGreen = 0;
                int sumBlue = 0;
                for (int dx = -offset; dx <= +offset; dx++) {
                    for (int dy = -offset; dy <= +offset; dy++) {
                        int newc = c + dx;
                        int newr = r + dy;

                        int newrgb = inputImage.getRGB(newc, newr);
                        int newrr = (newrgb >> 16) & 0xFF;
                        int newgg = (newrgb >> 8) & 0xFF;
                        int newbb = (newrgb >> 0) & 0xFF;
                        
                        int multipliedValue;
                        multipliedValue = blurKernel[dy + offset][dx + offset] * newrr;
                        sumRed += multipliedValue;
                        multipliedValue = blurKernel[dy + offset][dx + offset] * newgg;
                        sumGreen += multipliedValue;
                        multipliedValue = blurKernel[dy + offset][dx + offset] * newbb;
                        sumBlue += multipliedValue;
                    }
                }
                // DIVIDE the sum values by 9
                // pack the new RGBs into one integer
                // put the integer in the new image
                int avgRed = sumRed / 9;
                int avgGreen = sumGreen / 9;
                int avgBlue = sumBlue / 9;
                
                int newRGB = (avgRed << 16) | (avgGreen << 8) | avgBlue;
                outputImage.setRGB(c, r, newRGB);
            }
        }
        displayImage(outputImage, rightPane);
    }
     */

    @FXML
    private void handleAddImageAction(ActionEvent event) {
        if(inputImage2 != null){
            System.out.println("inside");

            outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), inputImage.getType());

            for (int c = 0; c < inputImage.getWidth(); c++) {
                for (int r = 0; r < inputImage.getHeight(); r++) {
                    int sumRed = 0;
                    int sumGreen = 0;
                    int sumBlue = 0;

                    int rgb1 = inputImage.getRGB(c, r);
                    int rr1 = (rgb1 >> 16) & 0xFF;
                    int gg1 = (rgb1 >> 8) & 0xFF;
                    int bb1 = (rgb1 >> 0) & 0xFF;

                    int rgb2 = inputImage2.getRGB(c, r);
                    int rr2 = (rgb1 >> 16) & 0xFF;
                    int gg2 = (rgb2 >> 8) & 0xFF;
                    int bb2 = (rgb2 >> 0) & 0xFF;

                    sumRed = rr1 + rr2;
                    sumGreen = gg1 + gg2;
                    sumBlue = bb1 + bb2;
                    int extra = 0;

                    if(sumRed > 255){
                        extra = sumRed - 255;
                        sumRed = 255;
                        sumGreen = sumGreen - extra;
                        sumBlue = sumBlue - extra;
                    }
                    if(sumGreen >255){
                        extra = sumGreen - 255;
                        sumRed = sumRed - extra;
                        sumGreen = 255;
                        sumBlue = sumBlue - extra;
                    }
                    if(sumBlue > 255){
                        extra = sumBlue - 255;
                        sumRed = sumRed - extra;
                        sumBlue = sumBlue - extra;
                        sumBlue = 255;
                    }
                    
                    int rgb = (sumRed << 16) | (sumGreen << 8) | (sumBlue);
                    outputImage.setRGB(c, r, rgb);
                }
            }
            System.out.println("done");
            displayImage(outputImage, rightPane);
        }
        else{
            System.out.println("Open another image first");
        }
    }

    @FXML
    private void handleOpenSecondaryImageAction(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            BufferedImage bufferedImage = ImageIO.read(file);
//            if(inputImage != null){
//                inputImage2 = bufferedImage;
//            }
//            else{
                inputImage2 = bufferedImage;
           // }
            if (bufferedImage != null) {
                outputImage = null;
            }
            statusLabel.setText("Opened " + file.getName() + " [" + bufferedImage.getWidth() + "x" + bufferedImage.getHeight() + "]");

            displayImage(inputImage, leftPane);
        } catch (IOException ex) {
            Logger.getLogger(MainUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleCalculateThresholdAction(ActionEvent event) {
        int threshold = 127;
        List<Integer> rgbfr = new ArrayList<>();
        List<Integer> rgbmv = new ArrayList<>();
        List<Integer> rgbcs = new ArrayList<>();
        List<Integer> rgbcfr = new ArrayList<>();
        for(int i = 0; i < 256; i++){
            rgbfr.add(0);
            rgbmv.add(0);
            rgbcs.add(0);
            rgbcfr.add(0);
            
        }
        
        outputImage = turnToGrayScale(inputImage);
      //  outputImage = inputImage;
        for(int c = 0; c < outputImage.getWidth(); c++){
            for(int r = 0; r < outputImage.getHeight(); r++){
                int rgb = outputImage.getRGB(c, r);
                int rr = (rgb >> 16) & 0xFF;
                int gg = (rgb >> 8) & 0xFF;
                int bb = (rgb >> 0) & 0xFF;
              //  rgbfr.set(rr, (rgbfr.get(rgbfr.indexOf(rr)) + 1));
                rgbfr.set(bb, (rgbfr.get(bb) + 1));
            }
        }
        rgbcfr.set(0, rgbfr.get(0));
        System.out.printf("ind: 0; fr: %d; cfr: %d; mv: %d; cmv: %d\n", rgbfr.get(0), rgbcfr.get(0), rgbmv.get(0), rgbcs.get(0));
        for(int j = 1; j < 256; j++){
            rgbmv.set(j, (j * rgbfr.get(j)));
            rgbcs.set(j, (rgbmv.get(j) + rgbcs.get(j - 1)));
            rgbcfr.set(j, (rgbfr.get(j) + rgbcfr.get(j - 1)));
            System.out.printf("ind: %d; fr: %d; cfr: %d; mv: %d; cmv: %d\n", j, rgbfr.get(j), rgbcfr.get(j), rgbmv.get(j), rgbcs.get(j));
        }
        int m1 = rgbcs.get(threshold) / rgbcfr.get(threshold);
        int m2 = (rgbcs.get(255) - rgbcs.get(threshold)) / (rgbcfr.get(255) - rgbcfr.get(threshold));
        threshold = (m1 + m2) / 2;
        thresholdText.setText("Threshold: " + threshold);
        
        int outputImageHeight = inputImage.getWidth() * inputImage.getHeight();
        outputImage = new BufferedImage(255, outputImageHeight, inputImage.getType());
        for (int c = 0; c < 256; c++){
            for (int r = 0; r < outputImageHeight; r++){
                if(outputImageHeight - rgbfr.get(c) < r){
                //    int rgb = 0x000000;
                    outputImage.setRGB(c, r, 255);
                }
                else{
                    int rgb = 0x000001;
                 //   outputImage.setRGB(c, r, rgb);
                }
            }
        }
        displayImage(outputImage, rightPane);
    }

    @FXML
    private void getColorHistogramAction(ActionEvent event) {
        
    }
}
