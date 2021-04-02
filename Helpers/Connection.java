package Code.Helpers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Connection {

    public static String getWebsiteSource(String url){
        try {
            URL page = new URL(url);
            URLConnection urlConnection = page.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            urlConnection.getInputStream()));
            String inputLine;
            StringBuilder output  = new StringBuilder();

            while ((inputLine = in.readLine()) != null)
                 output.append(inputLine);
            in.close();

            return output.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
