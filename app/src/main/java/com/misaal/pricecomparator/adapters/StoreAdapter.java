package com.misaal.pricecomparator.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.misaal.pricecomparator.R;
import com.misaal.pricecomparator.utilities.UtilityMethods;
import com.misaal.pricecomparator.model.Store;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Misaal on 22/04/2015.
 */
public class StoreAdapter extends BaseAdapter {


    private final Context mContext;
    private final List<Store> mStores;


    /**
     * Constructor
     * @param context : Context
     * @param storeList : List of store data
     */
    public StoreAdapter(Context context, List<Store> storeList){
        this.mContext = context;
        if(storeList == null){
            this.mStores = new ArrayList<>();
        }else{
            this.mStores = storeList;
        }
    }


    @Override
    public int getCount() {
        return mStores.size();
    }

    @Override
    public Object getItem(int i) {
        return mStores.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.store_list_item, null);
        }
        final Store store = mStores.get(position);

        ImageView logoIv = (ImageView)view.findViewById(R.id.product_store_logo_iv);
        // if connected to the internet, download and fit image into productImgIv using Picasso
        if(UtilityMethods.isNetworkConnected(mContext)){
            Picasso.with(mContext).load(store.getLogoUrl()).fit().into(logoIv);
        }

        TextView storePriceTv = (TextView)view.findViewById(R.id.product_store_price);
        storePriceTv.setText(UtilityMethods.createPriceText(store.getPrice()));

        Button buyButton = (Button)view.findViewById(R.id.product_store_buy_btn);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(store.getLink()));
                mContext.startActivity(browserIntent);
            }
        });

        return view;
    }


    /**
     *
     * @param storeList
     */
    public void addAll(List<Store> storeList){
        if(storeList != null){
            mStores.addAll(storeList);
            notifyDataSetChanged();
        }
    }
}
