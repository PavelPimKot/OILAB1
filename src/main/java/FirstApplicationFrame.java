import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import java.awt.*;
import java.util.List;

public class FirstApplicationFrame extends ApplicationFrame {
    public FirstApplicationFrame(String title, List<Double> xSeries, List<Double> ySeries, String lowLineTitle, String highLineTitle) {
        super(title);
        var series = new XYSeries(title);
        for (int i = 0; i < xSeries.size(); ++i) {
            series.add(xSeries.get(i), ySeries.get(i));
        }
        var data = new XYSeriesCollection(series);
        var chart = ChartFactory.createXYLineChart(
                title,
                lowLineTitle,
                highLineTitle,
                data,
                PlotOrientation.VERTICAL,
                false,
                false,
                false
        );
        var chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(1000, 500));
        setContentPane(chartPanel);
    }

    public void showFrame() {
        pack();
        setVisible(true);
    }
}
