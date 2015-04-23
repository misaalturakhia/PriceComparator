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

import com.misaal.pricecomparator.R;
import com.misaal.pricecomparator.adapters.CategoriesAdapter;
import com.misaal.pricecomparator.tasks.FetchCategoriesTask;
import com.misaal.pricecomparator.utilities.Constants;
import com.misaal.pricecomparator.utilities.UtilityMethods;

import java.util.ArrayList;


/**
 * Displays a list of categories
 */
public class CategoriesActivity extends ActionBarActivity {

    private static final String BASE_CATEGORY_URL =
            "http://api.smartprix.com/simple/v1?type=search&key=NVgien7bb7P5Gsc8DWqc&category=";

    private static final String BASE_SEARCH_URL =
            "http://api.smartprix.com/simple/v1?type=search&key=NVgien7bb7P5Gsc8DWqc&q=";

    private TextView mEmptyTv;
    private CategoriesAdapter mAdapter;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        final Context context = this;
        // edittext that allows for input of a search string
        final EditText searchTf = (EditText)findViewById(R.id.categories_search_tf);
        searchTf.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if(id == EditorInfo.IME_ACTION_SEARCH){
                    search(searchTf);
                }
                return false;
            }
        });

        // invokes the search
        final Button searchBtn = (Button)findViewById(R.id.category_search_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(searchTf);
            }
        });

        // a layout that shows a progressbar and a message saying 'fetching categories'
        LinearLayout pbLayout = (LinearLayout)findViewById(R.id.categories_progress_layout);
        mProgressBar = (ProgressBar)findViewById(R.id.categories_list_pb);
        mEmptyTv = (TextView) findViewById(R.id.categories_empty_tv);


        // the listview which displays the categories
        ListView categoriesLv = (ListView) findViewById(R.id.category_list);
        // set the mPbLayout as the view when the list is empty
        categoriesLv.setEmptyView(pbLayout);
        categoriesLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                String category = mAdapter.getItem(index);
                String url = BASE_CATEGORY_URL + category;
                // navigate to ProductListActivity to display the products that are results of the search
                startActivity(getProductListIntent(category, url, true));
            }
        });
        // the adapter that displays the categories in the list
        mAdapter = new CategoriesAdapter(this, new ArrayList<String>());
        categoriesLv.setAdapter(mAdapter);
        // fetch the list of categories and add it to the adapter
        fetchCategories();
    }


    /**
     * invokes the search
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
            startActivity(getProductListIntent(q, url, false));
        }
    }


    /**
     * Creates the intent required to navigate to the ProductListActivity
     * @param category : category or search string
     * @param url : the url used to fetch data
     * @return : Intent
     */
    private Intent getProductListIntent(String category, String url, boolean isCategory) {
        Intent productListIntent = new Intent(this, ProductListActivity.class);
        productListIntent.putExtra(Constants.ARG_CATEGORY, category);
        productListIntent.putExtra(Constants.ARG_URL, url);
        productListIntent.putExtra(Constants.ARG_IS_CATEGORY, isCategory);
        return productListIntent;
    }


    /**
     * Checks if the device is connected to the internet. If no, returns null, else makes a call to
     * the server to fetch the list of categories using an async task
     * @return : List of categories
     */
    private void fetchCategories() {
        // check internet connection
        if(!UtilityMethods.isNetworkConnected(this)){
            UtilityMethods.showShortToast(this, R.string.no_internet);
            mProgressBar.setVisibility(View.GONE);
            mEmptyTv.setText(getResources().getString(R.string.no_internet));
            return;
        }
        // fetch categories by making a server call using an async task
        FetchCategoriesTask task = new FetchCategoriesTask(this, mAdapter, mProgressBar, mEmptyTv);
        task.execute();
    }



}
