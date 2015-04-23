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
import com.misaal.pricecomparator.adapters.CategoriesAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Misaal on 21/04/2015.
 */
public class FetchCategoriesTask extends AsyncTask<Void, Void, List<String>> {

    private static final String LOG_TAG = FetchCategoriesTask.class.getSimpleName();
    private final Context mContext;

    public static final String URL = "http://api.smartprix.com/simple/v1?type=categories&key=NVgien7bb7P5Gsc8DWqc&indent=1";
    private final CategoriesAdapter mAdapter;
    private final ProgressBar mProgressBar;
    private final TextView mEmptyView;

    /**
     * Constructor
     * @param context
     */
    public FetchCategoriesTask(Context context, CategoriesAdapter adapter, ProgressBar pb, TextView emptyView){
        this.mContext = context;
        this.mAdapter = adapter;
        this.mProgressBar = pb;
        this.mEmptyView = emptyView;
    }


    @Override
    protected void onPreExecute() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCancelled() {
        mProgressBar.setVisibility(View.GONE);
        mEmptyView.setText(R.string.couldnt_get_categories);
        UtilityMethods.showLongToast(mContext, R.string.couldnt_get_categories);
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        JSONService service = new JSONService();
        String jsonStr = null;
        try {
            jsonStr = service.getJSONString(URL);
        }catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            cancel(true);
        }

        if(jsonStr != null){
            return getCategories(jsonStr);
        }

        return null;
    }


    /**
     * Reads the json string and returns a list of categories
     * @param jsonStr
     * @return
     */
    private List<String> getCategories(String jsonStr) {
        List<String> categories = new ArrayList<>();
        try {
            JSONObject reader = new JSONObject(jsonStr);
            String response = reader.getString(Constants.ARG_REQUEST_STATUS);
            if(!response.equals(Constants.RESULT_SUCCESS)){
                cancel(true);
                return null;
            }
            JSONArray categoryArray = reader.getJSONArray(Constants.ARG_REQUEST_RESULT);
            for(int i = 0 ; i < categoryArray.length(); i++){
                String category = categoryArray.getString(i);
                categories.add(category);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            cancel(true);
        }

        return categories;
    }


    @Override
    protected void onPostExecute(List<String> categories) {
        mProgressBar.setVisibility(View.GONE);
        mEmptyView.setText(R.string.no_categories);
        if(categories != null){
            mAdapter.addAll(categories);
        }

    }
}
