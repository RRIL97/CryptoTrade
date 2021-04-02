package Code.Indicators;

import Code.Helpers.Pair;
import com.binance.api.client.domain.market.Candlestick;

import java.util.ArrayList;

public class RSI {


    private final ArrayList<Candlestick> candles;
    private final ArrayList<Double> candleClosingPrices;
    private final ArrayList<Pair<Candlestick, Double>> calculatedRSIValues;

    public RSI(ArrayList<Candlestick> candlestickList) {
        this.calculatedRSIValues = new ArrayList<Pair<Candlestick, Double>>();
        this.candleClosingPrices = new ArrayList<>();

        for (int i = 0; i < candlestickList.size() - 1; i++)
            candleClosingPrices.add(Double.parseDouble(candlestickList.get(i).getClose()));

        this.candles = candlestickList;
        initRSI();
    }

    public ArrayList<Pair<Candlestick, Double>> getRSIValues() {
        return calculatedRSIValues;
    }

    public void printRSI() {
        for (Pair<Candlestick, Double> calculatedRSIValue : calculatedRSIValues)
            System.out.println(calculatedRSIValue.getElement1());
    }

    private void initRSI() {
        double avgUp   = 0;
        double avgDown = 0;

        int rsiRange = 14;
        ArrayList<Double> profitAndLosses = new ArrayList<>();
        for(int i = 0; i < candleClosingPrices.size()- 1 ;i++)
            profitAndLosses.add(candleClosingPrices.get(i+1)-candleClosingPrices.get(i));

        for(int i = 0; i < rsiRange; i++){
            double change = profitAndLosses.get(i);

            if(change >= 0)
                avgUp += change;
            else
                avgDown += Math.abs(change);
        }

        avgUp   = avgUp   / (double) rsiRange;
        avgDown = avgDown / (double) rsiRange;

        //Smoothing process, Wilders RSI
        double rsiItself = 0.0;

        for (int i = rsiRange ; i < profitAndLosses.size() ; i++) {
           double change = profitAndLosses.get(i);
            if (change > 0) {
                  avgUp = (avgUp * (rsiRange - 1) + change) / (double) rsiRange;
                  avgDown = (avgDown * (rsiRange - 1)) / (double) rsiRange;
               } else {
                  avgDown = (avgDown * (rsiRange - 1) + Math.abs(change)) / (double) rsiRange;
                  avgUp = (avgUp * (rsiRange - 1)) / (double) rsiRange;
             }
            rsiItself = 100 - 100.0 / (1 + avgUp / avgDown);
            calculatedRSIValues.add(new Pair(candles.get(i), rsiItself));
        }
    }
}