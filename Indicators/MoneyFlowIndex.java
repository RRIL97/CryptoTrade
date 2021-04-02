package Code.Indicators;

import Code.Helpers.Pair;
import com.binance.api.client.domain.market.Candlestick;

import java.util.ArrayList;
import java.util.Date;

public class MoneyFlowIndex {

    private final ArrayList<Candlestick> candles;
    private ArrayList<Pair<Candlestick,Double>>      mfiVals1;
    private ArrayList<Pair<Candlestick,Double>>      mfiVals2;

    private final int slow = 55;
    private final int fast = 9;


    public MoneyFlowIndex(ArrayList<Candlestick> candles){
         this.candles = candles;
        getDoubleMoneyFlow();
    }

    private void getDoubleMoneyFlow(){
        mfiVals1 = getMoneyFlowIndex(slow);
        mfiVals2 = getMoneyFlowIndex(fast);
    }

    public ArrayList<Pair<Candlestick,Double>> getMfiVals1(){
        return mfiVals1;
    }
    public ArrayList<Pair<Candlestick,Double>> getMfiVals2(){
        return mfiVals2;
    }


    public void printMoneyFlowValues(){

        for(int i =0 ; i < Math.min(mfiVals1.size(),mfiVals2.size()); i++)
            System.out.println(new Date(candles.get(candles.size()-i-1).getOpenTime()) + " | " + " MFI "+slow+" : " + mfiVals1.get(i).getElement1() + " | MFI "+fast+" : " + mfiVals2.get(i).getElement1() );
    }

    /*
    Typical Price = (High + Low + Close)/3
    Raw Money Flow = Typical Price x Volume
    Money Flow Ratio = (14-period Positive Money Flow)/(14-period Negative Money Flow)
    Money Flow Index = 100 - 100/(1 + Money Flow Ratio)
    */
    private ArrayList<Pair<Candlestick,Double>> getMoneyFlowIndex(int period){

        ArrayList<Pair<Candlestick,Double>> moneyFlowIndexVals = new ArrayList<>();

        for(int i = candles.size() -1; i >= period; i--)
        {
            double positiveFlow = 0.0;
            double negativeFlow = 0.0;

            for(int p = 0; p < period; p++){
            Candlestick    currentCandle = candles.get(i-p);
            Candlestick    beforeCandle  = candles.get(i-p-1);

            double beforePrice  = Double.parseDouble(beforeCandle.getClose());
            double beforeHigh   = Double.parseDouble(beforeCandle.getHigh());
            double beforeLow    = Double.parseDouble(beforeCandle.getLow());

            double currentHigh  = Double.parseDouble(currentCandle.getHigh());
            double currentLow   = Double.parseDouble(currentCandle.getLow());
            double currentPrice = Double.parseDouble(currentCandle.getClose());

            double beforeVolumeInCandle  = Double.parseDouble(beforeCandle.getVolume());

            double typicalPriceCurrent = ((currentHigh + currentLow + currentPrice) / 3.0);
            double typicalPriceBefore  = ((beforeHigh  + beforeLow  + beforePrice ) / 3.0);

            double moneyFlowBefore  = (typicalPriceBefore * beforeVolumeInCandle);
            if(typicalPriceCurrent > typicalPriceBefore)
                positiveFlow += moneyFlowBefore;
             else
                negativeFlow += moneyFlowBefore;
            }
            double moneyFlowRatio = positiveFlow/negativeFlow;
            double moneyFlowIndex = 100.0 -  ( 100.0 /(1+moneyFlowRatio));
            System.out.println(new Date(candles.get(i).getOpenTime())+"|"+moneyFlowIndex);
            moneyFlowIndexVals.add(new Pair(candles.get(i),moneyFlowIndex));
        }
        return moneyFlowIndexVals;
    }
}
