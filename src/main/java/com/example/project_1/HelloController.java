package com.example.project_1;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Author William Eteta Letshu
 */
public class HelloController {
    @FXML
    private ImageView imageViewBefore;
    @FXML
    private ImageView imageViewAfter;
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
    String NOISE_REDUCED_PATH = "src/main/resources/com/example/Audio/output_audio.wav";
    String EQUALIZED_PATH = "src/main/resources/com/example/Audio/equalized_audio.wav";
    private boolean PERFORMED_NOISE_REDUCTION = false;

    /**
     * This function performs noise reduction
     */
    @FXML
    protected boolean noiseReduction() throws UnsupportedAudioFileException, IOException {
        imageViewBefore.setVisible(true);
        imageViewAfter.setVisible(true);
        System.out.println("Performed Noise Reduction");
        if (selected == null){
            System.out.println("File empty");
            Utils.showAlert(Alert.AlertType.ERROR,"Missing File","Choose a audio file first!");
        }else {
            File reduced_audio = new File(NOISE_REDUCED_PATH);
            NoiseReduction.applyWienerFilter(selected,reduced_audio, imageViewBefore,imageViewAfter);
            Utils.showAlert(Alert.AlertType.INFORMATION,"DONE!","Noise reduction Completed!");
            PERFORMED_NOISE_REDUCTION = true;
            return true;
        }
        return false;
    }

    /**
     * This function performs equalization
     */
    @FXML
    protected void equalization() throws UnsupportedAudioFileException, IOException {
        System.out.println("Perform equalization");
        imageViewBefore.setVisible(true);
        imageViewAfter.setVisible(true);
        if (!PERFORMED_NOISE_REDUCTION){
            if (selected != null){
                File equalized = new File(EQUALIZED_PATH);
                Equalization.applyParametricEqualization(selected,equalized,imageViewBefore,imageViewAfter);
                Utils.showAlert(Alert.AlertType.INFORMATION,"DONE!","Equalization Completed!");
            }else{
                Utils.showAlert(Alert.AlertType.ERROR,"Missing File","Choose audio file");
            }
        }else{
            File noise_reduced = new File(NOISE_REDUCED_PATH);
            File equalized = new File(EQUALIZED_PATH);
            Equalization.applyParametricEqualization(noise_reduced,equalized,imageViewBefore,imageViewAfter);
            Utils.showAlert(Alert.AlertType.INFORMATION,"DONE!","Equalization Completed!");
        }
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

    @FXML
    protected void PlayAudio(){
        if (new File(NOISE_REDUCED_PATH).exists()){
            Utils.playAudio(NOISE_REDUCED_PATH);
        }else {
            Utils.showAlert(Alert.AlertType.ERROR,"Missing File","Perform noise reduction first");
        }
    }

    @FXML
    protected void PlayEqualizedAudio(){
        if (new File(EQUALIZED_PATH).exists()){
            Utils.playAudio(EQUALIZED_PATH);
        }else {
            Utils.showAlert(Alert.AlertType.ERROR,"Missing File","Perform equalization first");
        }

    }

}