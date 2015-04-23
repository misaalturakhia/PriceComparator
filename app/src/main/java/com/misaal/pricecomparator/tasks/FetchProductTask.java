package com.misaal.pricecomparator.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.misaal.pricecomparator.utilities.Constants;
import com.misaal.pricecomparator.utilities.JSONService;
import com.misaal.pricecomparator.adapters.StoreAdapter;
import com.misaal.pricecomparator.model.ProductFull;
import com.misaal.pricecomparator.model.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Misaal on 22/04/2015.
 */
public class FetchProductTask extends AsyncTask<String, Void, ProductFull> {

    private static final String BASE_URL =
            "http://api.smartprix.com/simple/v1?type=product_full&key=NVgien7bb7P5Gsc8DWqc&id=";
    private static final String LOG_TAG = FetchProductTask.class.getSimpleName();

    private final Context mContext;
    private final StoreAdapter mAdapter;


    /**
     * Constructor
     * @param context
     * @param adapter
     */
    public FetchProductTask(Context context, StoreAdapter adapter){
        this.mContext = context;
        this.mAdapter = adapter;
    }


    @Override
    protected ProductFull doInBackground(String... strings) {
        String id = strings[0];
        final String url = BASE_URL + id;
        JSONService service = new JSONService();
        String jsonStr = null;
        try {
            jsonStr = service.getJSONString(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            cancel(true);
        }
        if(jsonStr != null){
            return getProductFull(jsonStr);
        }

        return null;
    }


    /**
     * Read the Json String and create a ProductFull object by extracting relevant data
     * @param jsonStr
     * @return
     */
    private ProductFull getProductFull(String jsonStr) {
        try {
            JSONObject reader = new JSONObject(jsonStr);
            String resultStatus = reader.getString(Constants.ARG_REQUEST_STATUS);
            if(!resultStatus.equals(Constants.RESULT_SUCCESS)){
                cancel(true);
                return null;
            }
            JSONObject result = reader.getJSONObject(Constants.ARG_REQUEST_RESULT);
            String name = result.getString("name");
            String imageUrl = result.getString("img_url");
            JSONArray array = result.getJSONArray("prices");
            List<Store> storeList = new ArrayList<>();
            for(int i = 0; i < array.length(); i++){
                JSONObject priceData = array.getJSONObject(i);
                String logoUrl = priceData.getString("logo");
                int price = priceData.getInt("price");
                String link = priceData.getString("link");
                storeList.add(new Store(logoUrl, price, link));
            }
            return new ProductFull(name, imageUrl, storeList);
        } catch (JSONException e) {
            cancel(true);
            return null;
        }
    }
}
