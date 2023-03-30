package com.example.project_1;

import org.jtransforms.fft.DoubleFFT_1D;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Equalization {

    /**
     * Applies parametric equalization to a given audio file and saves the equalized audio to a new file. The equalization is performed
     * by applying a gain to a specific frequency range centered around a center frequency.
     *
     * @param inputFile The input audio file containing the audio that needs to be equalized.
     * @param outputFile The output audio file where the equalized audio will be saved.
     * @throws IOException If there is an error reading or writing the audio files.
     * @throws UnsupportedAudioFileException If the input audio file format is not supported.
     */
    public static void applyParametricEqualization(File inputFile, File outputFile) throws IOException, UnsupportedAudioFileException {
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

        // Apply the parametric equalizer to each channel
        DoubleFFT_1D fft = new DoubleFFT_1D(numSamples);
        for (int i = 0; i < numChannels; i++) {
            parametricEqualization(audioData[i], fft);
        }

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
     * Applies parametric equalization on the given audio data using the provided FFT instance. The equalization is performed
     * by applying a gain to a specific frequency range centered around the center frequency.
     *
     * @param audioData The input audio data array containing the audio samples. The equalized audio data will be written back to this array.
     * @param fft An instance of DoubleFFT_1D from the JTransforms library, initialized with the same length as the audio data array.
     *          This instance is used to perform the FFT and inverse FFT operations within the function.
     */
    private static void parametricEqualization(double[] audioData, DoubleFFT_1D fft) {
        // Apply FFT
        double[] powerSpectrum = new double[audioData.length * 2];
        System.arraycopy(audioData, 0, powerSpectrum, 0, audioData.length);
        fft.realForwardFull(powerSpectrum);

        // Define equalization parameters
        double centerFrequency = 1000.0; // Hz
        double bandwidth = 200.0; // Hz
        double gain = 6.0; // dB

        // Convert gain from dB to linear
        double linearGain = Math.pow(10, gain / 20);

        // Apply parametric equalization
        int numSamples = audioData.length;
        double sampleRate = 44100.0;
        for (int i = 0; i < numSamples / 2; i++) {
            double frequency = (double) i * sampleRate / numSamples;
            double distanceToCenter = Math.abs(frequency - centerFrequency);

            // Calculate gain for the current frequency
            double currentGain = 1.0;
            if (distanceToCenter < bandwidth / 2) {
                currentGain = linearGain;
            }

            // Apply gain to the power spectrum
            powerSpectrum[2 * i] *= currentGain;
            powerSpectrum[2 * i + 1] *= currentGain;
        }

        // Calculate the inverse FFT
        fft.realInverseFull(powerSpectrum, true);

        // Copy the filtered data back to the audioData array
        System.arraycopy(powerSpectrum, 0, audioData, 0, audioData.length);
    }


    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        File inputFile = new File("src/main/resources/com/example/Audio/output_audio.wav");
        File outputFile = new File("src/main/resources/com/example/Audio/equalized_audio.wav");
        applyParametricEqualization(inputFile, outputFile);
    }
}
