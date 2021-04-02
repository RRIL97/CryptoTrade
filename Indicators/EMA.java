package Code.Indicators;

import com.binance.api.client.domain.market.Candlestick;

import java.util.ArrayList;
import java.util.List;

public class EMA {

    private final ArrayList<Double> closingPrices;
    private final ArrayList<Double> emaHistory;
    private double                  multiplyValue = 0.0;
    private final int               timeFrame;
    private double                  currentEma = 0;


    public EMA(int timeFrame, List<Candlestick> candlestickList , ArrayList<Double> valuesForEma){
        this.timeFrame     = timeFrame;
        this.multiplyValue = 2.0/ (1+timeFrame);
        this.emaHistory         = new ArrayList<Double>();

        if(valuesForEma == null) {
            this.closingPrices = new ArrayList<Double>();
            for (Candlestick candlestick : candlestickList)
               closingPrices.add(Double.parseDouble(candlestick.getClose()));
        }else{
            this.closingPrices = valuesForEma;
        }
        initEma();
    }

    private void initEma(){
        for(int i = 0 ; i < timeFrame  ; i ++)
               currentEma += closingPrices.get(i).intValue();

        currentEma /= timeFrame;
        emaHistory.add(currentEma);

        for(int i = timeFrame ; i < closingPrices.size() -1 ; i ++ )
               calculateEma(closingPrices.get(i)); //Recursive Calculation
    }

    private void calculateEma(double newCandlePrice)
    {
        currentEma = (newCandlePrice - currentEma) * multiplyValue + currentEma;
        emaHistory.add(currentEma);
    }

    public void printEmas(){
        for (Double aDouble : emaHistory) System.out.println(aDouble + "\n");
    }
    public ArrayList<Double> getEmaHistory(){
        return emaHistory;
    }
}
