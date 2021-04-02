package Code.Indicators;

import Code.Helpers.Pair;
import com.binance.api.client.domain.market.Candlestick;

import java.util.ArrayList;

public class SMA {

    private final ArrayList<Double> values;
    private final ArrayList<Pair<Candlestick,Double>> averaged = new ArrayList<>();
    private final ArrayList<Candlestick> candles;
    private final int smaPeriod;

    public SMA(ArrayList<Candlestick> candles,ArrayList<Double> values , int smaPeriod){
       this.values    = values;
       this.smaPeriod = smaPeriod;
       this.candles = candles;

       initSMA();
    }
    public ArrayList<Pair<Candlestick,Double>> getSMA(){
        return averaged;
    }

    public ArrayList<Double> getSMAValues(){
        ArrayList<Double> smaVals = new ArrayList<>();
        for (Pair<Candlestick, Double> candlestickDoublePair : averaged)
            smaVals.add(candlestickDoublePair.getElement1());
        return smaVals;
    }

    private void initSMA(){

        double sum;
        int    count = 1;
        int    candleSize = candles.size();

        for(int i = values.size() - 1; i > smaPeriod + 1 ; i--)
        {
            sum = 0;
            for(int j = 0 ; j < smaPeriod ; j++) {
                sum += values.get(i - j);
            }
            sum  /= smaPeriod;
            averaged.add(new Pair(candles.get(candleSize-count),sum));
            count++;
        }
    }
}
