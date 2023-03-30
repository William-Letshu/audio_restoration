package com.example.project_1;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class HelloController {
    @FXML
    private ImageView New;
    @FXML
    private ImageView Older;
    @FXML
    private Label welcomeText;
    @FXML
    private Button Equalize;
    @FXML
    private Button Noise;
    @FXML
    private Button NoiseEqual;
    @FXML
    private Button choose_file;
    
    File selected;

    protected byte[] convertDoubleToByteArray(double[] doubles) {
        ByteBuffer bb = ByteBuffer.allocate(doubles.length * 8);
        for (double d : doubles) {
            bb.putDouble(d);
        }
        return bb.array();
    }
    /**
     * This function performs noise reduction
     */
    @FXML
    protected void noiseReduction() throws UnsupportedAudioFileException, IOException {
        System.out.println("Performed Noise Reduction");
        if (selected == null){
            System.out.println("File empty");
        }else{
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(selected);
            int numFrames = (int) audioIn.getFrameLength();
            int numChannels = audioIn.getFormat().getChannels();
            int bytesPerFrame = audioIn.getFormat().getFrameSize();
            int sampleRate = (int) audioIn.getFormat().getSampleRate();
            byte[] data = new byte[numFrames * bytesPerFrame];
            audioIn.read(data);
            audioIn.close();

            double[] noisySignal = new double[numFrames * numChannels];
            
           for (int i = 0; i < numFrames; i++) {
               for (int j = 0; j < numChannels; j++) {
                   int idx = i * bytesPerFrame + j * 2;
                   noisySignal[i * numChannels + j] = ((data[idx+1] & 0xff) << 8) | (data[idx] & 0xff);
               }
           }
           KalmanFiltering kf = new KalmanFiltering(numChannels);
           for (int i = 0; i < numFrames; i++) {
               double[] z = new double[numChannels];
               for (int j = 0; j < numChannels; j++) {
                   z[j] = noisySignal[i * numChannels + j];
               }
               kf.update(z);
               double[] state = kf.getState();
               for (int j = 0; j < numChannels; j++) {
                   noisySignal[i * numChannels + j] = state[j];
               }
           }

           System.out.println("Done");
           System.out.println(noisySignal.length);
           Older.setImage(null);

        }

    }

    /**
     * This function performs equalization
     */
    @FXML
    protected void equalization(){
        System.out.println("Perform equalization");

    }

    /**
     * This function performs both equalization and noise reduction
     */
    @FXML
    protected void perform_equalization_noise() throws UnsupportedAudioFileException, IOException {
        noiseReduction();
        equalization();
        System.out.println("Perform both Equalization and Noise reduction");
    }

    /**
     * This method is used to choose a file
     * And store the selected file in variable called "selected"
     */
    @FXML
    protected void choose_file(){
        System.out.println("Choosing file");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose your audio file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files","*.wav"));
        selected = fileChooser.showOpenDialog(new Stage());
        if (selected != null){
            System.out.println("File has been successfully loaded");
        }
    }

}