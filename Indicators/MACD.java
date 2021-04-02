package Code.Indicators;

import Code.Helpers.Pair;
import com.binance.api.client.domain.market.Candlestick;

import java.util.ArrayList;
import java.util.Collections;

public class MACD {

    private final ArrayList<Double>                   emaHistoryFirst;
    private final ArrayList<Double>                   emaHistorySecond;
    private final ArrayList<Pair<Candlestick,Double>> histogramValues;
    private final ArrayList<Double>        macdLine;
    private final ArrayList<Candlestick>   candles;


    private final int longPeriod = 26;
    public MACD(ArrayList<Candlestick> candlestickList){
       this.candles = candlestickList;

       int shortPeriod = 12;
       EMA firstEma  = new EMA(shortPeriod, candlestickList, null);
       EMA secondEma = new EMA(longPeriod, candlestickList , null);

       histogramValues = new ArrayList<>();
       macdLine        = new ArrayList<>();

       emaHistoryFirst  = firstEma.getEmaHistory();
       emaHistorySecond = secondEma.getEmaHistory();
       initMACD();
    }

    public ArrayList<Pair<Candlestick,Double>> getHistogramValues(){
        return histogramValues;
    }

    private void initMACD(){
        for(int i = 0; i < Math.min (emaHistoryFirst.size(), emaHistorySecond.size())-1 ; i++)
          macdLine.add(emaHistoryFirst.get(emaHistoryFirst.size() - i - 1)- emaHistorySecond.get(emaHistorySecond.size() - i - 1));

        Collections.reverse(macdLine);
        int signalPeriod = 9;
        EMA signalLineEma = new EMA(signalPeriod, null, macdLine);
        ArrayList<Double> emaHistorySignal = signalLineEma.getEmaHistory();

        Collections.reverse(emaHistorySignal);
        for(int i = 0  ;i < emaHistorySignal.size()   ; i++)
            histogramValues.add(new Pair(candles.get(i+longPeriod+signalPeriod), macdLine.get(i + signalPeriod) - emaHistorySignal.get(emaHistorySignal.size() -1 -i)));
    }
}
