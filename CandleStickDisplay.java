package Code;

import Code.Helpers.Pair;
import Code.Indicators.*;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import org.jfree.chart.*;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.*;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.*;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class CandleStickDisplay extends JFrame  {

    private       ArrayList<Candlestick>               stockCandles;
    private       XYDataset                            candlesDataSet;
    private final XYPlot                               pricePlot;
    private final JFreeChart                           chart;


    private final StandardXYItemRenderer customRenderer;

    private final int HullSet = 3;

    private final boolean toggleHull;

    private MACD                         macd;
    private ArrayList<Double>            macdCandleValues;

    private final String               stockSymbol;

    public CandleStickDisplay(String stockSymbol ,ArrayList<Candlestick> inputCandles , boolean toggleEma , boolean toggleWma , boolean toggleMACD, boolean toggleHull) {
        super("Crypto Trading Bot");

        this.stockSymbol   = stockSymbol;

        try {
            setIconImage(ImageIO.read(new File("res/icon.png")));
        }catch(Exception ignored){ }
        this.stockCandles = inputCandles;
        this.toggleHull   = toggleHull;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DateAxis    dateAxis         = new DateAxis  ("Date");
        NumberAxis  priceAxis          = new NumberAxis("Price");
        CandlestickRenderer renderer   = new CandlestickRenderer();
        candlesDataSet                 = getData(stockSymbol,stockCandles);
        pricePlot                      = new XYPlot(candlesDataSet, dateAxis, priceAxis, renderer);

        annonationOnGraph = new ArrayList<>();

        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setUpPaint    (new Color(0x00FF00));
        renderer.setDownPaint  (new Color(0xFF0000));
        renderer.setDrawVolume (true);
        priceAxis .setAutoRangeIncludesZero(false);

        renderer.setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_SMALLEST);

        XYDataset wmaData  = null;
        XYDataset emaData  = null;
        XYDataset hullData = null;

        if(toggleEma)
            emaData = createEMADataset(9);
        if(toggleWma)
            wmaData = createWMADataset(9);
        if(this.toggleHull)
            hullData = createHullDataset(9);

        chart = new JFreeChart(stockSymbol, null, pricePlot, false);

        chart.setNotify(true);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize    (new Dimension(600, 300));
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.addChartMouseListener(new org.jfree.chart.ChartMouseListener() {
        @Override
            public void chartMouseClicked(ChartMouseEvent chartMouseEvent) {
                ChartEntity entity = chartMouseEvent.getEntity();
                if(entity != null) {
                    if(entity.getShapeType().equals("rect")) {

                        String toolTip = chartMouseEvent.getEntity().getToolTipText();
                        if (toolTip != null) {
                            toolTip = toolTip.replaceAll(",", "");

                            double high = Double.parseDouble(toolTip.split ("High=")[1].split(" ")[0]);
                            double low  = Double.parseDouble(toolTip.split ("Low=" )[1].split(" ")[0]);

                            for (int i = stockCandles.size() - 1; i >= 0; i--){
                                Candlestick stockCandle = stockCandles.get(i);
                                double currentCandleHigh = Double.parseDouble(stockCandle.getHigh());
                                double currentCandleLow = Double.parseDouble(stockCandle.getLow());
                                if (currentCandleLow == low && currentCandleHigh == high) {
                                     //Found the clicked candle.
                                }
                            }
                        }
                    }
                }
                }
            @Override
            public void chartMouseMoved(ChartMouseEvent chartMouseEvent) {

            }
        });
        this.add(chartPanel);
        customRenderer =  new StandardXYItemRenderer();
        customRenderer.setBaseStroke( new BasicStroke( 3 ) );
        customRenderer.setSeriesStroke( 0, new BasicStroke( 3 ) );

        if(toggleEma) {
            int EMASet = 1;
            pricePlot     .setDataset(EMASet, emaData);
            customRenderer.setSeriesPaint(0, Color.BLACK);
            pricePlot     .setRenderer(EMASet, customRenderer);
        }
        if(toggleWma) {
            int WMASet = 2;
            pricePlot     .setDataset(WMASet, wmaData);
            customRenderer.setSeriesPaint(0, Color.BLACK);
            pricePlot     .setRenderer(WMASet, customRenderer);

        }
        if(this.toggleHull){
            pricePlot     .setDataset(HullSet, hullData);
            customRenderer.setSeriesPaint(0, Color.BLACK);
            pricePlot     .setRenderer(HullSet, customRenderer);
        }

        pricePlot.setRenderer(HullSet, customRenderer);

        this.pack();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        TextTitle topInfoText = new TextTitle("");
        topInfoText.setText("");
        topInfoText.setPosition(RectangleEdge.TOP);

        new Thread(() -> {
                while (true) {
                    try {
                        Candlestick lastCandle      = stockCandles.get(stockCandles.size() - 1);
                        VolumeBinance volumeBiannce = new VolumeBinance(stockSymbol,lastCandle.getOpenTime(),lastCandle.getCloseTime());
                        topInfoText.setText("Last Candle - " + new Date(lastCandle.getOpenTime()) + "\nTotal_Buy_Volume:" + volumeBiannce.getTotalBuyVolume() + "\n" + "Total_Sell_Volume:" + volumeBiannce.getTotalSellVolume());
                        Thread.sleep(300);
                    } catch (Exception ignored) {
                    }
                }
        }).start();

        chart.addSubtitle(topInfoText);
    }

    private void addMTextAnnotation(double x, double y, String label, boolean fadeAway, int sleepTime) {
        String[] lines = label.split("\n");
        ArrayList<XYTextAnnotation> annotations = new ArrayList<>();
        for(int i=0; i < lines.length;  i++){
            XYTextAnnotation annotationLabel = new XYTextAnnotation(lines[i],
                    x,  y + (i) * (100));
            annotationLabel.setOutlineVisible(true);
            annotationLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            annotationLabel.setPaint(Color.WHITE);
            annotationLabel.setBackgroundPaint(Color.BLACK);
            pricePlot.addAnnotation(annotationLabel);
            annotations.add(annotationLabel);
        }
        if(fadeAway) {
            new Thread(() -> {
                try {
                    Thread.sleep(sleepTime);
                    for (XYTextAnnotation s : annotations)
                        pricePlot.removeAnnotation(s);
                } catch (Exception e) {

                }
            }).start();
        }
    }

    public void updateGraphWithNewState(ArrayList<Candlestick> updatedCandleSticks, double price)  {
        candlesDataSet = getData(stockSymbol,updatedCandleSticks);
        stockCandles = updatedCandleSticks;
        
        /*
        Update Candle Prices.. + Add New Candles
        */
        pricePlot.setDataset(candlesDataSet);
        /*
        Update Hull Indicator
        */
        if(toggleHull) {
            XYDataset hullData = createHullDataset(9);
            pricePlot.setDataset(HullSet, hullData);
            customRenderer.setSeriesPaint(0, Color.BLACK);
            pricePlot.setRenderer(HullSet, customRenderer);
        }
        pricePlot.clearRangeMarkers();
        Marker priceMarker = new ValueMarker(price);
        priceMarker.setPaint(Color.red);
        priceMarker.setLabel("Price : " + price);
        priceMarker.setLabelAnchor(RectangleAnchor.BOTTOM_LEFT);
        priceMarker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
        pricePlot.addRangeMarker(priceMarker);
    }


    /*
    INDICATOR DATASETS START
    */
    private XYDataset createEMADataset(int timeFrame) {
        XYSeries series = new XYSeries("EMA");

        EMA emaCalculator = new EMA(timeFrame,stockCandles,null);
        ArrayList<Double> emaHistory = emaCalculator.getEmaHistory();
        for(int i = 1;  i < stockCandles.size() - timeFrame -1 ; i++)
            series.add(stockCandles.get(i+timeFrame+1).getOpenTime(), emaHistory.get(i));
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }
    private XYDataset createWMADataset(int timeFrame) {
        XYSeries series = new XYSeries("WMA");

        WMA wmaCalculator = new WMA(timeFrame,stockCandles,null);
        ArrayList<Double> wmaHistory = wmaCalculator.getWmaHistory();
        int Counter = 0;
        for(int i = timeFrame ;  i < wmaHistory.size() + timeFrame ; i++) {
            series.add(stockCandles.get(i).getOpenTime(), wmaHistory.get(Counter));
            Counter++;
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }
    private XYDataset createHullDataset(int timeFrame) {
        XYSeries series = new XYSeries("Hull");

        Hull hullCalculator = new Hull(timeFrame,stockCandles);
        ArrayList<Pair<Candlestick,Double>> hullHistory = hullCalculator.getHullHistory();

        for (Pair<Candlestick, Double> entry : hullHistory)
            series.add(entry.getElement0().getOpenTime(), entry.getElement1());
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }
    /*
    END
    */
    private AbstractXYDataset getData(String StockName,List<Candlestick> candleSticks) {
        List<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();
        try {
            for (Candlestick currentCandle : candleSticks) {
                double open       = Double.parseDouble(currentCandle.getOpen());
                double high       = Double.parseDouble(currentCandle.getHigh());
                double low        = Double.parseDouble(currentCandle.getLow());
                double close      = Double.parseDouble(currentCandle.getClose());
                double volume     = Double.parseDouble(currentCandle.getVolume());
                OHLCDataItem item = new OHLCDataItem(new Date(currentCandle.getOpenTime()), open, high, low, close, volume);
                dataItems.add(item);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new DefaultOHLCDataset(StockName,dataItems.toArray(new OHLCDataItem[0]));
    }
}