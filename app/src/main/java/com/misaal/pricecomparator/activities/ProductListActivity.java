package com.misaal.pricecomparator.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.misaal.pricecomparator.R;
import com.misaal.pricecomparator.adapters.ProductsAdapter;
import com.misaal.pricecomparator.model.Product;
import com.misaal.pricecomparator.tasks.FetchProductsTask;
import com.misaal.pricecomparator.utilities.Constants;
import com.misaal.pricecomparator.utilities.UtilityMethods;

import java.util.ArrayList;

/**
 * Displays a list of products
 */
public class ProductListActivity extends ActionBarActivity {

    private static final String BASE_SEARCH_URL =
            "http://api.smartprix.com/simple/v1?type=search&key=NVgien7bb7P5Gsc8DWqc";

    private ProductsAdapter mAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptyTv;
    private ListView mProductsLv;
    private String mTitle;
    private String mUrl;
    private boolean isCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        Intent intent = getIntent();
        if(intent != null){
            // holds the search string or category
            mTitle = intent.getStringExtra(Constants.ARG_CATEGORY);
            // the url which supplies the data
            mUrl = intent.getStringExtra(Constants.ARG_URL);
            // whether mTitle is a category or a search string
            isCategory = intent.getBooleanExtra(Constants.ARG_IS_CATEGORY, false);
        }else{
            startActivity(new Intent(this, CategoriesActivity.class));
        }

        // the textview that holds the title of the productlist (category, search string etc)
        final TextView titleTv = (TextView) findViewById(R.id.product_list_title);
        titleTv.setText(mTitle);

        final EditText searchTf = (EditText)findViewById(R.id.products_search_tf);
        searchTf.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if(id == EditorInfo.IME_ACTION_SEARCH){
                    searchAction(searchTf, titleTv);
                }
                return false;
            }
        });

        final Context context = this;
        Button searchBtn = (Button)findViewById(R.id.products_search_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAction(searchTf, titleTv);
            }
        });

        LinearLayout pbLayout = (LinearLayout)findViewById(R.id.products_progress_layout);
        mProgressBar = (ProgressBar)findViewById(R.id.products_list_pb);
        mProgressBar.setVisibility(View.VISIBLE);
        mEmptyTv = (TextView) findViewById(R.id.products_empty_tv);

        // the listview used to display the products
        mProductsLv = (ListView) findViewById(R.id.product_list_view);
        mProductsLv.setEmptyView(pbLayout);
        // handle clicking on a list item
        mProductsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // get id of the product clicked
                String id = ((Product)mAdapter.getItem(position)).getId();
                getFragmentManager().beginTransaction().addToBackStack(null).commit();
                Intent productIntent = new Intent(context, ProductActivity.class);
                productIntent.putExtra(Constants.ARG_CATEGORY, mTitle);
                productIntent.putExtra(Constants.ARG_URL, mUrl);
                productIntent.putExtra(Constants.ARG_ID, id);
                startActivity(productIntent);
            }
        });

        // the list adapter which displays each product as an item in the listview
        mAdapter = new ProductsAdapter(this, new ArrayList<Product>());
        mProductsLv.setAdapter(mAdapter);
        // fetch products from the server
        fetchProducts(mUrl);
    }


    /**
     *
     * @param searchTf
     * @param titleTv
     */
    private void searchAction(EditText searchTf, TextView titleTv) {
        if(!UtilityMethods.isNetworkConnected(this)){
            UtilityMethods.showShortToast(this, R.string.no_internet);
            return;
        }
        String q = searchTf.getText().toString();
        if(q.length() > 1){
            String url = createSearchUrl(q);
            titleTv.setText(createTitleText(q));
            fetchProducts(url);
        }

    }


    /**
     * creates text to be displayed above the list of products
     * @param q : the serach string
     * @return :
     */
    private String createTitleText(String q) {
        StringBuilder builder = new StringBuilder();
        if(isCategory){
            builder.append(mTitle+", ");
        }
        builder.append(q);
        return builder.toString();
    }

    /** Creates the search url
     *
     * @param q
     * @return
     */
    private String createSearchUrl(String q) {
        StringBuilder builder = new StringBuilder();
        builder.append(BASE_SEARCH_URL);
        if(isCategory){
            builder.append("&category=");
            builder.append(mTitle);
        }
        builder.append("&q=");
        builder.append(q);
        return builder.toString();
    }


    /**
     * If the device is connected to the internet, make a call to mUrl to fetch the JSON that providdes data
     * of each product corresponding to the search or category
     */
    private void fetchProducts(String url) {
        if(!UtilityMethods.isNetworkConnected(this)){
            UtilityMethods.showShortToast(this, R.string.no_internet);
            mProgressBar.setVisibility(View.GONE);
            mEmptyTv.setText(R.string.no_internet);
            return;
        }
        FetchProductsTask task = new FetchProductsTask(this, mAdapter, mProgressBar, mEmptyTv);
        task.execute(url);

    }

}
