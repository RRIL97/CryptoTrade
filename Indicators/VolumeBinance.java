package Code.Indicators;

import Code.Helpers.Connection;
import Code.Helpers.Volume;
import com.google.gson.Gson;
public class VolumeBinance implements Volume {

    private final String  coinSymbol;
    private final long    openTime;
    private final long    closeTime;
    private final Gson    g;

    private long          totalBuys  = 0;
    private long          totalSells = 0;

    private static class orderData{
        private long a;
        private double p;
        private double q;
        private long f;
        private long l;
        private long t;
        private boolean m;
        private boolean M;
    }
    public VolumeBinance(String coinSymbol, long openTime, long closeTime){
        this.coinSymbol    = coinSymbol;
        this.openTime = openTime;
        this.closeTime   = closeTime;

        this.g = new Gson();
        parseData();
    }

    public long getTotalBuyVolume(){
        return totalBuys;
    }
    public long getTotalSellVolume(){
        return totalSells;
    }
    /*
       "a": 26129,         // Aggregate tradeId
       "p": "0.01633102",  // Price
       "q": "4.70443515",  // Quantity
       "f": 27781,         // First tradeId
       "l": 27781,         // Last tradeId
       "T": 1498793709153, // Timestamp
       "m": true,          // Was the buyer the maker?
       "M": true           // Was the trade the best price match?
    */

    private void parseData() {
        try {

            String pageSrc = Connection.getWebsiteSource("https://www.binance.com/api/v1/aggTrades?symbol="+coinSymbol+"&startTime="+openTime+"&endTime="+closeTime);
            orderData[] orders = g.fromJson(pageSrc, orderData[].class);

            totalBuys         = 0;
            totalSells        = 0;
            double sellAmount = 0;
            double buyAmount  = 0;


            for (orderData order: orders) {
             if (order.m) {
                 sellAmount = order.q * order.p;
                 totalSells += sellAmount;
             } else {
                 buyAmount = order.q * order.p;
                 totalBuys += buyAmount;
             }
        }
        } catch (Exception ignored) {
            ignored.printStackTrace();

        }
    }

}
