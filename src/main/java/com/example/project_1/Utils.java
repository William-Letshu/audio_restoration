package com.example.project_1;

import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.io.File;

/**
 * Author William Eteta Letshu
 */
public class Utils {

    /**
     * Updates the given ImageView with a line graph of the audio data.
     *
     * @param Older        The ImageView to be updated with the audio data graph.
     * @param audioData    A 2D array of audio data, where each row represents a channel,
     *                     and each element within the row represents an audio sample.
     *                     The values should be normalized between -1 and 1.
     */
    public static void Update_Image(ImageView Older, double[][] audioData) {
        WritableImage audioGraphImage = drawAudioData(audioData);
        Older.setImage(audioGraphImage);
        System.out.println("Images done!");
    }

    /**
     * Updates the given ImageView with a line graph of the audio data.
     *
     * @param Older        The ImageView to be updated with the audio data graph.
     * @param audioData    A 1D array of audio data, where each row represents a channel,
     *                     and each element within the row represents an audio sample.
     *                     The values should be normalized between -1 and 1.
     */
    public static void Update_Image(ImageView Older, double[] audioData) {
        WritableImage audioGraphImage = drawAudioData(audioData);
        Older.setImage(audioGraphImage);
        System.out.println("Images done!");
    }

    /**
     * Draws a line graph of the audio data on a WritableImage.
     *
     * @param audioData A 2D array of audio data, where each row represents a channel,
     *                  and each element within the row represents an audio sample.
     *                  The values should be normalized between -1 and 1.
     * @return A WritableImage containing the line graph of the audio data.
     */
    private static WritableImage drawAudioData(double[][] audioData) {
        // Combine audio data from all channels into a single array
        int numChannels = audioData.length;
        int numSamples = audioData[0].length;
        double[] combinedAudioData = new double[numSamples];

        for (int i = 0; i < numSamples; i++) {
            double sampleValue = 0;
            for (int j = 0; j < numChannels; j++) {
                sampleValue += audioData[j][i];
            }
            combinedAudioData[i] = sampleValue / numChannels;
        }

        WritableImage audioGraphImage = new WritableImage(350, 218);
        PixelWriter pixelWriter = audioGraphImage.getPixelWriter();

        // Clear the background
        for (int y = 0; y < 218; y++) {
            for (int x = 0; x < 350; x++) {
                pixelWriter.setColor(x, y, Color.WHITE);
            }
        }

        // Draw the audio data as a line graph
        int prevX = -1;
        int prevY = -1;
        for (int i = 0; i < numSamples; i++) {
            int x = (int) (((double) i / numSamples) * 350);
            int y = (int) ((1.0 - (combinedAudioData[i] + 1) / 2.0) * 218);

            if (prevX != -1 && prevY != -1) {
                // Draw a line between (prevX, prevY) and (x, y) by interpolating points
                double dx = x - prevX;
                double dy = y - prevY;
                double distance = Math.sqrt(dx * dx + dy * dy);
                for (int j = 0; j < distance; j++) {
                    double ratio = j / distance;
                    int interpolatedX = (int) (prevX + ratio * dx);
                    int interpolatedY = (int) (prevY + ratio * dy);

                    // Ensure the coordinates are within the image bounds
                    if (interpolatedX >= 0 && interpolatedX < 350 && interpolatedY >= 0 && interpolatedY < 218) {
                        pixelWriter.setColor(interpolatedX, interpolatedY, Color.BLUE);
                    }
                }
            }

            prevX = x;
            prevY = y;
        }

        return audioGraphImage;
    }

    /**
     * Generates a visual representation of the provided 1D audio data as a WritableImage.
     * The audio waveform is drawn as a line graph on the image.
     *
     * @param audioData A 1D double array representing the audio data samples.
     *                  The values should be in the range of [-1.0, 1.0].
     * @return A WritableImage containing the audio waveform visualization.
     */
    private static WritableImage drawAudioData(double[] audioData) {
        int numSamples = audioData.length;

        WritableImage audioGraphImage = new WritableImage(350, 218);
        PixelWriter pixelWriter = audioGraphImage.getPixelWriter();

        // Clear the background
        for (int y = 0; y < 218; y++) {
            for (int x = 0; x < 350; x++) {
                pixelWriter.setColor(x, y, Color.WHITE);
            }
        }

        // Draw the audio data as a line graph
        int prevX = -1;
        int prevY = -1;
        for (int i = 0; i < numSamples; i++) {
            int x = (int) (((double) i / numSamples) * 350);
            int y = (int) ((1.0 - (audioData[i] + 1) / 2.0) * 218);

            if (prevX != -1 && prevY != -1) {
                // Draw a line between (prevX, prevY) and (x, y) by interpolating points
                double dx = x - prevX;
                double dy = y - prevY;
                double distance = Math.sqrt(dx * dx + dy * dy);
                for (int j = 0; j < distance; j++) {
                    double ratio = j / distance;
                    int interpolatedX = (int) (prevX + ratio * dx);
                    int interpolatedY = (int) (prevY + ratio * dy);

                    // Ensure the coordinates are within the image bounds
                    if (interpolatedX >= 0 && interpolatedX < 350 && interpolatedY >= 0 && interpolatedY < 218) {
                        pixelWriter.setColor(interpolatedX, interpolatedY, Color.BLUE);
                    }
                }
            }

            prevX = x;
            prevY = y;
        }

        return audioGraphImage;
    }


    public static void playAudio(String filePath) {
        // Load the audio file
        Media audioFile = new Media(new File(filePath).toURI().toString());
        // Create a MediaPlayer instance and set it to play the audio file
        MediaPlayer mediaPlayer = new MediaPlayer(audioFile);
        mediaPlayer.volumeProperty().set(1.0);
        mediaPlayer.play();
    }

    public static void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        PauseTransition delay = new PauseTransition(Duration.seconds(10));
        delay.setOnFinished(event -> alert.close());
        delay.play();
        alert.showAndWait();
    }

}
