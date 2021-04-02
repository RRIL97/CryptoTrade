package Code.Indicators;

import Code.Helpers.Pair;
import com.binance.api.client.domain.market.Candlestick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Hull {

    private final int timeFrame;
    private final ArrayList<Candlestick> candlestickList;
    private final ArrayList<Pair<Candlestick,Double>> hullHistory;

    private int sqrtTimeFrame;

    public Hull(int timeFrame, ArrayList<Candlestick> candlestickList) {
        this.timeFrame = timeFrame;
        hullHistory = new ArrayList<Pair<Candlestick,Double>>();

        this.candlestickList = candlestickList;
        initHull();
    }
    public int getSqrtTimeFrame(){
        return sqrtTimeFrame;
    }
    private void initHull() {

        WMA first  = new WMA((int) Math.ceil(timeFrame / 2), candlestickList, null); //4
        WMA second = new WMA(timeFrame, candlestickList, null); //9

        ArrayList<Double> wmaHistoryFirst  = first.getWmaHistory();
        ArrayList<Double> wmaHistorySecond = second.getWmaHistory();

        ArrayList<Double> ansWma = new ArrayList<>();
        int difference = (wmaHistoryFirst.size() - wmaHistorySecond.size()); //5

        int sqrtPeriod = (int) Math.ceil(Math.sqrt(timeFrame)); //3
        this.sqrtTimeFrame = sqrtPeriod;

        for (int i = 0 ; i <wmaHistorySecond.size()  ; i++)
            ansWma.add(2 * wmaHistoryFirst.get(i + difference) - wmaHistorySecond.get(i));

        int counter = 1;
        for(int j = ansWma.size() - 1  ; j  > sqrtPeriod  ; j--) {
           double currentWma = 0;
           for(int i = 1 ; i <= sqrtPeriod; i++)
               currentWma = currentWma + ansWma.get(j - sqrtPeriod + i) *  (i);
           currentWma = currentWma / ((sqrtPeriod * (sqrtPeriod + 1.0)) / 2.0);
           hullHistory.add(new Pair(candlestickList.get(candlestickList.size()-counter),currentWma));
           counter ++;
        }
        Collections.reverse(hullHistory);
    }
    public void printHull(){
        for (Pair<Candlestick, Double> candlestickDoublePair : hullHistory)
            System.out.println(new Date(candlestickDoublePair.getElement0().getOpenTime()) + " |  " + candlestickDoublePair.getElement1());
    }
    public ArrayList<Pair<Candlestick,Double>> getHullHistory(){
        return hullHistory;
    }
}
