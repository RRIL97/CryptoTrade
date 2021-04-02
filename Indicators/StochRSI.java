package Code.Indicators;

import Code.Helpers.Pair;
import com.binance.api.client.domain.market.Candlestick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class StochRSI {


    //Rsi - min rsi
    //-------------
    //max rsi - min rsi

    private final int kSmooth = 3;
    private final int dSmooth = 3;
    private final int stochLength;
    private ArrayList<Candlestick> candles;


    private final RSI rsiCalculator;
    private ArrayList<Pair<Candlestick,Double>> smoothedK;
    private ArrayList<Pair<Candlestick,Double>> smoothedD;


    public StochRSI(ArrayList<Candlestick> candles){
        this.rsiCalculator = new RSI(candles);
        this.stochLength   = 14;
        this.candles       = candles;

        initStoch();
    }

    public ArrayList<Pair<Candlestick,Double>> getSmoothedK(){
        return smoothedK;
    }
    public ArrayList<Pair<Candlestick,Double>> getSmoothedD(){
        return smoothedD;
    }

    public void printSmoothed(){
        System.out.println(smoothedK.size() + " | " + smoothedD.size());
        for(int i = 0; i < Math.min(smoothedK.size(),smoothedD.size()) ; i++)
            System.out.println(new Date(smoothedK.get(i + kSmooth + dSmooth - 2).getElement0().getOpenTime())+" | K Value - " + smoothedK.get(i + kSmooth + dSmooth - 1).getElement1() + " | D Value - " + smoothedD.get(i).getElement1());
    }

    private void initStoch(){
       ArrayList<Pair<Candlestick,Double>> rsiValues = rsiCalculator.getRSIValues();
       int numRsiValues                              = rsiValues.size();

       ArrayList<Double> kValues = new ArrayList<>();
       for(int i = numRsiValues -1 ; i > stochLength + 1 ; i--)
       {
           double rsiVal      = rsiValues.get(i).getElement1();
           double lowestRsi   = 200.0;
           double highestRsi  = -200.0;

           for(int j =  0 ; j < stochLength ; j++)
           {
              double currentRSI = rsiValues.get(i-j).getElement1();
               if(currentRSI > highestRsi)
                   highestRsi = currentRSI;
               if(currentRSI < lowestRsi)
                   lowestRsi  = currentRSI;
           }
           /*
           100 x ( Recent Close - Lowest Low(n) / Highest High(n) - Lowest Low(n) )
           */
           double k = 100 * (rsiVal - lowestRsi) / (highestRsi - lowestRsi);
           kValues.add(k);
       }
       Collections.reverse(kValues);

       SMA kSma = new SMA(candles,kValues,kSmooth);
       smoothedK =kSma.getSMA();
       Collections.reverse(smoothedK);

       SMA dSMA = new SMA(candles,kSma.getSMAValues(),dSmooth);
       smoothedD = dSMA.getSMA();
       Collections.reverse(smoothedD);
    }
}
