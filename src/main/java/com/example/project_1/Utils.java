package com.example.project_1;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Utils {

    //How to draw a line graph using javafx?

    public static void save_frequency_graph(double[] amplitudes){
        double[] frequencies = new double[amplitudes.length];
        for (int i = 0; i < frequencies.length; i++) {
            frequencies[i] = i;
        }


        // Create dataset
        XYSeries series = new XYSeries("Frequency Spectrum");
        for (int i = 0; i < frequencies.length; i++) {
            series.add(frequencies[i], amplitudes[i]);
        }
        XYDataset dataset = new XYSeriesCollection(series);

        // Create chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Frequency Spectrum",
                "Frequency (Hz)",
                "Amplitude",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Set chart colors and fonts
        chart.setBackgroundPaint(Color.WHITE);
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.BLACK);
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setAutoRangeIncludesZero(false);
        domainAxis.setTickLabelFont(domainAxis.getTickLabelFont().deriveFont(10.0f));
        domainAxis.setLabelFont(domainAxis.getLabelFont().deriveFont(12.0f));
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelFont(rangeAxis.getTickLabelFont().deriveFont(10.0f));
        rangeAxis.setLabelFont(rangeAxis.getLabelFont().deriveFont(12.0f));

        // Set chart renderer
        XYSplineRenderer renderer = new XYSplineRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setPrecision(6);
        plot.setRenderer(renderer);
        // Save chart as PNG image
        int width = 500;   // image width in pixels
        int height = 300;  // image height in pixels
        File file = new File("frequency_spectrum.png");
        try {
            ChartUtils.saveChartAsPNG(file, chart, width, height);
        } catch (IOException e) {
            System.out.println("Error saving chart to PNG: " + e.getMessage());
        }

        System.out.println("Chart saved as " + file.getAbsolutePath());
    }

}
