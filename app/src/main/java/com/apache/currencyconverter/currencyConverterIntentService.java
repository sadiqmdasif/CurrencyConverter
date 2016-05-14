package com.apache.currencyconverter;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class currencyConverterIntentService extends IntentService {

    String data="";


    public currencyConverterIntentService() {
        super("currencyConverterIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {



            String url = intent.getStringExtra("hrhafizSite");

            Log.d("Received", "url="+url);
            try {
                data = downloadUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("com.apache.currencyconverter.currencyConverterIntentService");
            broadcastIntent.putExtra("siteData", data);
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.sendBroadcast(broadcastIntent);
            }

    }
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        }catch(Exception e){
            Log.d("Exception fetching", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        Log.d("Sitedata \n",data);
        return data;
    }

}
