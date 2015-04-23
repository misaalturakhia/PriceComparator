package com.misaal.pricecomparator.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.misaal.pricecomparator.R;
import com.misaal.pricecomparator.adapters.StoreAdapter;
import com.misaal.pricecomparator.model.ProductFull;
import com.misaal.pricecomparator.model.Store;
import com.misaal.pricecomparator.tasks.FetchProductTask;
import com.misaal.pricecomparator.utilities.Constants;
import com.misaal.pricecomparator.utilities.UtilityMethods;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Displays a product
 */
public class ProductActivity extends ActionBarActivity {

    private static final String LOG_TAG = ProductActivity.class.getSimpleName();
    private static final String BASE_SEARCH_URL =
            "http://api.smartprix.com/simple/v1?type=search&key=NVgien7bb7P5Gsc8DWqc&q=";

    private TextView mProductNameTv;
    private ImageView mProductImageIv;
    private TextView mBestPriceTv;
    private TextView mNoOfStoresTv;
    private ListView mStoresLv;
    private StoreAdapter mAdapter;
    private String mId;
    private ProgressBar mProgressBar;
    private TextView mEmptyTv;
    private String mCategory;
    private String mUrl;
    private boolean isCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // dsiplay home button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // get product id enclosed in the intent
        mId = getIntent().getStringExtra(Constants.ARG_ID);
        // get the category text or search tring entered in the ProductListActivity
        mCategory = getIntent().getStringExtra(Constants.ARG_CATEGORY);
        // get the url used to fetch content for the ProductListActivity
        mUrl = getIntent().getStringExtra(Constants.ARG_URL);
        // get if the mCategory text is a category or a search string. Only used to pass back to the previous
        // intent on clicking the home button
        isCategory = getIntent().getBooleanExtra(Constants.ARG_IS_CATEGORY, true);

        final EditText searchTf = (EditText)findViewById(R.id.product_search_tf);
        searchTf.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if(id == EditorInfo.IME_ACTION_SEARCH){
                    search(searchTf);
                }
                return false;
            }
        });

        Button searchBtn = (Button)findViewById(R.id.product_search_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(searchTf);
            }
        });

        // displays the product name
        mProductNameTv = (TextView)findViewById(R.id.product_name_tv);
        // displays product image
        mProductImageIv = (ImageView)findViewById(R.id.product_big_image_iv);
        // displays the best price
        mBestPriceTv = (TextView)findViewById(R.id.product_best_price_tv);
        // displays the number of stores that sell the phone (including those that show 'OUT OF STOCK')
        mNoOfStoresTv = (TextView)findViewById(R.id.product_no_of_stores_tv);
        // displays the list of stores and the prices
        mStoresLv = (ListView)findViewById(R.id.product_stores_lv);
        // the emptyview that notifies the user that there are no items in the list
        mEmptyTv = (TextView)findViewById(R.id.stores_empty_tv);
        // the adapter that displays the stores and their prices in the list
        mAdapter = new StoreAdapter(this, new ArrayList<Store>());
        mStoresLv.setAdapter(mAdapter);
        mStoresLv.setEmptyView(mEmptyTv);
        // fetch data of the product identified by mId
        fetchProductData();
    }


    /** Invokes the search
     *
     * @param searchTf
     */
    private void search(EditText searchTf) {
        if(!UtilityMethods.isNetworkConnected(this)){
            UtilityMethods.showShortToast(this, R.string.no_internet);
            return;
        }
        String q = searchTf.getText().toString();
        if(q.length() > 1){
            String url = BASE_SEARCH_URL + q;
            startActivity(createProductListIntent(q, url, false));
        }
    }


    /**
     * Fetch the data of the product identified by mId
     */
    private void fetchProductData() {
        if(!UtilityMethods.isNetworkConnected(this)){
            UtilityMethods.showShortToast(this, R.string.no_internet);
            // TODO:
            mEmptyTv.setText(R.string.no_internet);
            return;
        }
        FetchProductTask task = new FetchProductTask(this, mAdapter);
        task.execute(mId);

        ProductFull product = null;
        try {
            // get result of the server call
            product = task.get();
            // populate the views in the activity with the data
            populateData(product);
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } catch (ExecutionException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }


    /**
     *
     * @param product
     */
    private void populateData(ProductFull product) {
        // set name of product
        mProductNameTv.setText(product.getName());
        // if connected to the internet, download and fit image into productImgIv using Picasso
        if(UtilityMethods.isNetworkConnected(this)){
            Picasso.with(this).load(product.getImageUrl()).fit().into(mProductImageIv);
        }
        List<Store> stores = product.getStores();
        // calculate best price amongst all the stores
        int bestPrice = findBestPrice(stores);
        if(bestPrice == -1){
            mBestPriceTv.setText("Best Price : None");
        }else{
            mBestPriceTv.setText("Best Price : " + UtilityMethods.createPriceText(bestPrice));
        }

        int noOfStores = stores.size();
        if(noOfStores == 1){
            mNoOfStoresTv.setText("Available at "+stores.size()+" store");
        }else{
            mNoOfStoresTv.setText("Available at "+stores.size()+" stores");
        }
        mAdapter.addAll(stores);
    }


    /**
     * Fetches the lowest price from the input list of stores
     * @param stores : List of store data with prices
     * @return : lowest price
     */
    private int findBestPrice(List<Store> stores) {
        int minPrice = 10000000; // default high value
        if(stores.size() < 1){
            return -1;
        }
        for(Store store: stores){
            int price = store.getPrice();
            if(price < minPrice){
                minPrice = price;
            }
        }
        return minPrice;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            startActivity(createProductListIntent(mCategory, mUrl, isCategory));
        }
        return true;
    }


    /**
     *
     * @param category
     * @param url
     * @param isCat
     * @return
     */
    private Intent createProductListIntent(String category, String url, boolean isCat){
        Intent listIntent = new Intent(this, ProductListActivity.class);
        listIntent.putExtra(Constants.ARG_CATEGORY, category);
        listIntent.putExtra(Constants.ARG_URL, url);
        listIntent.putExtra(Constants.ARG_IS_CATEGORY, isCat);
        return listIntent;
    }

}
