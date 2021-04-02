package Code.Indicators;

import Code.Helpers.Connection;
import Code.Helpers.Volume;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class VolumeAggr implements Volume {

    private final Gson g;
    private final long openTime;
    private final long closeTime;

    private long totalBuyPower;
    private long totalSellPower;
    private long numBuying;
    private long numSelling;

    public static class info    {
        private Date   time;
        private Double close;
        public int     count;
        private int    count_buy;
        private int    count_sell;
        private String exchange;
        private double high;
        private double liquidation_buy;
        private double liquidation_sell;
        private double low;
        private double open;
        public String  pair;
        private double vol;
        private double vol_buy;
        public double  vol_sell;
    }
    public static class infoHolder {
        @SerializedName("format")    protected String format;
        @SerializedName("results")   protected ArrayList<info> results;
    }
    public VolumeAggr(long openTime, long closeTime){

        this.openTime  = openTime;
        this.closeTime = closeTime;
        g = new Gson();
        parseData();
    }

    public long getTotalBuyVolume(){
        return totalBuyPower;
    }
    public long getTotalSellVolume(){
        return totalSellPower;
    }

    private void parseData()   {
        try {
            String pageSrc          = Connection.getWebsiteSource("https://api.aggr.trade/btcusd/historical/"+openTime+"/"+closeTime+"/10000");

            infoHolder generalData  = g.fromJson(pageSrc, infoHolder.class);
            ArrayList<info> results = generalData.results;

            for (info res : results) {
                totalBuyPower   += res.vol_buy;
                totalSellPower  += res.vol_sell;
                numBuying       += res.count_buy;
                numSelling      += res.count_sell;
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
