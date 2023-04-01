package com.example.project_1;

import javafx.scene.image.ImageView;
import org.jtransforms.fft.DoubleFFT_1D;
import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Author William Eteta Letshu
 */

public class NoiseReduction {

    private static String NOISE_REDUCED_PATH_IMG = "src/main/resources/com/example/images/";

    /**
     * Applies the Wiener filtering algorithm for noise reduction on a given audio file and saves the filtered audio to a new file.
     *
     * @param inputFile The input audio file containing the noisy audio.
     * @param outputFile The output audio file where the filtered audio will be saved.
     * @throws IOException If there is an error reading or writing the audio files.
     * @throws UnsupportedAudioFileException If the input audio file format is not supported.
     */
    public static void applyWienerFilter(File inputFile, File outputFile, ImageView Older, ImageView New) throws IOException, UnsupportedAudioFileException {
        // Read the input audio file
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(inputFile);
        AudioFormat format = inputStream.getFormat();
        int numChannels = format.getChannels();
        byte[] audioBytes = inputStream.readAllBytes();
        int numSamples = audioBytes.length / (format.getSampleSizeInBits() / 8) / numChannels;

        // Convert audio data to double arrays
        double[][] audioData = new double[numChannels][numSamples];
        ByteBuffer buffer = ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < numSamples; i++) {
            for (int j = 0; j < numChannels; j++) {
                audioData[j][i] = buffer.getShort() / 32768.0;
            }
        }

        Utils.Update_Image(Older,audioData,NOISE_REDUCED_PATH_IMG+"noise");

        // Apply the Wiener filter to each channel
        DoubleFFT_1D fft = new DoubleFFT_1D(numSamples);
        for (int i = 0; i < numChannels; i++) {
            wienerFilter(audioData[i], fft);
        }

        Utils.Update_Image(New,audioData,NOISE_REDUCED_PATH_IMG+"noise_reduced");

        // Convert double arrays back to byte arrays
        byte[] outputBytes = new byte[audioBytes.length];
        buffer = ByteBuffer.wrap(outputBytes).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < numSamples; i++) {
            for (int j = 0; j < numChannels; j++) {
                buffer.putShort((short) (audioData[j][i] * 32768));
            }
        }

        // Write the output audio file
        AudioInputStream outputStream = new AudioInputStream(
                new ByteArrayInputStream(outputBytes),
                format,
                inputStream.getFrameLength()
        );
        AudioSystem.write(outputStream, AudioFileFormat.Type.WAVE, outputFile);
    }

    /**
     * Applies the Wiener filtering algorithm for noise reduction on the given audio data using the provided FFT instance.
     *
     * @param audioData The input audio data array containing the noisy audio samples. The filtered audio data will be written back to this array.
     * @param fft An instance of DoubleFFT_1D from the JTransforms library, initialized with the same length as the audio data array.
     */
    private static void wienerFilter(double[] audioData, DoubleFFT_1D fft) {
        
        // Calculate the power spectrum
        double[] powerSpectrum = new double[audioData.length * 2];
        System.arraycopy(audioData, 0, powerSpectrum, 0, audioData.length);
        fft.realForwardFull(powerSpectrum);

        // Compute the magnitude of the FFT
        double[] magnitude = new double[audioData.length];
        for (int i = 0; i < audioData.length; i++) {
            magnitude[i] = Math.sqrt(Math.pow(powerSpectrum[2 * i], 2) + Math.pow(powerSpectrum[2 * i + 1], 2));
        }

        // Estimate the noise using the first few samples (assuming they contain noise)
        int noiseSamples = audioData.length / 10;
        double noiseEstimate = 0;
        for (int i = 0; i < noiseSamples; i++) {
            noiseEstimate += magnitude[i] * magnitude[i];
        }
        noiseEstimate /= noiseSamples;

        // Calculate the gain using G(k, i) = ξ(k, i) / (1 + ξ(k, i))
        double[] gain = new double[audioData.length];
        for (int i = 0; i < audioData.length; i++) {
            double aPrioriSNR = magnitude[i] * magnitude[i] / noiseEstimate;
            gain[i] = aPrioriSNR / (1 + aPrioriSNR);
        }

        // Apply the Wiener filter using the gain function
        for (int i = 0; i < audioData.length; i++) {
            powerSpectrum[2 * i] *= gain[i];
            powerSpectrum[2 * i + 1] *= gain[i];
        }

        // Calculate the inverse FFT
        fft.realInverseFull(powerSpectrum, true);

        // Copy the filtered data back to the audioData array
        System.arraycopy(powerSpectrum, 0, audioData, 0, audioData.length);
    }
    
}
