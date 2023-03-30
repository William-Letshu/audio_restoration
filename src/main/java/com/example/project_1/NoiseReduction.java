package com.example.project_1;

import org.jtransforms.fft.DoubleFFT_1D;
import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class NoiseReduction {

    /**
     * Applies the Wiener filtering algorithm for noise reduction on a given audio file and saves the filtered audio to a new file.
     *
     * @param inputFile The input audio file containing the noisy audio.
     * @param outputFile The output audio file where the filtered audio will be saved.
     * @throws IOException If there is an error reading or writing the audio files.
     * @throws UnsupportedAudioFileException If the input audio file format is not supported.
     */
    public static void applyWienerFilter(File inputFile, File outputFile) throws IOException, UnsupportedAudioFileException {
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

        // Apply the Wiener filter to each channel
        DoubleFFT_1D fft = new DoubleFFT_1D(numSamples);
        for (int i = 0; i < numChannels; i++) {
            wienerFilter(audioData[i], fft);
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

        // Apply the Wiener filter
        for (int i = 0; i < audioData.length; i++) {
            double gain = Math.max(0, 1 - noiseEstimate / (magnitude[i] * magnitude[i]));
            powerSpectrum[2 * i] *= gain;
            powerSpectrum[2 * i + 1] *= gain;
        }

        // Calculate the inverse FFT
        fft.realInverseFull(powerSpectrum, true);

        // Copy the filtered data back to the audioData array
        System.arraycopy(powerSpectrum, 0, audioData, 0, audioData.length);
    }

//    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
//        File inputFile = new File("src/main/resources/com/example/Audio/Audio.wav");
//        File outputFile = new File("src/main/resources/com/example/Audio/output_audio.wav");
//        applyWienerFilter(inputFile, outputFile);
//    }

}