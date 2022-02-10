package com.weatherallgregator.service;

import com.weatherallgregator.dto.DatePressure;
import com.weatherallgregator.service.forecast.OpenWeatherForecastService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PressurePlotService {

    private final OpenWeatherForecastService forecastService;

    public PressurePlotService(final OpenWeatherForecastService service) {
        this.forecastService = service;
    }

    public InputStream pressurePlot() {
        List<DatePressure> datePressures = forecastService.getPressures();

        XYDataset dataset = createDataset(datePressures);
        var chartTitle = "График давления на " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "","Давление, мм рт. ст.", dataset);
        XYPlot plot = chart.getXYPlot();
        plot.getRangeAxis().setRange(700, 800);
        annotatePlot(plot, dataset, datePressures);

        return imageStreamFromChart(chart);
    }

    private InputStream imageStreamFromChart(final JFreeChart chart) {
        BufferedImage bufferedImage = chart.createBufferedImage(400, 400);
        var stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(stream.toByteArray());
    }

    private void annotatePlot(final XYPlot xyPlot, final XYDataset dataset, List<DatePressure> datePressures) {
        for (int seriesInd = 0; seriesInd < dataset.getSeriesCount(); seriesInd++) {
            for (int itemInd = 0; itemInd < dataset.getItemCount(seriesInd); itemInd++) {
                var annotationText = datePressures.get(itemInd).getDate().format(DateTimeFormatter.ofPattern("dd.MM"));
                var annotation = new XYTextAnnotation(annotationText, dataset.getXValue(seriesInd, itemInd), dataset.getYValue(seriesInd, itemInd));
                annotation.setY(annotation.getY() + 5);
                annotation.setFont(annotation.getFont().deriveFont(12f));

                xyPlot.addAnnotation(annotation);
            }
        }
    }

    private XYDataset createDataset(List<DatePressure> datePressures) {
        var series = new XYSeries("График давления");
        int i = 1;
        for (var datePressure : datePressures) {
            series.add(i++, datePressure.getPressure());
        }

        var dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        return dataset;
    }
}
