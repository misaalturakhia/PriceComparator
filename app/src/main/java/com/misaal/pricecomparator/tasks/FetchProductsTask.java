package com.misaal.pricecomparator.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.misaal.pricecomparator.utilities.Constants;
import com.misaal.pricecomparator.utilities.JSONService;
import com.misaal.pricecomparator.R;
import com.misaal.pricecomparator.utilities.UtilityMethods;
import com.misaal.pricecomparator.adapters.ProductsAdapter;
import com.misaal.pricecomparator.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Misaal on 21/04/2015.
 */
public class FetchProductsTask extends AsyncTask<String, Void, List<Product>> {

    private static final String LOG_TAG = FetchProductsTask.class.getSimpleName();
    private final Context mContext;
    private final ProductsAdapter mAdapter;
    private final ProgressBar mProgressBar;
    private final TextView mEmptyTv;


    /**
     * Constructor
     * @param context
     */
    public FetchProductsTask(Context context, ProductsAdapter adapter, ProgressBar pb, TextView emptyView){
        this.mContext = context;
        this.mAdapter = adapter;
        this.mProgressBar = pb;
        this.mEmptyTv = emptyView;
    }

    @Override
    protected void onPreExecute() {
        mAdapter.clearAll();
        mProgressBar.setVisibility(View.VISIBLE);
        mEmptyTv.setText(R.string.fetching_products);
    }

    @Override
    protected void onCancelled(List<Product> products) {
        mProgressBar.setVisibility(View.GONE);
        mEmptyTv.setText(R.string.couldnt_get_products);
        UtilityMethods.showShortToast(mContext, R.string.couldnt_get_products);
    }


    @Override
    protected List<Product> doInBackground(String... strings) {
        String url = strings[0];
        JSONService service = new JSONService();
        String jsonStr = null;
        try {
            // fetch the response json from the given url
            jsonStr = service.getJSONString(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            cancel(true);
        }
        if(jsonStr != null){
            return getProducts(jsonStr);
        }

        return null;
    }


    /**
     *
     * @param jsonStr
     * @return
     */
    private List<Product> getProducts(String jsonStr) {
        List<Product> productList = new ArrayList<>();
        JSONObject reader = null;
        try {
            reader = new JSONObject(jsonStr);
            String resultStatus = reader.getString(Constants.ARG_REQUEST_STATUS);
            if(!resultStatus.equals(Constants.RESULT_SUCCESS)){
                cancel(true);
                return null;
            }
            JSONObject result = reader.getJSONObject(Constants.ARG_REQUEST_RESULT);
            JSONArray resultArray = result.getJSONArray("results");
            for(int i = 0;i < resultArray.length(); i++){
                JSONObject productJson = (JSONObject) resultArray.get(i);
                Product product = getProduct(productJson);
                productList.add(product);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return productList;
    }


    /**
     * Creates Product objects from the data in the JSONObject that holds the products data
     * @param productJson : JSONObject corresponding to the product
     * @return : Product
     * @throws JSONException
     */
    private Product getProduct(JSONObject productJson) throws JSONException {
        String id = productJson.getString("id");
        String category = productJson.getString("category");
        String name = productJson.getString("name");
        int price = productJson.getInt("price");
        String brand = productJson.getString("brand");
        String imgUrl = productJson.getString("img_url");

        return new Product(id, category, name, price, brand, imgUrl);
    }

    @Override
    protected void onPostExecute(List<Product> productList) {
        mProgressBar.setVisibility(View.GONE);
        mEmptyTv.setText(R.string.no_products);
        if(productList != null){
            mAdapter.addAll(productList);
        }
    }
}
