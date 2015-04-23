package com.misaal.pricecomparator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.misaal.pricecomparator.R;

import java.util.ArrayList;
import java.util.List;

/** A list adapter for the listview of category names
 * Created by Misaal on 21/04/2015.
 */
public class CategoriesAdapter extends BaseAdapter {

    private List<String> categories;

    private final Context mContext;
    /**
     * Categories
     * @param context : Context
     * @param categoryList : List of categories
     */
    public CategoriesAdapter(Context context, List<String> categoryList){
        this.mContext = context;
        if(categoryList == null){
            categories = new ArrayList<>();
        }else{
            this.categories = categoryList;
        }
    }

    @Override
    public String getItem(int position) {
        return categories.get(position);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = null;
        if(view == null){
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.category_list_item, null);
        }
        // get category text
        String category = categories.get(position);
        // set it to text view
        TextView categoryTv = (TextView)view.findViewById(R.id.category_name_tv);
        categoryTv.setText(category);
        return view;
    }


    /**
     *  Adds an input list of category names to the adapter
     * @param list : a list of category names
     */
    public void addAll(List<String> list){
        categories.addAll(list);
        notifyDataSetChanged();
    }

}
