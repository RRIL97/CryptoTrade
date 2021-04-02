package Code;

import Code.Indicators.MoneyFlowIndex;
import Code.Indicators.RSI;
import Code.Indicators.StochRSI;
import Code.Strategies.Strat;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;

import java.util.ArrayList;
public class Main {

    private  static final String                   coinName = "BTCUSDT";
    private  static BinanceApiRestClient           client;
    private static  ArrayList<Candlestick>         candlesticks;

    private static CandleStickDisplay              displayObject;

    public static void main(String [] args)
    {

          BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("API-KEY", "SECRET");
          client        = factory.newRestClient();

          candlesticks  = new ArrayList<Candlestick>(client.getCandlestickBars(coinName, CandlestickInterval.THREE_MINUTES));
          displayObject = new CandleStickDisplay(coinName,candlesticks,false,false,false,true);
          displayObject.setVisible(true);
          new Thread(feedNewCandles).start();
    }
    static Runnable feedNewCandles = new Runnable(){
        public void run(){
            try{
                while(true){
                    candlesticks = new ArrayList<Candlestick>(client.getCandlestickBars(coinName, CandlestickInterval.THREE_MINUTES));
                    displayObject.updateGraphWithNewState(candlesticks,Double.parseDouble(client.getPrice(coinName).getPrice()));
                    Thread.sleep(100);
                }
            }catch(Exception e){
            }
        }
    };
}
