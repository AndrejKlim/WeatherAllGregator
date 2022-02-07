package com.weatherallgregator.service;

import com.weatherallgregator.service.forecast.OpenWeatherForecastService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Map;

@Service
public class PressurePlotService {

    private final OpenWeatherForecastService forecastService;

    public PressurePlotService(final OpenWeatherForecastService service) {
        this.forecastService = service;
    }

    private XYDataset createDataset() {

        var series = new XYSeries("График давления");
        Map<LocalDate, Integer> pressures = forecastService.getPressures());
        int i = 1;
        for (Integer pressure : pressures.values()) {
            series.add(i++, pressure);
        }

        var dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        return dataset;
    }

    public InputStream pressurePlot() {
        XYDataset dataset = createDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("График давления", "день",
                "Давление, мм рт. ст.", dataset);
        XYPlot xyPlot = chart.getXYPlot();
        xyPlot.getRangeAxis().setRange(700, 800);
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
