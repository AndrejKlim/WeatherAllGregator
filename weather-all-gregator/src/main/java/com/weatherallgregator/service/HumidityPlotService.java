package com.weatherallgregator.service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class HumidityPlotService {

    public InputStream humidityPlot() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(73, "one1", "one2");
        dataset.addValue(75, "one1", "one2");
        dataset.addValue(76, "one1", "one2");
        dataset.addValue(77, "one1", "one2");
        dataset.addValue(78, "one1", "one2");

        JFreeChart chart = ChartFactory.createLineChart("Humidity plot", "day",
                "humidity, %", dataset);
        BufferedImage bufferedImage = chart.createBufferedImage(400, 400);
        var stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(stream.toByteArray());
    }
}
