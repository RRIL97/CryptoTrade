package Code.Indicators;


import com.binance.api.client.domain.market.Candlestick;

import java.util.ArrayList;
import java.util.List;

public class WMA {

    private final ArrayList<Double> closingPrices;
    private final ArrayList<Double> wmaHistory;
    private final int               timeFrame;


    public WMA(int timeFrame, List<Candlestick> candlestickList , ArrayList<Double> candleCloseValues){
        this.timeFrame     = timeFrame;
        wmaHistory         = new ArrayList<Double>();

        if(candleCloseValues == null) {
            closingPrices = new ArrayList<Double>();
            for (Candlestick candlestick : candlestickList)
                closingPrices.add(Double.parseDouble(candlestick.getClose()));
        }else
            closingPrices = candleCloseValues;
        initWma();
    }

    private void initWma(){
           for(int j = timeFrame  ; j < closingPrices.size()  ; j++) {
               double currentWma = 0;
               for (int i = timeFrame   ; i > 0; i--)
                   currentWma = currentWma + closingPrices.get(j - i) * (timeFrame - i + 1);
               currentWma = currentWma / ((timeFrame * (timeFrame + 1.0)) / 2.0);
               wmaHistory.add(currentWma);
           }
    }
    public void printWmas(){
        for (Double aDouble : wmaHistory)
             System.out.println(aDouble + "\n");
    }
    public ArrayList<Double> getWmaHistory(){
             return wmaHistory;
    }
}