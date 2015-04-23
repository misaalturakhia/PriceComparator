package com.misaal.pricecomparator.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Misaal on 21/04/2015.
 */
public class JSONService {

    private static final String LOG_TAG = JSONService.class.getSimpleName();
    private static final int TIMEOUT = 15000; // 15 SECONDS


    /**
     *
     * @param url : URL from which to fetch the JSON response
     * @return : json string of data
     */
    public String getJSONString(String url) throws IOException, MalformedURLException{
        String jsonStr = null;
        java.net.URL u = new URL(url);
        HttpURLConnection c = createConnection(u);
        c.connect();
        int status = c.getResponseCode();
        switch (status) {
            case 200: // SUCCESS CODES
            case 201:
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                jsonStr = sb.toString();
        }
        c.disconnect();
        return jsonStr;
    }


    /**
     * Creates and sets parameters to a HttpURLConnection object
     * @param u
     * @return : httpUrlConnection
     * @throws IOException
     */
    private HttpURLConnection createConnection(URL u) throws IOException {
        HttpURLConnection c = (HttpURLConnection) u.openConnection();
        c.setRequestMethod("GET");
        c.setUseCaches(false);
        c.setAllowUserInteraction(false);
        c.setConnectTimeout(TIMEOUT);
        c.setReadTimeout(TIMEOUT);
        return c;
    }

}
